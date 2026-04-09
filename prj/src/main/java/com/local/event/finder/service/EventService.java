package com.local.event.finder.service;

import com.local.event.finder.model.dto.EventRequestDto;
import com.local.event.finder.model.dto.EventResponseDto;
import com.local.event.finder.model.entity.Event;

import java.util.List;

public interface EventService {

    EventResponseDto create(EventRequestDto eventDto);

    Event getById(Long id);

    List<EventResponseDto> getByTitle(String title);

    List<EventResponseDto> getAll();

    EventResponseDto update(Long id, EventRequestDto eventDto);

    void delete(Long id);

    List<EventResponseDto> getEventsByUser(Long userId);

    EventResponseDto getEventResponseById(Long id);
}
