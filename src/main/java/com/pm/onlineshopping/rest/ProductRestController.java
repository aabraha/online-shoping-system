package com.pm.onlineshopping.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.pm.onlineshopping.dto.ProductDto;
import com.pm.onlineshopping.entity.Product;
import com.pm.onlineshopping.service.ProductService;

@RestController

@RequestMapping("/")
public class ProductRestController {

	@Autowired
	private ProductService productService;
	
	@GetMapping("/products")
	public Page<Product> findAll(Pageable pageable){
		
		return productService.findAllActive(pageable);
	}
	
	@GetMapping("/products/{id}")
	public Product findById(@PathVariable Long id) {
		
		return productService.findById(id);
	}
	
	@PostMapping("/api/products")
	public String save(@RequestBody ProductDto theProduct) {
		
		productService.save(theProduct);
		
		return "save successfull";
	}
	
	@PutMapping("/api/products/{id}")
	public Product updateById(@RequestBody ProductDto theProduct, @PathVariable Long id) {
		
		return productService.updateById(theProduct, id);
	}
	@DeleteMapping("/api/products/{id}")
	public String deleteById(@PathVariable Long id){
		
		productService.deleteById(id);
		
		return "deleted product with id: " + id;
	}
	
	// Additional API end points
	@GetMapping("/api/products/{vendorId}")
	public List<Product> findByVendorId(@PathVariable Long vendorId){
				
		return productService.findByVendorId(vendorId);
	}
	
	@GetMapping(value = "/products/search", params="categoryName")
	public List<Product> findByCategoryName(@RequestParam("categoryName") String categoryName){
		
		return productService.findByCategoryName(categoryName);
	}
	@GetMapping(value = "/products/search", params="categoryId")
	public List<Product> findByCategoryId(@RequestParam("categoryId") Long categoryId){
		
		return productService.findByCategoryId(categoryId);
	}
	
	@GetMapping(value = "/api/products/search", params = "active")
	public List<Product> findByActive(@RequestParam("active") boolean active){
		
		return productService.findByActive(active);
	}
	@GetMapping(value="/products/search", params = "name")
	public List<Product> findByName(@RequestParam("name") String name){
		
		return productService.findByName(name);
	}
	
	@GetMapping(value = "/api/products/inactive")
	public List<Product> findInactive(){
		
		return productService.findInactive();
	}
	
	@PutMapping("/api/products/approve")
	public List<Product> approveProducts(@RequestBody List<ProductDto> products){
		
		return productService.updateProducts(products);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
