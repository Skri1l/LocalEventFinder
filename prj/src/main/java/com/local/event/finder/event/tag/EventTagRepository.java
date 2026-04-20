package com.local.event.finder.event.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTagRepository extends JpaRepository<EventTag, Long> {

    boolean existsByEventIdAndTagId(Long eventId, Long tagId);

    void deleteByEventIdAndTagId(Long eventId, Long tagId);
}
