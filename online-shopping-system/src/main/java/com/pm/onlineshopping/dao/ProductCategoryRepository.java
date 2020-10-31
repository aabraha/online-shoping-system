package com.pm.onlineshopping.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.pm.onlineshopping.entity.ProductCategory;

@Repository
@CrossOrigin
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

	public ProductCategory findByCategoryName(String categoryName);

}
