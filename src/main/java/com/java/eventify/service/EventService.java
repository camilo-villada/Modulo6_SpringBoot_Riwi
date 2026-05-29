package com.java.eventify.service;

import com.java.eventify.dto.CreateEventRequest;
import com.java.eventify.dto.EventSummaryDTO;
import com.java.eventify.exception.DomainValidationException;
import com.java.eventify.exception.ResourceNotFoundException;
import com.java.eventify.model.Category;
import com.java.eventify.model.Event;
import com.java.eventify.model.Venue;
import com.java.eventify.repository.CategoryRepository;
import java.util.List;
import java.util.Set;
import com.java.eventify.repository.EventRepository;
import com.java.eventify.repository.VenueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventService {
	private final EventRepository eventRepository;
	private final VenueRepository venueRepository;
	private final CategoryRepository categoryRepository;

	public EventService(
			EventRepository eventRepository,
			VenueRepository venueRepository,
			CategoryRepository categoryRepository
	) {
		this.eventRepository = eventRepository;
		this.venueRepository = venueRepository;
		this.categoryRepository = categoryRepository;
	}

	public Event create(CreateEventRequest request) {
		validateRequest(request);
		Venue venue = venueRepository.findById(request.getVenueId())
				.orElseThrow(() -> new ResourceNotFoundException("Venue with id " + request.getVenueId() + " was not found"));
		List<Category> categories = categoryRepository.findByIdIn(request.getCategoryIds());
		if (categories.size() != request.getCategoryIds().size()) {
			throw new DomainValidationException("Every selected category must exist");
		}

		Event event = Event.builder()
				.name(request.getName().trim())
				.date(request.getDate())
				.description(request.getDescription().trim())
				.active(true)
				.venue(venue)
				.categories(Set.copyOf(categories))
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
		return eventRepository.findDetailedById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " was not found"));
	}

	@Transactional(readOnly = true)
	public List<Event> getAllForAdmin() {
		return eventRepository.findAllByOrderByDateDesc();
	}

	@Transactional(readOnly = true)
	public Slice<EventSummaryDTO> searchSummaries(
			String city,
			String category,
			java.time.LocalDate fromDate,
			java.time.LocalDate toDate,
			Integer minCapacity,
			Pageable pageable
	) {
		return eventRepository.searchSummaries(
				normalize(city),
				normalize(category),
				fromDate,
				toDate,
				minCapacity,
				pageable
		);
	}

	public Event update(Long id, CreateEventRequest request) {
		validateRequest(request);
		Venue venue = venueRepository.findById(request.getVenueId())
				.orElseThrow(() -> new ResourceNotFoundException("Venue with id " + request.getVenueId() + " was not found"));
		List<Category> categories = categoryRepository.findByIdIn(request.getCategoryIds());
		if (categories.size() != request.getCategoryIds().size()) {
			throw new DomainValidationException("Every selected category must exist");
		}

		Event existingEvent = getById(id);
		existingEvent.setName(request.getName().trim());
		existingEvent.setDate(request.getDate());
		existingEvent.setDescription(request.getDescription().trim());
		existingEvent.setVenue(venue);
		existingEvent.setCategories(Set.copyOf(categories));

		return eventRepository.save(existingEvent);
	}

	public void delete(Long id) {
		Event existingEvent = getById(id);
		existingEvent.deactivate();
		eventRepository.save(existingEvent);
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
		if (request.getVenueId() == null) {
			throw new DomainValidationException("Event venue is required");
		}
		if (request.getCategoryIds() == null || request.getCategoryIds().isEmpty()) {
			throw new DomainValidationException("Event must have at least one category");
		}
	}

	private String normalize(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return value.trim();
	}
}
