package com.pm.onlineshopping.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pm.onlineshopping.dto.ProductCategoryDto;
import com.pm.onlineshopping.dto.ProductSuccessResponse;
import com.pm.onlineshopping.entity.ProductCategory;
import com.pm.onlineshopping.service.ProductCategoryService;

@RestController
@RequestMapping("/")
public class ProductCategoryRestController {

	@Autowired
	private ProductCategoryService ProductCategoryService;
	
	@GetMapping("/categories")
	public List<ProductCategory> findAll(){
		
		return ProductCategoryService.findAll();
	}
	
	@GetMapping("/categories/{id}")
	public ProductCategory findById(@PathVariable Long id) {
		
		return ProductCategoryService.findById(id);
	}
	
	@PostMapping("/api/categories")
	public ResponseEntity<ProductSuccessResponse> save(@RequestBody ProductCategoryDto theCategory) {
		
		
		
		return ProductCategoryService.save(theCategory);
	}
	
	@PutMapping("/api/categories/{id}")
	public ProductCategory updateById(@RequestBody ProductCategoryDto theCategory, @PathVariable Long id) {
		
		return ProductCategoryService.updateById(theCategory, id);
	}
	
	@DeleteMapping("/api/categories/{id}")
	public ResponseEntity<ProductSuccessResponse> deleteById(@PathVariable Long id){
		
		
		
		return ProductCategoryService.deleteById(id);
	}
	
	// Additiona API end points
	@GetMapping("/categories/search")
	public ProductCategory findByCategoryName(@RequestParam String categoryName){
		
		return ProductCategoryService.findByCategoryName(categoryName);
	}
	
	
	
	
	
	
	
	
}
