package com.pm.onlineshopping.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.pm.onlineshopping.dto.ProductDto;
import com.pm.onlineshopping.entity.Product;

public interface ProductService {

	//public List<Product> findAll();
	//public Page<Product> findAll(Pageable pageable);
	public Page<Product> findAllActive(Pageable pageable);	
	
	public Product findById(Long theId);
	
	public void save(ProductDto theProduct);
	
	public Product updateById(ProductDto theProduct, Long id);
	
	public void deleteById(Long theId);

	public List<Product> findByVendorId(Long vendorId);

	public List<Product> findByCategoryName(String categoryName);

	public List<Product> findByActive(boolean active);

	public List<Product> findByName(String name);

	public List<Product> findByCategoryId(Long categoryId);

	public List<Product> findInactive();

	public List<Product> updateProducts(List<ProductDto> products);

	

}
