package com.pm.onlineshopping.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pm.onlineshopping.dao.ProductCategoryRepository;
import com.pm.onlineshopping.dao.ProductRepository;
import com.pm.onlineshopping.dto.ProductCategoryDto;
import com.pm.onlineshopping.dto.ProductDto;
import com.pm.onlineshopping.dto.ProductSuccessResponse;
import com.pm.onlineshopping.entity.Product;
import com.pm.onlineshopping.entity.ProductCategory;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductCategoryRepository productCategoryRepository;
		
	@Override
	public List<ProductCategory> findAll() {
				
		return productCategoryRepository.findAll();
	}

	@Override
	public ProductCategory findById(Long theId) {
		
		Optional<ProductCategory> result = productCategoryRepository.findById(theId);
		
		ProductCategory theProductCategory = null;
		
		if(result.isPresent()) {
			theProductCategory = result.get();
		} 
		else {
			// didn't find the ProductCategory
			 throw new ProductNotFoundException("Did not find category id - " + theId);
		}
		
		return theProductCategory;
	}

	@Override
	public ResponseEntity<ProductSuccessResponse> save(ProductCategoryDto theProductCategory) {
		
		ProductCategory productCategory = new ProductCategory();
		ProductSuccessResponse success = new ProductSuccessResponse( 
				HttpStatus.ACCEPTED.value(),
				"Success",
				System.currentTimeMillis());
		
		if(theProductCategory == null || theProductCategory.getCategoryName() == null ||
				theProductCategory.getCategoryName().replaceAll(" ","").length() <= 1 ) {
			System.out.println("categories");
			throw new ProductNotFoundException("Category name should be non empty");
		}
		//check if the role exists by name if so response role exists and return;
		if(productCategoryRepository.findByCategoryName(theProductCategory.getCategoryName())!= null) {
			throw new ProductNotFoundException("Category Exists");
		}
		else {
			productCategory.setCategoryName(theProductCategory.getCategoryName());
			productCategory.setProducts(theProductCategory.getProducts());
						
			productCategoryRepository.save(productCategory);
		}
		
		return new ResponseEntity<ProductSuccessResponse>(success, HttpStatus.CREATED);
	}

	@Override
	public ProductCategory updateById(ProductCategoryDto theProductCategory, Long theId) {
		
		Optional<ProductCategory> category = productCategoryRepository.findById(theId);
		
		// check if product is available with the given id
		if(!category.isPresent()) {
			throw new ProductNotFoundException("Category does not exist with id: " + theId);
		}
		
		category.get().setCategoryName(theProductCategory.getCategoryName());
		//category.get().setProducts(theProductCategory.getProducts());
		
		 //productRepository.flush();
		 productCategoryRepository.flush();
			 
		return category.get();
	}

	@Override
	public ResponseEntity<ProductSuccessResponse> deleteById(Long theId) {
		
		ProductSuccessResponse success = new ProductSuccessResponse( 
				HttpStatus.ACCEPTED.value(),
				"Success",
				System.currentTimeMillis());
		// Check if product is available with the given id
		Optional<ProductCategory> theProductCategory = productCategoryRepository.findById(theId);
		if(!theProductCategory.isPresent()) {
			throw new ProductNotFoundException("there is no product with the id: " + theId);
		}
		
		productCategoryRepository.deleteById(theId);
		
		return new ResponseEntity<ProductSuccessResponse>(success, HttpStatus.ACCEPTED);
	}

	// Additional API end point implementation
	@Override
	public ProductCategory findByCategoryName(String name) {

		ProductCategory category = productCategoryRepository.findByCategoryNameStartsWith(name);
		if(category == null)
			throw new ProductNotFoundException("Category not found with name: " + name);
		
		return category;
	}
	
}
