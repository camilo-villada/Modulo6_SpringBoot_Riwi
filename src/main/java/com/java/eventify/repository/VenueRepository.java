package com.java.eventify.repository;

import com.java.eventify.model.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
	Page<Venue> findByNameContaining(String name, Pageable pageable);

	Page<Venue> findByCityContainingIgnoreCase(String city, Pageable pageable);
}
