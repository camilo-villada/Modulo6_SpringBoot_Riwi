package com.java.eventify.dto;

import lombok.Value;

@Value
public class VenueResponse {
	Long id;
	String name;
	String address;
	Integer capacity;
}
