package com.java.eventify.dto;

import java.time.LocalDate;
public record EventSummaryDTO(
		Long id,
		String name,
		LocalDate date,
		String venueName,
		String city
) {
}
