package com.pm.onlineshopping.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.pm.onlineshopping.dao.ProductCategoryRepository;
import com.pm.onlineshopping.dao.ProductRepository;
import com.pm.onlineshopping.dto.ProductDto;
import com.pm.onlineshopping.entity.Product;
import com.pm.onlineshopping.entity.ProductCategory;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductCategoryRepository productCategoryRepository;

	@Override
	public Page<Product> findAll(Pageable pageable) {
		Page<Product> products = productRepository.findAll(pageable);
		if (products.isEmpty())
			throw new ProductNotFoundException("Empty record");

		return products;
	}

	@Override
	public Product findById(Long theId) {

		Optional<Product> result = productRepository.findById(theId);

		Product theProduct = null;

		if (result.isPresent()) {
			theProduct = result.get();
		} else {
			// didn't find the Product
			throw new ProductNotFoundException("Did not find product with id: " + theId);
		}

		return theProduct;
	}

	@Override
	public void save(ProductDto theProduct) {

		Product newProduct = new Product();
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
						p.setUnitsInStock(p.getUnitsInStock() + theProduct.getQuantity());
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
		//System.out.println(p.get());
		return savedProduct;
	}


	@Override
	public void deleteById(Long theId) {

		// Check if product is available with the given id
		Optional<Product> theProduct = productRepository.findById(theId);
		if (!theProduct.isPresent()) {
			throw new ProductNotFoundException("there is no product with the id: " + theId);
		}

		productRepository.deleteById(theId);
	}

	// common method to map entity to dto
	private void dataMapping(Product newProduct, ProductDto theProduct) {
		// newProduct.setCategory(theProduct.getCategory());

		newProduct.setName(theProduct.getName());
		newProduct.setActive(false);
		newProduct.setDescription(theProduct.getDescription());
		newProduct.setUnitPrice(theProduct.getUnitPrice());
		newProduct.setImageUrl(theProduct.getImageUrl());
		newProduct.setUnitsInStock(theProduct.getQuantity());
		newProduct.setVendorId(theProduct.getVendorId());

	}
	
	private void updateMapping(Product product, ProductDto theProduct) {
		
		product.setName(theProduct.getName());
		System.out.println("Active: " + theProduct.getActive());
		product.setActive(theProduct.getActive());
		product.setDescription(theProduct.getDescription());
		product.setUnitPrice(theProduct.getUnitPrice());
		product.setImageUrl(theProduct.getImageUrl());
		product.setUnitsInStock(theProduct.getQuantity());
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

		List<Product> products = productRepository.findByCategoryCategoryName(categoryName);
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

}
