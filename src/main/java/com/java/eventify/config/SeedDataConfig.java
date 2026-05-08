package com.java.eventify.config;

import com.java.eventify.dto.CreateEventRequest;
import com.java.eventify.dto.CreateVenueRequest;
import com.java.eventify.service.EventService;
import com.java.eventify.service.VenueService;
import java.time.LocalDate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {
	private final EventService eventService;
	private final VenueService venueService;

	public SeedDataConfig(EventService eventService, VenueService venueService) {
		this.eventService = eventService;
		this.venueService = venueService;
	}

	@Bean
	@ConditionalOnProperty(prefix = "eventify.seeder", name = "enabled", havingValue = "true")
	public ApplicationRunner seedData() {
		return args -> {
			venueService.create(new CreateVenueRequest("Main Auditorium", "123 Main St", 500));
			venueService.create(new CreateVenueRequest("North Hall", "456 North Ave", 120));

			eventService.create(new CreateEventRequest(
					"Eventify Launch",
					LocalDate.now().plusDays(7),
					"Internal seed event"
			));
		};
	}
}
