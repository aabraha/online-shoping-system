package com.pm.onlineshopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import com.pm.onlineshopping.service.KafkaConsumer;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableEurekaClient
public class OnlineShoppingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineShoppingSystemApplication.class, args);
	}
	
	@Bean
	   public Docket productApi() {
	      return new Docket(DocumentationType.SWAGGER_2).select()
	         .apis(RequestHandlerSelectors.basePackage("com.pm.onlineshopping")).build();
	   }
	
	// testing payment event
	public void produce() {
		KafkaConsumer payment = new KafkaConsumer();
		payment.producer();
	}
	

}
