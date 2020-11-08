package com.pm.onlineshopping.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.pm.onlineshopping.dto.ProductErrorResponse;
import com.pm.onlineshopping.service.ProductNotFoundException;

@ControllerAdvice
public class ProductRestExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<ProductErrorResponse> exceptionHandle( 
			ProductNotFoundException exc){
		
		ProductErrorResponse error = new ProductErrorResponse( 
				HttpStatus.NOT_FOUND.value(),
				exc.getMessage(),
				System.currentTimeMillis());
		
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	//handle the rest of exceptions
	@ExceptionHandler
	public ResponseEntity<ProductErrorResponse> exceptionHandle( 
			Exception exc){
		
		ProductErrorResponse error = new ProductErrorResponse( 
				HttpStatus.BAD_REQUEST.value(),
				"invalid input check your payload!",//exc.getMessage(),
				System.currentTimeMillis());
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	
	
	
	
	
	
}
