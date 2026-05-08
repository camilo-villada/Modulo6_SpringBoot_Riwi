package com.java.eventify.dto;

import java.time.LocalDate;
import lombok.Value;

@Value
public class EventResponse {
	Long id;
	String name;
	LocalDate date;
	String description;
}
