package com.java.eventify.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:admin-event-controller-test;DB_CLOSE_DELAY=-1;MODE=LEGACY",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class AdminEventControllerIntegrationTest {
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
		venueRepository.deleteAll();
		categoryRepository.deleteAll();
	}

	@Test
	void getAdminPanel_returnsHtmlViewWithEventsVenuesAndForms() throws Exception {
		Venue venue = venueRepository.save(Venue.builder()
					.name("Grand Hall")
					.address("123 Central Avenue")
					.capacity(500)
					.city("Bogotá")
					.build());
		Category category = categoryRepository.save(Category.builder()
				.name("Conferences")
				.description("Professional talks")
				.build());
		eventRepository.save(Event.builder()
					.name("Spring Summit")
					.date(LocalDate.of(2026, 6, 15))
					.description("Annual product summit")
					.venue(venue)
					.categories(Set.of(category))
					.build());

		mockMvc.perform(get("/admin"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/events"))
				.andExpect(model().attributeExists("events"))
					.andExpect(model().attributeExists("venues"))
					.andExpect(model().attributeExists("categories"))
					.andExpect(model().attributeExists("eventForm"))
					.andExpect(model().attributeExists("venueForm"))
					.andExpect(content().string(containsString("<table")))
					.andExpect(content().string(containsString("Spring Summit")))
					.andExpect(content().string(containsString("Grand Hall")));
	}

	@Test
	void getAdminPanel_whenEmpty_returnsHtmlViewWithExactEmptyState() throws Exception {
		mockMvc.perform(get("/admin"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/events"))
					.andExpect(model().attributeExists("events"))
					.andExpect(model().attributeExists("venues"))
					.andExpect(model().attributeExists("categories"))
					.andExpect(model().attributeExists("eventForm"))
					.andExpect(model().attributeExists("venueForm"))
					.andExpect(content().string(containsString("Actualmente no hay eventos programados")));
	}

	@Test
	void createEvent_redirectsAfterSuccessfulPost() throws Exception {
		Venue venue = venueRepository.save(Venue.builder()
				.name("Grand Hall")
				.address("123 Central Avenue")
				.capacity(500)
				.city("Bogotá")
				.build());
		Category category = categoryRepository.save(Category.builder()
				.name("Workshops")
				.description("Hands-on sessions")
				.build());

		mockMvc.perform(post("/admin/events")
							.param("name", "Architecture Day")
							.param("date", "2026-08-20")
							.param("description", "Internal architecture workshop")
							.param("venueId", venue.getId().toString())
							.param("categoryIds", category.getId().toString()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin"))
				.andExpect(flash().attributeExists("successMessage"));

		mockMvc.perform(get("/api/events"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Architecture Day")));
	}

	@Test
	void createEvent_withBlankName_returnsSameViewWithError() throws Exception {
		mockMvc.perform(post("/admin/events")
							.param("name", " ")
							.param("date", "2026-08-20")
							.param("description", "Internal architecture workshop")
							.param("venueId", "1")
							.param("categoryIds", "1"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/events"))
				.andExpect(model().attributeExists("events"))
				.andExpect(model().attributeExists("venues"))
				.andExpect(model().attributeExists("eventForm"))
				.andExpect(model().attributeExists("venueForm"))
				.andExpect(model().attributeExists("errorMessage"))
				.andExpect(content().string(containsString("Event name must not be blank")));
	}

	@Test
	void createVenue_redirectsAfterSuccessfulPost() throws Exception {
		mockMvc.perform(post("/admin/venues")
							.param("name", "North Patio")
							.param("address", "45th Street")
							.param("capacity", "120")
							.param("city", "Medellín"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/admin"))
				.andExpect(flash().attributeExists("successMessage"));

		mockMvc.perform(get("/api/venues"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("North Patio")));
	}

	@Test
	void getAdminEventsRoute_stillReturnsUnifiedView() throws Exception {
		mockMvc.perform(get("/admin/events"))
				.andExpect(status().isOk())
				.andExpect(view().name("admin/events"))
					.andExpect(model().attributeExists("events"))
					.andExpect(model().attributeExists("venues"));
	}
}
