package com.java.eventify.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
	private String name;
	private LocalDate date;
	private String description;
	private Long venueId;
	private Set<Long> categoryIds = new HashSet<>();
}
