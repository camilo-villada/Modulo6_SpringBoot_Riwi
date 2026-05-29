package com.java.eventify.controller;

import com.java.eventify.dto.CreateEventRequest;
import com.java.eventify.dto.CreateVenueRequest;
import com.java.eventify.dto.EventSummaryDTO;
import com.java.eventify.exception.DomainValidationException;
import com.java.eventify.model.Event;
import com.java.eventify.model.Venue;
import com.java.eventify.service.CategoryService;
import com.java.eventify.service.EventService;
import com.java.eventify.service.VenueService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminEventController {
	private final EventService eventService;
	private final VenueService venueService;
	private final CategoryService categoryService;

	public AdminEventController(EventService eventService, VenueService venueService, CategoryService categoryService) {
		this.eventService = eventService;
		this.venueService = venueService;
		this.categoryService = categoryService;
	}

	@GetMapping
	public String adminHome(
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam(required = false) Integer minCapacity,
			@RequestParam(defaultValue = "0") int page,
			Model model
	) {
		preparePageModel(model, city, category, fromDate, toDate, minCapacity, page);
		return "admin/events";
	}

	@GetMapping("/events")
	public String showEvents(
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam(required = false) Integer minCapacity,
			@RequestParam(defaultValue = "0") int page,
			Model model
	) {
		preparePageModel(model, city, category, fromDate, toDate, minCapacity, page);
		return "admin/events";
	}

	@PostMapping("/events")
	public String createEvent(
			@ModelAttribute("eventForm") CreateEventRequest eventForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes
		) {
			if (bindingResult.hasErrors()) {
				preparePageModel(model, null, null, null, null, null, 0);
				model.addAttribute("errorMessage", "Please provide a valid event date.");
				return "admin/events";
			}

		try {
			Event createdEvent = eventService.create(eventForm);
			redirectAttributes.addFlashAttribute(
					"successMessage",
					"Event \"" + createdEvent.getName() + "\" was created successfully."
			);
			return "redirect:/admin";
			} catch (DomainValidationException ex) {
				preparePageModel(model, null, null, null, null, null, 0);
				model.addAttribute("errorMessage", ex.getMessage());
				return "admin/events";
			}
	}

	@PostMapping("/venues")
	public String createVenue(
			@ModelAttribute("venueForm") CreateVenueRequest venueForm,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes
		) {
			if (bindingResult.hasErrors()) {
				preparePageModel(model, null, null, null, null, null, 0);
				model.addAttribute("errorMessage", "Please provide a valid venue capacity.");
				return "admin/events";
			}

		try {
			Venue createdVenue = venueService.create(venueForm);
			redirectAttributes.addFlashAttribute(
					"successMessage",
					"Venue \"" + createdVenue.getName() + "\" was created successfully."
			);
			return "redirect:/admin";
			} catch (DomainValidationException ex) {
				preparePageModel(model, null, null, null, null, null, 0);
				model.addAttribute("errorMessage", ex.getMessage());
				return "admin/events";
			}
		}

	private void preparePageModel(
			Model model,
			String city,
			String category,
			LocalDate fromDate,
			LocalDate toDate,
			Integer minCapacity,
			int page
	) {
		if (!model.containsAttribute("eventForm")) {
			model.addAttribute("eventForm", new CreateEventRequest());
		}
		if (!model.containsAttribute("venueForm")) {
			model.addAttribute("venueForm", new CreateVenueRequest());
		}

		Pageable pageable = PageRequest.of(Math.max(page, 0), 20);
		Slice<EventSummaryDTO> events = eventService.searchSummaries(city, category, fromDate, toDate, minCapacity, pageable);
		Map<String, Object> filters = new HashMap<>();
		filters.put("city", city);
		filters.put("category", category);
		filters.put("fromDate", fromDate);
		filters.put("toDate", toDate);
		filters.put("minCapacity", minCapacity);

		model.addAttribute("events", events);
		model.addAttribute("venues", venueService.getAllForAdmin());
		model.addAttribute("categories", categoryService.getAll());
		model.addAttribute("filters", filters);
	}
}
