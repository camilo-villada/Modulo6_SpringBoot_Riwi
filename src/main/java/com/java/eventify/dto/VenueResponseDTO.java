package com.java.eventify.dto;

import lombok.Value;

@Value
public class VenueResponseDTO {
	Long id;
	String name;
	String address;
	Integer capacity;
	String city;
}
