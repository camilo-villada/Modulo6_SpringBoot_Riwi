package com.java.eventify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVenueRequest {
	private String name;
	private String address;
	private Integer capacity;
	private String city;
}
