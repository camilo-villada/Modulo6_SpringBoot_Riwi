package com.java.eventify.repository;

import com.java.eventify.model.Venue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class VenueRepository {
	private final Map<Long, Venue> venuesById = new ConcurrentHashMap<>();
	private final AtomicLong idSequence = new AtomicLong(0);

	public Venue save(Venue venue) {
		if (venue.getId() == null) {
			venue.setId(idSequence.incrementAndGet());
		}
		venuesById.put(venue.getId(), venue);
		return venue;
	}

	public List<Venue> findAll() {
		return new ArrayList<>(venuesById.values());
	}
}
