package com.java.eventify.service;

import com.java.eventify.dto.CreateEventRequest;
import com.java.eventify.exception.DomainValidationException;
import com.java.eventify.exception.ResourceNotFoundException;
import com.java.eventify.model.Event;
import java.util.List;
import com.java.eventify.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventService {
	private final EventRepository eventRepository;

	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	public Event create(CreateEventRequest request) {
		validateRequest(request);

		Event event = Event.builder()
				.name(request.getName().trim())
				.date(request.getDate())
				.description(request.getDescription().trim())
				.build();

		return eventRepository.save(event);
	}

	@Transactional(readOnly = true)
	public Page<Event> getAll(String name, Pageable pageable) {
		if (name == null || name.trim().isEmpty()) {
			return eventRepository.findAll(pageable);
		}
		return eventRepository.findByNameContaining(name.trim(), pageable);
	}

	@Transactional(readOnly = true)
	public Event getById(Long id) {
		return eventRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " was not found"));
	}

	@Transactional(readOnly = true)
	public List<Event> getAllForAdmin() {
		return eventRepository.findAll(Sort.by(Sort.Order.asc("date"), Sort.Order.asc("name")));
	}

	public Event update(Long id, CreateEventRequest request) {
		validateRequest(request);

		Event existingEvent = getById(id);
		existingEvent.setName(request.getName().trim());
		existingEvent.setDate(request.getDate());
		existingEvent.setDescription(request.getDescription().trim());

		return eventRepository.save(existingEvent);
	}

	public void delete(Long id) {
		Event existingEvent = getById(id);
		eventRepository.delete(existingEvent);
	}

	private void validateRequest(CreateEventRequest request) {
		if (request == null) {
			throw new DomainValidationException("Event payload is required");
		}
		if (request.getName() == null || request.getName().trim().isEmpty()) {
			throw new DomainValidationException("Event name must not be blank");
		}
		if (request.getDate() == null) {
			throw new DomainValidationException("Event date must not be null");
		}
		if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
			throw new DomainValidationException("Event description must not be blank");
		}
	}
}
