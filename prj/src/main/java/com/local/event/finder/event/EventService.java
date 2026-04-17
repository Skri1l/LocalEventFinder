package com.local.event.finder.event;

import com.local.event.finder.event.participant.EventParticipantResponseDto;

import java.util.List;

public interface EventService {

    void create(EventRequestDto eventDto);

    Event getById(Long id);

    List<EventResponseDto> getAll();

    EventResponseDto update(Long id, EventRequestDto eventDto);

    void delete(Long id);

    EventResponseDto getEventResponseById(Long id);

    void joinEvent(Long eventId);

    void leaveEvent(Long eventId);

    List<EventParticipantResponseDto> getParticipants(Long eventId);

    void assignCategory(Long eventId, Long categoryId);

    void removeCategory(Long eventId, Long categoryId);
}
