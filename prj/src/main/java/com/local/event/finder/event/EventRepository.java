package com.local.event.finder.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findAllByTitle(String title);

    boolean existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitude(
            String title,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Double latitude,
            Double longitude
    );

    boolean existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitudeAndIdNot(
            String title,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Double latitude,
            Double longitude,
            Long id
    );

    List<Event> findByEventCategory_Category_Id(Long categoryId);

    List<Event> findByLatitudeAndLongitudeAndCountryAndCity(Double latitude, Double longitude, String country, String city);

    List<Event> findByEventTags_Tag_Id(Long tagId);

    List<Event> findByCreatedById(Long userId);
}
