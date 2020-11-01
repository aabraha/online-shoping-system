package com.pm.onlineshopping.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.pm.onlineshopping.entity.Product;

import net.bytebuddy.asm.Advice.OffsetMapping.Sort;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByVendorId(Long vendorId);

	Product save(Optional<Product> product);

	List<Product> findByCategoryCategoryName(String categoryName);

	List<Product> findByActive(boolean active);
	
	Page<Product> findAll(Pageable pageable);

	List<Product> findByNameLike(String name);

	List<Product> findByCategoryId(Long categoryId);

	Page<Product> findByActive(boolean b, Pageable pageable);
	
}
