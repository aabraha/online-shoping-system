package com.pm.onlineshopping.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;


//@EnableKafka //=============== commented for the time being ==============//
@Configuration

@PropertySource("application.properties")
public class KafkaConfig {

	
	@Bean
	public RecordMessageConverter converter() {
		return new StringJsonMessageConverter();
	}
	
	 // RestTemplate bean creation
	  
	@Bean
	public RestTemplate getRestTemplate() {
	  
	               return new RestTemplate();
	  }
	  
	  @Bean
	    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
	        return new PropertySourcesPlaceholderConfigurer();
	    }
	
}
