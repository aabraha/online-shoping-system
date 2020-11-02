package com.pm.onlineshopping.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.pm.onlineshopping.dao.ProductRepository;
import com.pm.onlineshopping.dto.Order;
import com.pm.onlineshopping.entity.Product;


@Service
public class KafkaConsumer {

	@Autowired
	ProductRepository productRepository;
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	private static final String TOPIC = "Qty-Deduction-Status";
	
	
	@KafkaListener(topics = "Payment-Being-Paid", groupId = "product_id", containerFactory = "orderKafkaListenerFactory")
	public void orderConsumer(List<Order> orders) {
		System.out.println("Consumed Model: " + orders);
		
		// execute quantity deduction //
		synchronized (orders) {
			boolean commit = true;
			for(Order order : orders) {
				if(canDeductQuantity(order.getQuantity(), order.getId())) {
					commit = true;
				}
				else {
					commit = false;
					break;
				}
			}
			
			
			if(commit) {
				//execute transaction
				for(Order order : orders) {
					Optional<Product> p = productRepository.findById(order.getId());
					p.get().setUnitPrice(p.get().getUnitPrice().subtract(BigDecimal.valueOf(order.getQuantity())));
					if(!(p.isPresent() && productRepository.save(p.get()) != null)) {
						//there is any error on Tx roll back  
						
						//Kafka message produce failure
						kafkaTemplate.send(TOPIC, "FAILURE");
						break;
					}
				}
				// kafka message produce success
				kafkaTemplate.send(TOPIC, "SUCCESS");
			}
			else {
				// kafka message produce failure
				kafkaTemplate.send(TOPIC, "FAILURE");
			}
		}	
	}
	
	// check units in stock for an order
	synchronized public boolean canDeductQuantity(Long orderedQuantity, Long id) {
			
			int available = productRepository.findById(id).get().getUnitsInStock();
			
			if(available - orderedQuantity < 0)
				return false;
			
			return true;
		}
}
