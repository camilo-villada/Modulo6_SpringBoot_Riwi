package com.java.eventify.controller;

import com.java.eventify.dto.EventCreateDTO;
import com.java.eventify.dto.EventResponseDTO;
import com.java.eventify.dto.EventSummaryDTO;
import com.java.eventify.service.EventService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import java.net.URI;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Events", description = "Internal events catalog")
@RestController
@RequestMapping("/api/events")
public class EventController {
	private final EventService eventService;

	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@Operation(summary = "Create event", description = "Creates a persisted event")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Event created"),
			@ApiResponse(
					responseCode = "400",
					description = "Invalid request",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))
			)
	})

	
	@PostMapping
	public ResponseEntity<EventResponseDTO> create( @Valid @RequestBody EventCreateDTO request) {
		EventResponseDTO created = eventService.create(request);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.getId())
				.toUri();

		return ResponseEntity.created(location).body(created);
	}

	@Operation(
			summary = "Search active events",
			description = "Returns active events only. Soft-deleted events are hidden automatically by the global SQL restriction."
	)
	@ApiResponse(responseCode = "200", description = "Events page returned")
	@GetMapping
	public ResponseEntity<Slice<EventSummaryDTO>> getAll(
				@Parameter(description = "Partial city filter, case-insensitive") @RequestParam(required = false) String city,
				@Parameter(description = "Partial category name filter, case-insensitive") @RequestParam(required = false) String category,
				@Parameter(description = "Start date filter, ISO format yyyy-MM-dd")
				@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
				@Parameter(description = "End date filter, ISO format yyyy-MM-dd")
				@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
				@Parameter(description = "Minimum venue capacity") @RequestParam(required = false) Integer minCapacity,
				@ParameterObject Pageable pageable
		) {
			Slice<EventSummaryDTO> response = eventService.searchSummaries(city, category, fromDate, toDate, minCapacity, pageable);
			return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get event by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Event found"),
			@ApiResponse(
					// Add proper response details using ProblemDetail
					responseCode = "404",
					description = "Event not found",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))
			)
	})
	@GetMapping("/{id}")
	public ResponseEntity<EventResponseDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(eventService.getById(id));
	}

	@Operation(summary = "Update event by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Event updated"),
			@ApiResponse(
					responseCode = "400",
					description = "Invalid request",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "Event not found",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))
			)
	})

	@PutMapping("/{id}")
	public ResponseEntity<EventResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EventCreateDTO request) {
		return ResponseEntity.ok(eventService.update(id, request));
	}

	@Operation(summary = "Soft delete event by id", description = "Deactivates the event instead of physically deleting it.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Event deleted"),
			@ApiResponse(
					responseCode = "404",
					description = "Event not found",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))
			)
	})
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		eventService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
