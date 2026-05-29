package com.java.eventify.repository;

import com.java.eventify.model.Category;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findByNameIgnoreCase(String name);

	List<Category> findByIdIn(Collection<Long> ids);
}
