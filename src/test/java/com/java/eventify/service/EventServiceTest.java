package com.java.eventify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.java.eventify.dto.CreateEventRequest;
import com.java.eventify.exception.DomainValidationException;
import com.java.eventify.model.Event;
import com.java.eventify.repository.EventRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
	@Mock
	private EventRepository eventRepository;

	@InjectMocks
	private EventService eventService;

	@Test
	void create_validRequest_savesAndReturnsCreatedEvent() {
		when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
			Event arg = invocation.getArgument(0, Event.class);
			arg.setId(1L);
			return arg;
		});

		Event created = eventService.create(new CreateEventRequest(
				"  Launch  ",
				LocalDate.of(2026, 5, 8),
				"Description"
		));

		assertNotNull(created);
		assertEquals(1L, created.getId());
		assertEquals("Launch", created.getName());
		verify(eventRepository).save(any(Event.class));
	}

	@Test
	void create_emptyName_throwsAndDoesNotCallRepository() {
		assertThrows(
				DomainValidationException.class,
				() -> eventService.create(new CreateEventRequest("   ", LocalDate.now(), null))
		);

		verifyNoInteractions(eventRepository);
	}
}
