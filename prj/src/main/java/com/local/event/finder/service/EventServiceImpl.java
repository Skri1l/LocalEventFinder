package com.local.event.finder.service;

import com.local.event.finder.model.dto.EventRequestDto;
import com.local.event.finder.model.dto.EventResponseDto;
import com.local.event.finder.model.entity.Event;
import com.local.event.finder.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;

    @Override
    public EventResponseDto create(EventRequestDto eventDto) {
        Objects.requireNonNull(eventDto, "Event cannot be null");
        if(eventRepository.existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitude(eventDto.title(),
                eventDto.startTime(), eventDto.endTime(), eventDto.latitude(), eventDto.longitude())){
            throw new RuntimeException("This event already exists");
        }
        Event event = new Event();
        event.setTitle(eventDto.title());
        event.setDescription(eventDto.description());
        event.setLatitude(eventDto.latitude());
        event.setLongitude(eventDto.longitude());
        event.setCountry(eventDto.country());
        event.setCity(eventDto.city());
        event.setStartTime(eventDto.startTime());
        event.setEndTime(eventDto.endTime());
        event.setMaxParticipants(eventDto.maxParticipants());
        event.setAgeRestriction(eventDto.ageRestriction());
        event.setImageUrl(eventDto.imageUrl());
        //event.setCreatedBy(eventDto.cratedBy());
        Event savedEvent = eventRepository.save(event);

        return new EventResponseDto(
                savedEvent.getId(),
                savedEvent.getTitle(),
                savedEvent.getDescription(),
                savedEvent.getLatitude(),
                savedEvent.getLongitude(),
                savedEvent.getCountry(),
                savedEvent.getCity(),
                savedEvent.getStartTime(),
                savedEvent.getEndTime(),
                savedEvent.getMaxParticipants(),
                savedEvent.getAgeRestriction(),
                savedEvent.getImageUrl(),
                eventDto.tagIds(),
                eventDto.categoryIds(),
                savedEvent.getCreatedAt());
    }
}
