package com.java.eventify.service;

import com.java.eventify.dto.VenueCreateDTO;
import com.java.eventify.dto.VenueResponseDTO;
import com.java.eventify.exception.DomainValidationException;
import com.java.eventify.exception.ResourceNotFoundException;
import com.java.eventify.mapper.VennueMapper;
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
	private final VennueMapper vennueMapper;

	public VenueService(VenueRepository venueRepository, VennueMapper vennueMapper) {
		this.venueRepository = venueRepository;
		this.vennueMapper = vennueMapper;
	}

	//create
	public VenueResponseDTO create(VenueCreateDTO request) {
		validateRequest(request);

		Venue venue = vennueMapper.toEntity(request);

		venue.setName(venue.getName().trim());
		venue.setAddress(venue.getAddress().trim());
		venue.setCity(venue.getCity().trim());

		Venue savedVenue = venueRepository.save(venue);

		return vennueMapper.toResponseDTO((savedVenue));
	}

	//getAll
	@Transactional(readOnly = true)
	public Page<VenueResponseDTO> getAll(String name, Pageable pageable) {
		if (name == null || name.trim().isEmpty()) {
			return venueRepository.findAll(pageable).map(vennueMapper::toResponseDTO);
		}
		return venueRepository.findByNameContaining(name.trim(), pageable).map(vennueMapper::toResponseDTO);
	}

	//getById
	@Transactional(readOnly = true)
	public VenueResponseDTO getById(Long id) {
		
		Venue venue = venueRepository.findById(id).orElseThrow(() -> new
		ResourceNotFoundException("Venue with id " + id + "was not found"));

		return vennueMapper.toResponseDTO(venue);
	}

	@Transactional(readOnly = true)
	public List<VenueResponseDTO> getAllForAdmin() {
		return venueRepository.findAll(Sort.by(Sort.Order.asc("name"), Sort.Order.asc("address")))
		.stream()
		.map(vennueMapper::toResponseDTO)
		.toList();
	}

	//update
	public VenueResponseDTO
	update(Long id, VenueCreateDTO request) {
		validateRequest(request);

		Venue existingVenue = venueRepository.findById(id).orElseThrow(() -> new 
		ResourceNotFoundException("Venue with id " + id + "was not found"));
		
		existingVenue.setName(request.getName().trim());
		existingVenue.setAddress(request.getAddress().trim());
		existingVenue.setCapacity(request.getCapacity());
		existingVenue.setCity(request.getCity().trim());

		Venue updatVenue = venueRepository.save(existingVenue);

		return vennueMapper.toResponseDTO(updatVenue);
	}

	public void delete(Long id) {
		Venue existingVenue = venueRepository.findById(id)
				.orElseThrow(() -> new 
				ResourceNotFoundException("Venue with id " + id + " was not found"));
		venueRepository.delete(existingVenue);
	}

	private void validateRequest(VenueCreateDTO request) {
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
