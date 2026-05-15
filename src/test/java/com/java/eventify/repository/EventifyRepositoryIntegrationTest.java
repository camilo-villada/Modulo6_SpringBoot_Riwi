package com.java.eventify.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.java.eventify.model.Event;
import com.java.eventify.model.Venue;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class EventifyRepositoryIntegrationTest {
	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private VenueRepository venueRepository;

	@Test
	void eventRepository_persistsAndFiltersByName() {
		Event savedEvent = eventRepository.save(Event.builder()
				.name("Spring Festival")
				.date(LocalDate.of(2026, 6, 10))
				.description("Community tech event")
				.build());

		assertNotNull(savedEvent.getId());

		eventRepository.save(Event.builder()
				.name("Music Night")
				.date(LocalDate.of(2026, 7, 2))
				.description("Live music")
				.build());

		Page<Event> result = eventRepository.findByNameContaining("Spring", PageRequest.of(0, 10));

		assertEquals(1, result.getTotalElements());
		assertEquals("Spring Festival", result.getContent().getFirst().getName());
	}

	@Test
	void venueRepository_persistsAndFiltersByName() {
		Venue savedVenue = venueRepository.save(Venue.builder()
				.name("Grand Hall")
				.address("123 Central Avenue")
				.capacity(300)
				.build());

		assertNotNull(savedVenue.getId());

		venueRepository.save(Venue.builder()
				.name("North Patio")
				.address("45th Street")
				.capacity(120)
				.build());

		Page<Venue> result = venueRepository.findByNameContaining("Grand", PageRequest.of(0, 10));

		assertEquals(1, result.getTotalElements());
		assertEquals("Grand Hall", result.getContent().getFirst().getName());
	}
}
