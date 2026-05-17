package com.local.event.finder.event.participant;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    long countByEventId(Long eventId);

    void deleteByEventIdAndUserId(Long eventId, Long userId);

    List<EventParticipant> findAllByEventId(Long eventId);

    List<EventParticipant> findAllByUserId(Long userId);
}
