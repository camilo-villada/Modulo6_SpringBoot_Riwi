package com.java.eventify.repository;

import com.java.eventify.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
	Page<Event> findByNameContaining(String name, Pageable pageable);
}
