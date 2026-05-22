package com.java.eventify.controller;

import com.java.eventify.dto.CreateEventRequest;
import com.java.eventify.dto.CreateVenueRequest;
import com.java.eventify.exception.DomainValidationException;
import com.java.eventify.model.Event;
import com.java.eventify.model.Venue;
import com.java.eventify.service.EventService;
import com.java.eventify.service.VenueService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminEventController {
	private final EventService eventService;
	private final VenueService venueService;

	public AdminEventController(EventService eventService, VenueService venueService) {
		this.eventService = eventService;
		this.venueService = venueService;
	}

	@GetMapping
	public String adminHome(Model model) {
		preparePageModel(model);
		return "admin/events";
	}

	@GetMapping("/events")
	public String showEvents(Model model) {
		preparePageModel(model);
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
			preparePageModel(model);
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
			preparePageModel(model);
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
			preparePageModel(model);
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
			preparePageModel(model);
			model.addAttribute("errorMessage", ex.getMessage());
			return "admin/events";
		}
	}

	private void preparePageModel(Model model) {
		if (!model.containsAttribute("eventForm")) {
			model.addAttribute("eventForm", new CreateEventRequest());
		}
		if (!model.containsAttribute("venueForm")) {
			model.addAttribute("venueForm", new CreateVenueRequest());
		}

		List<Event> events = eventService.getAllForAdmin();
		List<Venue> venues = venueService.getAllForAdmin();
		model.addAttribute("events", events);
		model.addAttribute("venues", venues);
	}
}
