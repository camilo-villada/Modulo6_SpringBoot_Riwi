package com.java.eventify.controller;

import com.java.eventify.dto.CreateVenueRequest;
import com.java.eventify.dto.VenueResponse;
import com.java.eventify.model.Venue;
import com.java.eventify.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Venues", description = "Internal venues catalog")
@RestController
@RequestMapping("/api/venues")
public class VenueController {
	private final VenueService venueService;

	public VenueController(VenueService venueService) {
		this.venueService = venueService;
	}

	@Operation(summary = "Create venue", description = "Creates a new in-memory venue")
	@PostMapping
	public ResponseEntity<VenueResponse> create(@RequestBody CreateVenueRequest request) {
		Venue created = venueService.create(request);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.getId())
				.toUri();

		return ResponseEntity.created(location).body(toResponse(created));
	}

	@Operation(summary = "List venues", description = "Returns the full list of venues")
	@GetMapping
	public ResponseEntity<List<VenueResponse>> getAll() {
		List<VenueResponse> response = venueService.getAll().stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
		return ResponseEntity.ok(response);
	}

	private VenueResponse toResponse(Venue venue) {
		return new VenueResponse(venue.getId(), venue.getName(), venue.getAddress(), venue.getCapacity());
	}
}
