package com.pm.onlineshopping.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pm.onlineshopping.dao.ProductCategoryRepository;
import com.pm.onlineshopping.dao.ProductRepository;
import com.pm.onlineshopping.dto.ProductDto;
import com.pm.onlineshopping.dto.ProductSuccessResponse;
import com.pm.onlineshopping.entity.Product;
import com.pm.onlineshopping.entity.ProductCategory;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductCategoryRepository productCategoryRepository;

	@Override
	public Page<Product> findAllActive(Pageable pageable) {
		Page<Product> products = productRepository.findByActiveTrue(pageable);
		if (products.isEmpty())
			throw new ProductNotFoundException("Empty record");
		
		return products;
	}

	@Override
	public Product findByIdActive(Long theId) {

		Product result = productRepository.findByIdAndActiveTrue(theId);
		
		if (result== null) {
			throw new ProductNotFoundException("Did not find product with id: " + theId);
		}

		return result;
	}

	@Override
	public ResponseEntity<ProductSuccessResponse> save(ProductDto theProduct) {

		Product newProduct = new Product();
		ProductSuccessResponse success = new ProductSuccessResponse( 
				HttpStatus.ACCEPTED.value(),
				"Success",
				System.currentTimeMillis());
		dataMapping(newProduct, theProduct);

		// a. checking catID and VendorID are invalid
		// return error //how to validate vendorId
		Optional<ProductCategory> category = productCategoryRepository.findById(theProduct.getCategoryId());

		Long vendorId = theProduct.getVendorId();
		if (!category.isPresent() || (vendorId <= 0 || vendorId == null)) {

			throw new ProductNotFoundException("Invalid product");
		}

		// category_id + vendor_id valid
		else {

			List<Product> products = productRepository.findByVendorId(theProduct.getVendorId());
			if (products.isEmpty()) {
				// new one product for a new valid vendor
				newProduct.setCategory(category.get());
				Product savedProduct = productRepository.save(newProduct);

				if (savedProduct == null)
					throw new ProductNotFoundException("Not created");

			} else {

				boolean exist = false;
				for (Product p : products) {
					if (p.getName().equals(theProduct.getName())) {
						// update quantity
						exist = true;
						p.setUnitsInStock(p.getUnitsInStock() + theProduct.getUnitsInStock());
						productRepository.flush();
						break;
					}
				}

				// new product name for a valid vendor and category id
				if (!exist) {

					newProduct.setCategory(category.get());
					Product savedProduct = productRepository.save(newProduct);
					if (savedProduct == null)
						throw new ProductNotFoundException("Not created");
				}

			}
		}
		
		return new ResponseEntity<ProductSuccessResponse>(success, HttpStatus.CREATED);
	}

	@Override
	public Product updateById(ProductDto theProduct, Long theId) {

		// check if product is available with the given id
		Optional<Product> p = productRepository.findById(theId);
		if (!p.isPresent()) {
			throw new ProductNotFoundException("product does not exist");
		}

		// flush the product with the given ID
		updateMapping(p.get(), theProduct);
		Product savedProduct = productRepository.save(p.get());
		
		return savedProduct;
	}


	@Override
	public ResponseEntity<ProductSuccessResponse> deleteById(Long theId) {
		
		ProductSuccessResponse success = new ProductSuccessResponse( 
				HttpStatus.ACCEPTED.value(),
				"Success",
				System.currentTimeMillis());
		// Check if product is available with the given id
		Optional<Product> theProduct = productRepository.findById(theId);
		if (!theProduct.isPresent()) {
			throw new ProductNotFoundException("there is no product with the id: " + theId);
		}

		productRepository.deleteById(theId);
		
		return new ResponseEntity<>(success, HttpStatus.ACCEPTED);
	}

	// common method to map entity to dto
	private void dataMapping(Product newProduct, ProductDto theProduct) {
		// validation
		if (theProduct == null ||
				theProduct.getName().replaceAll(" ", "").length() <= 1 || 
				theProduct.getName() == null ||
				theProduct.getUnitPrice().doubleValue() < 0
				|| theProduct.getUnitsInStock() < 0 || 
				theProduct.getVendorId() <= 0) {
			throw new ProductNotFoundException("Invalid payload check your entry");
		}

		newProduct.setName(theProduct.getName());
		newProduct.setActive(false);
		newProduct.setDescription(theProduct.getDescription());
		newProduct.setUnitPrice(theProduct.getUnitPrice());
		newProduct.setImageUrl(theProduct.getImageUrl());
		newProduct.setUnitsInStock(theProduct.getUnitsInStock());
		newProduct.setVendorId(theProduct.getVendorId());

	}
	
	private void updateMapping(Product product, ProductDto theProduct) {
		
		// validation
		if (theProduct == null ||
				theProduct.getName().replaceAll(" ", "").length() <= 1 || 
				theProduct.getName() == null ||
				theProduct.getUnitPrice().doubleValue() < 0
				|| theProduct.getUnitsInStock() < 0 || 
				theProduct.getVendorId() <= 0) {
			throw new ProductNotFoundException("Invalid entry");
		}
		product.setName(theProduct.getName());
		product.setActive(theProduct.getActive());
		product.setDescription(theProduct.getDescription());
		product.setUnitPrice(theProduct.getUnitPrice());
		product.setImageUrl(theProduct.getImageUrl());
		
		product.setUnitsInStock(theProduct.getUnitsInStock());
		product.setVendorId(theProduct.getVendorId());
		
	}

	// Additional API end points implementation
	@Override
	public List<Product> findByVendorId(Long vendorId) {

		List<Product> products = productRepository.findByVendorId(vendorId);
		if(products.isEmpty())
			throw new ProductNotFoundException("No product found");
		
		return products;
	}

	@Override
	public List<Product> findByCategoryName(String categoryName) {

		List<Product> products = productRepository.findByCategoryCategoryNameStartsWith(categoryName);
		if(products.isEmpty())
			throw new ProductNotFoundException("No product found with category: " + categoryName);
				
		return products;
	}

	@Override
	public List<Product> findByActive(boolean active) {

		List<Product> products = productRepository.findByActive(active);
		if(products.isEmpty())
			throw new ProductNotFoundException("No product with status: " + active);
		
		return products;
	}

	@Override
	public List<Product> findByName(String name) {

		List<Product> products = productRepository.findByNameStartingWithAndActiveTrue(name);
		
		if(products.isEmpty())
			throw new ProductNotFoundException("No product with name like: " + name);
		
		return products;
	}

	@Override
	public Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable) {
		Page<Product> products = null;
		
		if(categoryId.longValue() == 0) {
			products = productRepository.findByActiveTrue(pageable);
		}
		else {
			products = productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
		}
		
		if(products.isEmpty())
			throw new ProductNotFoundException("No product with category id: " + categoryId);
				
		return products;
	}

	@Override
	public List<Product> findInactive() {

		List<Product> products = productRepository.findByActive(false);
		if(products.isEmpty())
			throw new ProductNotFoundException("No inactive product");
		
		return products;
	}

	@Override
	public ResponseEntity<ProductSuccessResponse> approveProducts(List<Long> ids) {

		List<Long> result = new ArrayList<Long>();
		ProductSuccessResponse success = new ProductSuccessResponse( 
				HttpStatus.ACCEPTED.value(),
				"Success",
				System.currentTimeMillis());
		// list of inactive ids
		for(Long i : ids) {
			Optional<Product> p = productRepository.findById(i);
			p.get().setActive(true);
			productRepository.save(p.get());
			
			result.add(i);
			
		}
		
		return new ResponseEntity<ProductSuccessResponse>(success, HttpStatus.ACCEPTED);
	}
}
