package com.pm.onlineshopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableEurekaClient
@PropertySource("classpath:application.properties")
public class OnlineShoppingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineShoppingSystemApplication.class, args);
	}
	
	
	  @Bean public Docket productApi() { return new
	  Docket(DocumentationType.SWAGGER_2).select()
	  .apis(RequestHandlerSelectors.basePackage("com.pm.onlineshopping")).build();
	  }
 
}
