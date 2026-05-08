package com.java.eventify.service;

import com.java.eventify.dto.CreateEventRequest;
import com.java.eventify.exception.DomainValidationException;
import com.java.eventify.model.Event;
import com.java.eventify.repository.EventRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventService {
	private final EventRepository eventRepository;

	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	public Event create(CreateEventRequest request) {
		validateCreateRequest(request);

		Event event = Event.builder()
				.name(request.getName().trim())
				.date(request.getDate())
				.description(request.getDescription())
				.build();

		return eventRepository.save(event);
	}

	public List<Event> getAll() {
		return eventRepository.findAll();
	}

	private void validateCreateRequest(CreateEventRequest request) {
		if (request == null) {
			throw new DomainValidationException("Event payload is required");
		}
		if (request.getName() == null || request.getName().trim().isEmpty()) {
			throw new DomainValidationException("Event name must not be blank");
		}
	}
}
