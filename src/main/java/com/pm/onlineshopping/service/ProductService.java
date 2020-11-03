package com.pm.onlineshopping.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.pm.onlineshopping.dto.ProductDto;
import com.pm.onlineshopping.dto.ProductSuccessResponse;
import com.pm.onlineshopping.entity.Product;

public interface ProductService {

	public Page<Product> findAllActive(Pageable pageable);	
	//public Product findById(Long theId);
	public ResponseEntity<ProductSuccessResponse> save(ProductDto theProduct);
	public Product updateById(ProductDto theProduct, Long id);
	
	public ResponseEntity<ProductSuccessResponse> deleteById(Long theId);
	public List<Product> findByVendorId(Long vendorId);
	public List<Product> findByCategoryName(String categoryName);
	public List<Product> findByActive(boolean active);
	public Product findByIdActive(Long id);
	
	public List<Product> findByName(String name);
	public List<Product> findInactive();
	public ResponseEntity<ProductSuccessResponse> approveProducts(List<Long> ids);
	public Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
	//public List<Product> findByCategoryId(Long categoryId);	
}
