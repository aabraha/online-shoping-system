package com.pm.onlineshopping.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pm.onlineshopping.config.KafkaConfig;
import com.pm.onlineshopping.dao.ProductRepository;
import com.pm.onlineshopping.dto.EmailDto;
import com.pm.onlineshopping.dto.Order;
import com.pm.onlineshopping.dto.OrderSucceedDto;
import com.pm.onlineshopping.dto.ProductDto;
import com.pm.onlineshopping.dto.Vendor;
import com.pm.onlineshopping.entity.Product;


@Service
//@PropertySource("classpath:application.properties")
public class KafkaConsumer {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private KafkaTemplate<String, Order> failureKafkaTemplate;
	@Autowired
	private KafkaTemplate<String, OrderSucceedDto> successKafkaTemplate;
	private static final String TOPIC_SUCCESS = "Fail-Qty-Deduction ";
	private static final String TOPIC_FAILURE = "Order-Succeed";
	
	KafkaConfig config = new KafkaConfig();
	private String urlUser = config.getUrlUser();
	private String emailBodyCustomer = config.getEmailBodyCustomer();
	private String emailBodyVendor = config.getEmailBodyVendor();
	private String emailFromECommerce = config.getEmailFromECommerce();
	
	@KafkaListener(topics = "Payment-Being-Paid", groupId = "product_id", containerFactory = "orderKafkaListenerFactory")
	public void orderConsumer(Order order) {
		System.out.println("Consumed Model: " + order);
		List<ProductDto> productDtos = new ArrayList<>();
		List<Product> products = new ArrayList<>();
		//Map<Long, List<String>> vendors = new HashMap<>();
		
		productDtos = order.getProducts(); 
		if(productDtos.isEmpty()) {
			failureKafkaTemplate.send(TOPIC_FAILURE, order);
			return;
		}
		
		// execute quantity deduction //
		synchronized (order) {
			boolean commit = true;
			for(ProductDto product : productDtos) {
				if(canDeductQuantity(product.getUnitsInStock(), product.getId())) {
					commit = true;
				}
				else {
					commit = false;
					break;
				}
			}			
			
			if(commit) {
				//execute transaction
				for(ProductDto product : productDtos) {
					Optional<Product> p = productRepository.findById(product.getId());
					p.get().setUnitPrice(p.get().getUnitPrice().subtract(BigDecimal.valueOf(product.getUnitsInStock())));
					if(!(p.isPresent() && productRepository.save(p.get()) != null)) {
						//there is any error on Tx roll back  
						
						//Kafka message produce failure
						failureKafkaTemplate.send(TOPIC_FAILURE, order);
						break;
					}
					products.add(p.get());
				}
				//compose email and send success kafka event
				composeEmail(order, products);
				
				
			}
			else {
				// kafka message produce failure
				failureKafkaTemplate.send(TOPIC_FAILURE, order);
			}
		}	
	}
	
	private void composeEmail(Order order ,  List<Product> products) {
		
		Map<Long, Map<String, Integer>> map = new HashMap<>();
		Map<String, Integer> internalMap = new HashMap<>();
		Long vendorId;
		List<EmailDto> emailDtos = new ArrayList<>();
		EmailDto emailDto = new EmailDto();
		emailDto.setFrom(emailFromECommerce);
		
		// retrieve users from user microservice	
		List<Vendor> users = Arrays.asList(restTemplate.getForObject(urlUser, Vendor[].class));
		// identify vendor id lists
		for(Product product : products) {
			vendorId = product.getVendorId();
			if(map.containsKey(vendorId)) {
				map.get(vendorId).put(product.getName(), getOrderQuantity(product.getName(), order));
			}
			else {
				internalMap.put(product.getName(), getOrderQuantity(product.getName(), order));
				map.put(vendorId, internalMap);
			}
		}
		// receive vendor email === iterate through the vendorId key set and compose email
		for(Map.Entry<Long, Map<String, Integer>> entry : map.entrySet()) {
			emailDto.setTo(getCustomerEmail(entry.getKey(), users));
			internalMap = entry.getValue();
			emailDto.setEmailBody(emailBodyVendor +"\n" + internalMap);

			emailDtos.add(emailDto);
		}
		// add customer email compose
		emailDto.setTo(order.getUserEmail());
		emailDto.setEmailBody(emailBodyCustomer + "\n" + order.getProducts());
		
		emailDtos.add(emailDto);
		
		// construct success payload
		OrderSucceedDto succeed = new OrderSucceedDto();
		succeed.setEmails(emailDtos);
		succeed.setOrderId(order.getOrderId());
		// kafka message produce success
		successKafkaTemplate.send(TOPIC_SUCCESS, succeed);
	}

	private String getCustomerEmail(Long key, List<Vendor> users) {
		/*
		 * for(Vendor vendor : users) { if(vendor.getId().equals(key)) { return
		 * vendor.getEmail(); } }
		 */
		return users.parallelStream()
				.filter(u -> u.getId().equals(key))
				.map(u -> u.getEmail().toString())
				.findFirst().get();
	}

	private Integer getOrderQuantity(String name, Order order) {
		List<ProductDto> productDtos = order.getProducts();
		for(ProductDto productDto: productDtos) {
			if(productDto.getName().equals(name)) {
				return productDto.getUnitsInStock();
				
			}
		}
		return null;
	}

	// check units in stock for a product in an order
	public boolean canDeductQuantity(int orderedQuantity, Long id) {
			
			int available = productRepository.findById(id).get().getUnitsInStock();
			
			if(available - orderedQuantity < 0)
				return false;
			
			return true;
		}
}
