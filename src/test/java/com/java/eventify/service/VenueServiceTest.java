package com.java.eventify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.java.eventify.dto.CreateVenueRequest;
import com.java.eventify.exception.DomainValidationException;
import com.java.eventify.model.Venue;
import com.java.eventify.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {
	@Mock
	private VenueRepository venueRepository;

	@InjectMocks
	private VenueService venueService;

	@Test
	void create_validRequest_savesAndReturnsCreatedVenue() {
		when(venueRepository.save(any(Venue.class))).thenAnswer(invocation -> {
			Venue arg = invocation.getArgument(0, Venue.class);
			arg.setId(10L);
			return arg;
		});

		Venue created = venueService.create(new CreateVenueRequest(
				"  Auditorium  ",
				"  123 Main St  ",
				500
		));

		assertNotNull(created);
		assertEquals(10L, created.getId());
		assertEquals("Auditorium", created.getName());
		assertEquals("123 Main St", created.getAddress());
		assertEquals(500, created.getCapacity());
		verify(venueRepository).save(any(Venue.class));
	}

	@Test
	void create_invalidCapacity_throwsAndDoesNotCallRepository() {
		assertThrows(
				DomainValidationException.class,
				() -> venueService.create(new CreateVenueRequest("Auditorium", "123 Main St", 0))
		);

		verifyNoInteractions(venueRepository);
	}
}
