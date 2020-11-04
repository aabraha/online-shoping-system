package com.pm.onlineshopping.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmailDto {
	
	private String to;
	private final String from = "info@shopping.com";
	private String emailBody;

}
