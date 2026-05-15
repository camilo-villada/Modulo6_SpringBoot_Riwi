package com.java.eventify.controller;

import com.java.eventify.dto.CreateVenueRequest;
import com.java.eventify.dto.ApiErrorResponse;
import com.java.eventify.dto.VenueResponse;
import com.java.eventify.model.Venue;
import com.java.eventify.service.VenueService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springdoc.core.annotations.ParameterObject;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Venues", description = "Internal venues catalog")
@RestController
@RequestMapping("/api/venues")
public class VenueController {
	private final VenueService venueService;

	public VenueController(VenueService venueService) {
		this.venueService = venueService;
	}

	@Operation(summary = "Create venue", description = "Creates a persisted venue")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Venue created"),
			@ApiResponse(
					responseCode = "400",
					description = "Invalid request",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
			)
	})
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

	@Operation(summary = "List venues", description = "Returns paginated venues with optional filtering by name")
	@ApiResponse(responseCode = "200", description = "Venues page returned")
	@GetMapping
	public ResponseEntity<Page<VenueResponse>> getAll(
			@Parameter(description = "Optional partial name filter") @RequestParam(required = false) String name,
			@ParameterObject Pageable pageable
	) {
		Page<VenueResponse> response = venueService.getAll(name, pageable).map(this::toResponse);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get venue by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Venue found"),
			@ApiResponse(
					responseCode = "404",
					description = "Venue not found",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
			)
	})
	@GetMapping("/{id}")
	public ResponseEntity<VenueResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(toResponse(venueService.getById(id)));
	}

	@Operation(summary = "Update venue by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Venue updated"),
			@ApiResponse(
					responseCode = "400",
					description = "Invalid request",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "Venue not found",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
			)
	})
	@PutMapping("/{id}")
	public ResponseEntity<VenueResponse> update(@PathVariable Long id, @RequestBody CreateVenueRequest request) {
		return ResponseEntity.ok(toResponse(venueService.update(id, request)));
	}

	@Operation(summary = "Delete venue by id")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Venue deleted"),
			@ApiResponse(
					responseCode = "404",
					description = "Venue not found",
					content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
			)
	})
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		venueService.delete(id);
		return ResponseEntity.noContent().build();
	}

	private VenueResponse toResponse(Venue venue) {
		return new VenueResponse(venue.getId(), venue.getName(), venue.getAddress(), venue.getCapacity());
	}
}
