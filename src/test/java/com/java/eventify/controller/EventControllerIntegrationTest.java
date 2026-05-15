package com.java.eventify.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.java.eventify.model.Event;
import com.java.eventify.repository.EventRepository;
import java.time.LocalDate;
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

	@BeforeEach
	void setUp() {
		eventRepository.deleteAll();
	}

	@Test
	void createAndGetById_returnsPersistedEvent() throws Exception {
		String requestBody = """
				{
				  "name": "Spring Summit",
				  "date": "2026-06-15",
				  "description": "Annual product summit"
				}
				""";

		mockMvc.perform(post("/api/events")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/events/")))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Spring Summit"))
				.andExpect(jsonPath("$.date").value("2026-06-15"))
				.andExpect(jsonPath("$.description").value("Annual product summit"));

		Event savedEvent = eventRepository.findAll().getFirst();

		mockMvc.perform(get("/api/events/{id}", savedEvent.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedEvent.getId()))
				.andExpect(jsonPath("$.name").value("Spring Summit"));
	}

	@Test
	void list_returnsPaginatedAndSortedEvents() throws Exception {
		eventRepository.save(Event.builder()
				.name("Zeta Conference")
				.date(LocalDate.of(2026, 8, 10))
				.description("Last by sort order")
				.build());
		eventRepository.save(Event.builder()
				.name("Alpha Meetup")
				.date(LocalDate.of(2026, 5, 20))
				.description("First by sort order")
				.build());

		mockMvc.perform(get("/api/events")
						.param("page", "0")
						.param("size", "1")
						.param("sort", "name,asc"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0].name").value("Alpha Meetup"))
				.andExpect(jsonPath("$.totalElements").value(2))
				.andExpect(jsonPath("$.totalPages").value(2))
				.andExpect(jsonPath("$.size").value(1))
				.andExpect(jsonPath("$.number").value(0));
	}

	@Test
	void updateNonExistingId_returnsNotFound() throws Exception {
		String requestBody = """
				{
				  "name": "Missing Event",
				  "date": "2026-07-01",
				  "description": "Should fail"
				}
				""";

		mockMvc.perform(put("/api/events/{id}", 9999L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Event with id 9999 was not found"));
	}

	@Test
	void deleteExistingEvent_returnsNoContentAndRemovesRecord() throws Exception {
		Event savedEvent = eventRepository.save(Event.builder()
				.name("Delete Me")
				.date(LocalDate.of(2026, 9, 1))
				.description("To be deleted")
				.build());

		mockMvc.perform(delete("/api/events/{id}", savedEvent.getId()))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/events/{id}", savedEvent.getId()))
				.andExpect(status().isNotFound());
	}
}
