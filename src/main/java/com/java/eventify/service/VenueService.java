package com.java.eventify.service;

import com.java.eventify.dto.CreateVenueRequest;
import com.java.eventify.exception.DomainValidationException;
import com.java.eventify.model.Venue;
import com.java.eventify.repository.VenueRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VenueService {
	private final VenueRepository venueRepository;

	public VenueService(VenueRepository venueRepository) {
		this.venueRepository = venueRepository;
	}

	public Venue create(CreateVenueRequest request) {
		validateCreateRequest(request);

		Venue venue = Venue.builder()
				.name(request.getName().trim())
				.address(request.getAddress().trim())
				.capacity(request.getCapacity())
				.build();

		return venueRepository.save(venue);
	}

	public List<Venue> getAll() {
		return venueRepository.findAll();
	}

	private void validateCreateRequest(CreateVenueRequest request) {
		if (request == null) {
			throw new DomainValidationException("Venue payload is required");
		}
		if (request.getName() == null || request.getName().trim().isEmpty()) {
			throw new DomainValidationException("Venue name must not be blank");
		}
		if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
			throw new DomainValidationException("Venue address must not be blank");
		}
		if (request.getCapacity() == null || request.getCapacity() <= 0) {
			throw new DomainValidationException("Venue capacity must be greater than 0");
		}
	}
}
