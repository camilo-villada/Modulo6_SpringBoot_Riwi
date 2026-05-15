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

import com.java.eventify.model.Venue;
import com.java.eventify.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:venue-controller-test;DB_CLOSE_DELAY=-1;MODE=LEGACY",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class VenueControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private VenueRepository venueRepository;

	@BeforeEach
	void setUp() {
		venueRepository.deleteAll();
	}

	@Test
	void createAndGetById_returnsPersistedVenue() throws Exception {
		String requestBody = """
				{
				  "name": "Grand Hall",
				  "address": "123 Central Avenue",
				  "capacity": 500
				}
				""";

		mockMvc.perform(post("/api/venues")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", containsString("/api/venues/")))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").value("Grand Hall"))
				.andExpect(jsonPath("$.address").value("123 Central Avenue"))
				.andExpect(jsonPath("$.capacity").value(500));

		Venue savedVenue = venueRepository.findAll().getFirst();

		mockMvc.perform(get("/api/venues/{id}", savedVenue.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedVenue.getId()))
				.andExpect(jsonPath("$.name").value("Grand Hall"));
	}

	@Test
	void list_returnsPaginatedAndSortedVenues() throws Exception {
		venueRepository.save(Venue.builder()
				.name("Zen Garden")
				.address("99 South Street")
				.capacity(80)
				.build());
		venueRepository.save(Venue.builder()
				.name("Alpha Center")
				.address("11 North Street")
				.capacity(250)
				.build());

		mockMvc.perform(get("/api/venues")
						.param("page", "0")
						.param("size", "1")
						.param("sort", "name,asc"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0].name").value("Alpha Center"))
				.andExpect(jsonPath("$.totalElements").value(2))
				.andExpect(jsonPath("$.totalPages").value(2))
				.andExpect(jsonPath("$.size").value(1))
				.andExpect(jsonPath("$.number").value(0));
	}

	@Test
	void deleteNonExistingId_returnsNotFound() throws Exception {
		mockMvc.perform(delete("/api/venues/{id}", 9999L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Venue with id 9999 was not found"));
	}

	@Test
	void updateExistingVenue_returnsUpdatedPayload() throws Exception {
		Venue savedVenue = venueRepository.save(Venue.builder()
				.name("Old Venue")
				.address("Old Address")
				.capacity(100)
				.build());

		String requestBody = """
				{
				  "name": "Updated Venue",
				  "address": "500 New Avenue",
				  "capacity": 350
				}
				""";

		mockMvc.perform(put("/api/venues/{id}", savedVenue.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedVenue.getId()))
				.andExpect(jsonPath("$.name").value("Updated Venue"))
				.andExpect(jsonPath("$.address").value("500 New Avenue"))
				.andExpect(jsonPath("$.capacity").value(350));
	}
}
