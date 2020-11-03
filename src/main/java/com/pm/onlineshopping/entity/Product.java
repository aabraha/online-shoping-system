package com.pm.onlineshopping.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
@Table(name="product")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	//@JsonManagedReference
	@ManyToOne(cascade = CascadeType.PERSIST)
	private ProductCategory category;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "unit_price")
	private BigDecimal unitPrice;
	
	@Column(name = "image_url")
	private String imageUrl;
	
	@Column(name = "active", columnDefinition = "boolean default false")
	private boolean active;
	
	@Column(name = "units_in_stock")
	private int unitsInStock;
	
	@Column(name = "date_created")
	@CreationTimestamp
	private Date dateCreated;
	
	@Column(name = "last_updated")
	@UpdateTimestamp
	private Date lastUpdated;
	
	@Column(name = "vendor_id", nullable=false)
	private Long vendorId;
	
}
