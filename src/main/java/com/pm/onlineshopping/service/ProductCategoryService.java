package com.pm.onlineshopping.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.pm.onlineshopping.dto.ProductCategoryDto;
import com.pm.onlineshopping.dto.ProductSuccessResponse;
import com.pm.onlineshopping.entity.Product;
import com.pm.onlineshopping.entity.ProductCategory;

public interface ProductCategoryService {

	public List<ProductCategory> findAll();
	
	public ProductCategory findById(Long theId);
	
	public ResponseEntity<ProductSuccessResponse> save(ProductCategoryDto theCategory);
	
	public ProductCategory updateById(ProductCategoryDto theCategory, Long id);
	
	public ResponseEntity<ProductSuccessResponse> deleteById(Long theId);

	public ProductCategory findByCategoryName(String name);
}
