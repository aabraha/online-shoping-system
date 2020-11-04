package com.pm.onlineshopping.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderSucceedDto {

	private Long orderId;
	private List<EmailDto> emails;
}