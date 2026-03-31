package com.local.event.finder.repository;

import com.local.event.finder.model.entity.Event;
import com.local.event.finder.model.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByTitle(String title);

    boolean existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitude(
            String title,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Double latitude,
            Double longitude
    );

    List<Event> findByEventCategoryId(Long categoryId);

    List<Event> findByLocation(Double latitude, Double longitude, String country, String city);

    List<Event> findByEventTagId(Long tagId);
}
