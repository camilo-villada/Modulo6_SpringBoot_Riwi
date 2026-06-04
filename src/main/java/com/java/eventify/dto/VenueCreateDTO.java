package com.java.eventify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VenueCreateDTO {

	@NotBlank(message = "The name is required")
	private String name;

	@NotBlank(message = "The address is required")
	private String address;

	@NotNull(message = "The capacity is required")
	private Integer capacity;

	@NotBlank(message = "The city is required")
	private String city;
}
