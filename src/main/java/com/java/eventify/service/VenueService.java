package com.java.eventify.service;

import com.java.eventify.dto.CreateVenueRequest;
import com.java.eventify.exception.DomainValidationException;
import com.java.eventify.exception.ResourceNotFoundException;
import com.java.eventify.model.Venue;
import com.java.eventify.repository.VenueRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VenueService {
	private final VenueRepository venueRepository;

	public VenueService(VenueRepository venueRepository) {
		this.venueRepository = venueRepository;
	}

	public Venue create(CreateVenueRequest request) {
		validateRequest(request);

			Venue venue = Venue.builder()
					.name(request.getName().trim())
					.address(request.getAddress().trim())
					.capacity(request.getCapacity())
					.city(request.getCity().trim())
					.build();

		return venueRepository.save(venue);
	}

	@Transactional(readOnly = true)
	public Page<Venue> getAll(String name, Pageable pageable) {
		if (name == null || name.trim().isEmpty()) {
			return venueRepository.findAll(pageable);
		}
		return venueRepository.findByNameContaining(name.trim(), pageable);
	}

	@Transactional(readOnly = true)
	public Venue getById(Long id) {
		return venueRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Venue with id " + id + " was not found"));
	}

	@Transactional(readOnly = true)
	public List<Venue> getAllForAdmin() {
		return venueRepository.findAll(Sort.by(Sort.Order.asc("name"), Sort.Order.asc("address")));
	}

	public Venue update(Long id, CreateVenueRequest request) {
		validateRequest(request);

		Venue existingVenue = getById(id);
		existingVenue.setName(request.getName().trim());
		existingVenue.setAddress(request.getAddress().trim());
		existingVenue.setCapacity(request.getCapacity());
		existingVenue.setCity(request.getCity().trim());

		return venueRepository.save(existingVenue);
	}

	public void delete(Long id) {
		Venue existingVenue = getById(id);
		venueRepository.delete(existingVenue);
	}

	private void validateRequest(CreateVenueRequest request) {
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
		if (request.getCity() == null || request.getCity().trim().isEmpty()) {
			throw new DomainValidationException("Venue city must not be blank");
		}
	}
}
