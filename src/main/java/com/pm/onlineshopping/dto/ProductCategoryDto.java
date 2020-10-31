package com.pm.onlineshopping.dto;

import java.util.List;

import com.pm.onlineshopping.entity.Product;
import com.pm.onlineshopping.entity.ProductCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductCategoryDto {

	private String categoryName;
	private List<Product> products;


}
