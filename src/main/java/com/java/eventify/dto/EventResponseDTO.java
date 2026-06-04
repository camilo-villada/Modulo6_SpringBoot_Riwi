package com.java.eventify.dto;

import java.time.LocalDate;
import java.util.Set;
import lombok.Value;

@Value
public class EventResponseDTO {
	Long id;
	String name;
	LocalDate date;
	String description;
	String venueName;
	Set<String> categoryNames;
}
