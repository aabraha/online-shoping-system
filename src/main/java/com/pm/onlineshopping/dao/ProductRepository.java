package com.pm.onlineshopping.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pm.onlineshopping.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
