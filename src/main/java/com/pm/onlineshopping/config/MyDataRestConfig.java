package com.pm.onlineshopping.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;

import com.pm.onlineshopping.entity.Product;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer{

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {

		HttpMethod[] theUnsupportedActions = {HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE};
		
		// disable HTTP methods for product: POST, PUT and DELETE
		config.getExposureConfiguration()
				.forDomainType(Product.class)
				.withItemExposure((metadata, httpMethods)->httpMethods.disable(theUnsupportedActions))
				.withCollectionExposure((metadata, httpMethods)->httpMethods.disable(theUnsupportedActions));
						
	}

	
}
