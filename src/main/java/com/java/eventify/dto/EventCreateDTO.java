package com.java.eventify.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.java.eventify.validation.NoPastEvents;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateDTO {

	@NotBlank(message = "The name is required")
	private String name;

	@NoPastEvents(message = "The event date must be today or in the future")
	@NotNull(message = "The Date is required")
	private LocalDate date;

	@NotBlank(message = "The Description is required")
	private String description;

	@NotNull(message = "The Venue is required")
	private Long venueId;

	@NotEmpty
	private Set<Long> categoryIds = new HashSet<>();
}
