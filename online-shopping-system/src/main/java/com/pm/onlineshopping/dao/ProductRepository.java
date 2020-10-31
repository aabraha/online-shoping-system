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
@CrossOrigin
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByVendorId(Long vendorId);

	Product save(Optional<Product> product);

	List<Product> findByCategoryCategoryName(String categoryName);

	List<Product> findByActive(boolean active);
	
	Page<Product> findAll(Pageable pageable);


}
