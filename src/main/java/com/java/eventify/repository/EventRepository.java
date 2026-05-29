package com.java.eventify.repository;

import com.java.eventify.dto.EventSummaryDTO;
import com.java.eventify.model.Event;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {
	Page<Event> findByNameContaining(String name, Pageable pageable);

	@EntityGraph(attributePaths = {"venue", "categories"})
	List<Event> findAllByOrderByDateDesc();

	@EntityGraph(attributePaths = {"venue", "categories"})
	@Query("select e from Event e where e.id = :id")
	Optional<Event> findDetailedById(@Param("id") Long id);

	@Query("""
			select new com.java.eventify.dto.EventSummaryDTO(
				e.id, e.name, e.date, v.name, v.city
			)
			from Event e
			join e.venue v
			left join e.categories c
			where (:city is null or lower(v.city) like lower(concat('%', :city, '%')))
			  and (:category is null or lower(c.name) like lower(concat('%', :category, '%')))
			  and (:fromDate is null or e.date >= :fromDate)
			  and (:toDate is null or e.date <= :toDate)
			  and (:minCapacity is null or v.capacity >= :minCapacity)
			group by e.id, e.name, e.date, v.name, v.city
			order by e.date desc
			""")
	Slice<EventSummaryDTO> searchSummaries(
			@Param("city") String city,
			@Param("category") String category,
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate,
			@Param("minCapacity") Integer minCapacity,
			Pageable pageable
	);

	Slice<Event> findByVenueCityContainingIgnoreCaseOrderByDateDesc(String city, Pageable pageable);

	Slice<Event> findByDateBetweenOrderByDateDesc(LocalDate fromDate, LocalDate toDate, Pageable pageable);
}
