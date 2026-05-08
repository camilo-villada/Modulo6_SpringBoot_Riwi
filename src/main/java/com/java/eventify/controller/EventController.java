package com.java.eventify.controller;

import com.java.eventify.dto.CreateEventRequest;
import com.java.eventify.dto.EventResponse;
import com.java.eventify.model.Event;
import com.java.eventify.service.EventService;
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

@Tag(name = "Events", description = "Internal events catalog")
@RestController
@RequestMapping("/api/events")
public class EventController {
	private final EventService eventService;

	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@Operation(summary = "Create event", description = "Creates a new in-memory event")
	@PostMapping
	public ResponseEntity<EventResponse> create(@RequestBody CreateEventRequest request) {
		Event created = eventService.create(request);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.getId())
				.toUri();

		return ResponseEntity.created(location).body(toResponse(created));
	}

	@Operation(summary = "List events", description = "Returns the full list of events")
	@GetMapping
	public ResponseEntity<List<EventResponse>> getAll() {
		List<EventResponse> response = eventService.getAll().stream()
				.map(this::toResponse)
				.collect(Collectors.toList());
		return ResponseEntity.ok(response);
	}

	private EventResponse toResponse(Event event) {
		return new EventResponse(event.getId(), event.getName(), event.getDate(), event.getDescription());
	}
}
