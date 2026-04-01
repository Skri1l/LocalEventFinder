package com.local.event.finder.service;

import com.local.event.finder.model.dto.EventRequestDto;
import com.local.event.finder.model.dto.EventResponseDto;
import com.local.event.finder.model.entity.Event;
import com.local.event.finder.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;

    @Override
    @Transactional
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

    @Override
    public Event getById(Long id) {
        Objects.requireNonNull(id, "Event id cannot be null");
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
    }

    @Override
    public Event getByTitle(String title) {
        Objects.requireNonNull(title, "Event title cannot be null");
        return eventRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("Event with title " + title + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @Override
    @Transactional
    public Event update(Long id, EventRequestDto eventDto) {
        Objects.requireNonNull(id, "Event id cannot be null");
        Objects.requireNonNull(eventDto, "Event request cannot be null");
        if(eventRepository.existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitudeAndIdNot(
                eventDto.title(),
                eventDto.startTime(),
                eventDto.endTime(),
                eventDto.latitude(),
                eventDto.longitude(),
                id)){
            throw new IllegalArgumentException("This event already exists");
        }
        Event existingEvent = getById(id);
        BeanUtils.copyProperties(eventDto, existingEvent, "id");
        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Objects.requireNonNull(id, "Event id cannot be null");
        Event existingEvent = getById(id);
        eventRepository.delete(existingEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEventsByUser(Long userId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        return this.eventRepository.findByCreatedById(userId);
    }
}
