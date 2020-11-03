package com.pm.onlineshopping.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pm.onlineshopping.entity.Product;
import com.pm.onlineshopping.entity.ProductCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductDto {

	private Long id;
	private ProductCategory category;
	private Long categoryId;
	private String name;
	private String description;
	private BigDecimal unitPrice;
	private String imageUrl;
	@Getter
	private Boolean active;
	private int unitsInStock;//make it quantity
	private Long vendorId;
}
