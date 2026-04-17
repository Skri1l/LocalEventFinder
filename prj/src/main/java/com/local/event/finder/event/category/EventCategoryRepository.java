package com.local.event.finder.event.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

    boolean existsByEventIdAndCategoryId(Long eventId, Long categoryId);

    void deleteByEventIdAndCategoryId(Long eventId, Long categoryId);
}