package com.local.event.finder.service;

import com.local.event.finder.api.ApiResponseDto;
import com.local.event.finder.model.dto.EventParticipantResponseDto;
import com.local.event.finder.model.dto.EventRequestDto;
import com.local.event.finder.model.dto.EventResponseDto;
import com.local.event.finder.model.entity.Event;

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
