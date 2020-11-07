package com.pm.onlineshopping.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.cfg.Environment;
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
import com.pm.onlineshopping.dto.OrderSucceedEmailDto;
import com.pm.onlineshopping.dto.ProductDto;
import com.pm.onlineshopping.dto.ProductKafkaDto;
import com.pm.onlineshopping.dto.Vendor;
import com.pm.onlineshopping.entity.Product;

import lombok.Getter;

@Service
public class KafkaConsumer {
	@Value("${user.service}")
	public String userService;
	@Value("${email.body.customer}")
	private String emailBodyCustomer;
	@Value("${email.body.vendor}")
	private String emailBodyVendor;
	@Value("${email.from.eCommerce")
	private String emailFromECommerce;

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private KafkaTemplate<String, Long> KafkaTemplate;
	@Autowired
	private KafkaTemplate<String, OrderSucceedEmailDto> successKafkaEmailTemplate;
	private static final String TOPIC_SUCCESS = "Order-Succeed";
	private static final String TOPIC_FAILURE = "Fail-Qty-Deduction";
	private static final String TOPIC_SUCCESS_EMAIL = "Order-Succeed-Email";	
	private static final String PAYMENT = "Payment-Being-Paid";
	
	
	@KafkaListener(topics = "Payment-Being-Paid", groupId = "product_id")
	public void orderConsumer(Order order) {
		System.err.println("event payment detected");
		System.err.println("user service "+ userService);
		//KafkaTemplate.send(TOPIC_SUCCESS, Long.valueOf(order.getOrderId()));
		System.out.println("Consumed Model: " + order);
		List<ProductKafkaDto> productKafkaDtos = new ArrayList<>();
		List<Product> products = new ArrayList<>();
		//Map<Long, List<String>> vendors = new HashMap<>();
		
		productKafkaDtos = order.getProducts(); 
		if(productKafkaDtos.isEmpty()) {
			System.out.println("product empty in order");
			KafkaTemplate.send(TOPIC_FAILURE, order.getOrderId());
			return;
		}
		
		// execute quantity deduction //
		synchronized (order) {
			boolean canDeduct = true;
			for(ProductKafkaDto product : productKafkaDtos) {
				if(canDeductQuantity(product.getQuantity(), product.getProductId())) {
					canDeduct = true;
				}
				else {
					canDeduct = false;
					break;
				}
			}			
			
			if(canDeduct) {
				//execute transaction
				boolean flag = false;
				for(ProductKafkaDto product : productKafkaDtos) {
					Optional<Product> p = productRepository.findById(product.getProductId());
					if(!(p.isPresent())) {
						
						flag = true;
						break;
					}
					p.get().setUnitsInStock(p.get().getUnitsInStock() - product.getQuantity());
					
					products.add(p.get());
				}
				if(flag) {
					KafkaTemplate.send(TOPIC_FAILURE, order.getOrderId());
				}
				else {
					//compose email and send success kafka event
					productRepository.flush();
					KafkaTemplate.send(TOPIC_SUCCESS, order.getOrderId());
					composeEmail(order, products);
				}
				
				
				
			}
			else {
				// kafka message produce failure
				KafkaTemplate.send(TOPIC_FAILURE, order.getOrderId());
			}
		}	
	}
	
	private void composeEmail(Order order ,  List<Product> products) {
		
		Map<Long, Map<String, Integer>> map = new HashMap<>();
		Map<String, Integer> internalMap = new HashMap<>();
		Long vendorId;
		List<EmailDto> emailDtos = new ArrayList<>();
		System.err.println("entered user service");
		List<Vendor> users = new ArrayList<Vendor>();
		EmailDto emailDto = new EmailDto();
		emailDto.setFrom(emailFromECommerce);
		
		// retrieve users from user microservice	
		users = Arrays.asList(restTemplate.getForObject(userService, Vendor[].class));
		System.err.println("executed user service");
		
		// identify vendor id lists
		for(Product product : products) {
			vendorId = product.getVendorId();
			if(map.containsKey(vendorId)) {
				map.get(vendorId).put(product.getName(), getOrderQuantity(product.getId(), order));
			}
			else {
				internalMap.put(product.getName(), getOrderQuantity(product.getId(), order));
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
		OrderSucceedEmailDto succeed = new OrderSucceedEmailDto();
		succeed.setEmails(emailDtos);
		
		// kafka message produce success
		successKafkaEmailTemplate.send(TOPIC_SUCCESS_EMAIL, succeed);
		
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

	private Integer getOrderQuantity(Long id, Order order) {
		List<ProductKafkaDto> productKafkaDtos = order.getProducts();
		for(ProductKafkaDto productKafkaDto: productKafkaDtos) {
			if(productKafkaDto.getProductId().equals(id)) {
				return productKafkaDto.getQuantity();
				
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
	// Testing producer event Payment-Being-Paid
	/*
	 * public void producer() { Order order = new Order();
	 * 
	 * List<ProductKafkaDto> products = new ArrayList<ProductKafkaDto>();
	 * ProductKafkaDto p1 = new ProductKafkaDto(); ProductKafkaDto p2 = new
	 * ProductKafkaDto(); ProductKafkaDto p3 = new ProductKafkaDto();
	 * 
	 * 
	 * 
	 * p1.setQuantity(2); p1.setId(Long.valueOf(2));
	 * 
	 * p2.setQuantity(2); p2.setId(Long.valueOf(2));
	 * 
	 * p3.setQuantity(2); p3.setId(Long.valueOf(2));
	 * 
	 * products.add(p1); products.add(p2); products.add(p3);
	 * 
	 * order.setOrderId(Long.valueOf(1));
	 * order.setUserEmail("test@group3Ecommerce.com"); order.setProducts(products);
	 * 
	 * // this is payment producer for test KafkaTemplate.send(PAYMENT,
	 * order.getOrderId()); System.err.println("payment event generated"); }
	 */
	
}
