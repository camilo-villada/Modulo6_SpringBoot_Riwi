package com.java.eventify.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.java.eventify.model.Category;
import com.java.eventify.model.Event;
import com.java.eventify.model.Venue;
import com.java.eventify.repository.CategoryRepository;
import com.java.eventify.repository.EventRepository;
import com.java.eventify.repository.VenueRepository;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:event-controller-test;DB_CLOSE_DELAY=-1;MODE=LEGACY",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class EventControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private VenueRepository venueRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() {
		eventRepository.deleteAll();
		categoryRepository.deleteAll();
		venueRepository.deleteAll();
	}

	@Test
	void createAndGetById_returnsPersistedEvent() throws Exception {
		Venue venue = venueRepository.save(venue("Grand Hall", "Bogotá", 500));
		Category category = categoryRepository.save(category("Conferences"));
		String requestBody = """
				{
				  "name": "Spring Summit",
				  "date": "2026-06-15",
				  "description": "Annual product summit",
				  "venueId": %d,
				  "categoryIds": [%d]
				}
				""".formatted(venue.getId(), category.getId());

		mockMvc.perform(post("/api/events")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/events/")))
				.andExpect(jsonPath("$.id").isNumber())
					.andExpect(jsonPath("$.name").value("Spring Summit"))
					.andExpect(jsonPath("$.date").value("2026-06-15"))
					.andExpect(jsonPath("$.description").value("Annual product summit"))
					.andExpect(jsonPath("$.venueName").value("Grand Hall"))
					.andExpect(jsonPath("$.city").value("Bogotá"));

		Event savedEvent = eventRepository.findAll().getFirst();

		mockMvc.perform(get("/api/events/{id}", savedEvent.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedEvent.getId()))
				.andExpect(jsonPath("$.name").value("Spring Summit"));
	}

	@Test
	void list_returnsPaginatedAndSortedEvents() throws Exception {
		Venue venue = venueRepository.save(venue("Grand Hall", "Bogotá", 500));
		Category category = categoryRepository.save(category("Concerts"));
		eventRepository.save(Event.builder()
				.name("Zeta Conference")
				.date(LocalDate.of(2026, 8, 10))
				.description("Last by sort order")
				.venue(venue)
				.categories(Set.of(category))
				.build());
		eventRepository.save(Event.builder()
				.name("Alpha Meetup")
				.date(LocalDate.of(2026, 5, 20))
				.description("First by sort order")
				.venue(venue)
				.categories(Set.of(category))
				.build());

		mockMvc.perform(get("/api/events")
							.param("page", "0")
							.param("size", "1"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.content", hasSize(1)))
					.andExpect(jsonPath("$.content[0].name").value("Zeta Conference"))
					.andExpect(jsonPath("$.size").value(1))
					.andExpect(jsonPath("$.number").value(0));
	}

	@Test
	void updateNonExistingId_returnsNotFound() throws Exception {
		Venue venue = venueRepository.save(venue("Grand Hall", "Bogotá", 500));
		Category category = categoryRepository.save(category("Conferences"));
		String requestBody = """
				{
				  "name": "Missing Event",
				  "date": "2026-07-01",
				  "description": "Should fail",
				  "venueId": %d,
				  "categoryIds": [%d]
				}
				""".formatted(venue.getId(), category.getId());

		mockMvc.perform(put("/api/events/{id}", 9999L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Event with id 9999 was not found"));
	}

	@Test
	void deleteExistingEvent_returnsNoContentAndRemovesRecord() throws Exception {
		Venue venue = venueRepository.save(venue("Grand Hall", "Bogotá", 500));
		Category category = categoryRepository.save(category("Concerts"));
		Event savedEvent = eventRepository.save(Event.builder()
				.name("Delete Me")
				.date(LocalDate.of(2026, 9, 1))
				.description("To be deleted")
				.venue(venue)
				.categories(Set.of(category))
				.build());

		mockMvc.perform(delete("/api/events/{id}", savedEvent.getId()))
				.andExpect(status().isNoContent());

			mockMvc.perform(get("/api/events/{id}", savedEvent.getId()))
					.andExpect(status().isNotFound());

			mockMvc.perform(get("/api/events"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.content[*].name", not(hasItem("Delete Me"))));
	}

	private Venue venue(String name, String city, int capacity) {
		return Venue.builder()
				.name(name)
				.address("123 Central Avenue")
				.capacity(capacity)
				.city(city)
				.build();
	}

	private Category category(String name) {
		return Category.builder()
				.name(name)
				.description(name + " category")
				.build();
	}
}
