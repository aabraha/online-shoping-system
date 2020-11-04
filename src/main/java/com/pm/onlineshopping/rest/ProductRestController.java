package com.pm.onlineshopping.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pm.onlineshopping.dto.ProductDto;
import com.pm.onlineshopping.dto.ProductSuccessResponse;
import com.pm.onlineshopping.entity.Product;
import com.pm.onlineshopping.service.ProductService;

@RestController
@RequestMapping("/")
public class ProductRestController {

	@Autowired
	private ProductService productService;
	
	@GetMapping("/products")
	public Page<Product> findAllActive(Pageable pageable){
		
		return productService.findAllActive(pageable);
	}
	
	@GetMapping("/products/{id}")
	public Product findByIdActive(@PathVariable Long id) {
		
		return productService.findByIdActive(id);
	}
	
	@PostMapping("/api/products")
	public ResponseEntity<ProductSuccessResponse> save(@RequestBody ProductDto theProduct) {
		
		
		
		return productService.save(theProduct);
	}
	
	@PutMapping("/api/products/{id}")
	public Product updateById(@RequestBody ProductDto theProduct, @PathVariable Long id) {
		
		return productService.updateById(theProduct, id);
	}
	@DeleteMapping("/api/products/{id}")
	public ResponseEntity<ProductSuccessResponse> deleteById(@PathVariable Long id){
		
		
		
		return productService.deleteById(id);
	}
	
	// =========== Additional API end points =================//
	
	@GetMapping(value = "/products/searchByCategoryId/{id}")
	public Page<Product> findByCategoryId(@PathVariable Long id, Pageable pageable){
		
		return productService.findByCategoryIdAndActiveTrue(id, pageable);
	}
	
	@GetMapping(value="/products/searchByName/{searchText}")
	public List<Product> findByName(@PathVariable String searchText){
		
		return productService.findByName(searchText);
	}
	
	@GetMapping(value = "/products/searchByCategoryName/{searchText}")
	public List<Product> findByCategoryName(@PathVariable String searchText){
		
		return productService.findByCategoryName(searchText);
	}
	
	@GetMapping(value = "/api/products/search", params = "active")
	public List<Product> findByActive(@RequestParam("active") boolean active){
		
		return productService.findByActive(active);
	}
	
	
	@GetMapping(value = "/api/products/inactive")
	public List<Product> findInactive(){
		
		return productService.findInactive();
	}
	
	@PutMapping("/api/products/approve")
	public ResponseEntity<ProductSuccessResponse> approveProducts(@RequestBody List<Long> ids){
				
		return productService.approveProducts(ids);
	}
	
	@GetMapping("/api/products/{vendorId}")
	public List<Product> findByVendorId(@PathVariable Long vendorId){
				
		return productService.findByVendorId(vendorId);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
