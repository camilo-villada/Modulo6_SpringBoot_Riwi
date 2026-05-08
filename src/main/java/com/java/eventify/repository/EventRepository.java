package com.java.eventify.repository;

import com.java.eventify.model.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepository {
	private final Map<Long, Event> eventsById = new ConcurrentHashMap<>();
	private final AtomicLong idSequence = new AtomicLong(0);

	public Event save(Event event) {
		if (event.getId() == null) {
			event.setId(idSequence.incrementAndGet());
		}
		eventsById.put(event.getId(), event);
		return event;
	}

	public List<Event> findAll() {
		return new ArrayList<>(eventsById.values());
	}
}
