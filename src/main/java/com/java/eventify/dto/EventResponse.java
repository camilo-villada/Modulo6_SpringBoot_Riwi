package com.java.eventify.dto;

import java.time.LocalDate;
import java.util.Set;
import lombok.Value;

@Value
public class EventResponse {
	Long id;
	String name;
	LocalDate date;
	String description;
	Boolean active;
	Long venueId;
	String venueName;
	String city;
	Set<String> categories;
}
