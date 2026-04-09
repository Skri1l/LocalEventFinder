package com.local.event.finder.service;

import com.local.event.finder.model.dto.EventRequestDto;
import com.local.event.finder.model.dto.EventResponseDto;
import com.local.event.finder.model.entity.Event;
import com.local.event.finder.model.entity.User;
import com.local.event.finder.repository.EventRepository;
import com.local.event.finder.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EventResponseDto create(EventRequestDto eventDto) {
        Objects.requireNonNull(eventDto, "Event cannot be null");
        if(eventRepository.existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitude(eventDto.title(),
                eventDto.startTime(), eventDto.endTime(), eventDto.latitude(), eventDto.longitude())){
            throw new RuntimeException("This event already exists");
        }
        User user = userRepository.findById(eventDto.userId()).orElseThrow(() ->
                new EntityNotFoundException("User not found"));
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
        event.setCreatedBy(user);
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
    @Transactional(readOnly = true)
    public List<EventResponseDto> getByTitle(String title) {
        Objects.requireNonNull(title, "Event title cannot be null");
        return eventRepository.findAllByTitle(title).stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAll() {
        return eventRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public EventResponseDto update(Long id, EventRequestDto eventDto) {
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
        User user = userRepository.findById(eventDto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + eventDto.userId()));

        existingEvent.setTitle(eventDto.title());
        existingEvent.setDescription(eventDto.description());
        existingEvent.setLatitude(eventDto.latitude());
        existingEvent.setLongitude(eventDto.longitude());
        existingEvent.setCountry(eventDto.country());
        existingEvent.setCity(eventDto.city());
        existingEvent.setStartTime(eventDto.startTime());
        existingEvent.setEndTime(eventDto.endTime());
        existingEvent.setMaxParticipants(eventDto.maxParticipants());
        existingEvent.setAgeRestriction(eventDto.ageRestriction());
        existingEvent.setImageUrl(eventDto.imageUrl());
        existingEvent.setCreatedBy(user);
        Event savedEvent = eventRepository.save(existingEvent);
        return toResponseDto(savedEvent);
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
    public List<EventResponseDto> getEventsByUser(Long userId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        return eventRepository.findByCreatedById(userId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    private EventResponseDto toResponseDto(Event event) {
        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLatitude(),
                event.getLongitude(),
                event.getCountry(),
                event.getCity(),
                event.getStartTime(),
                event.getEndTime(),
                event.getMaxParticipants(),
                event.getAgeRestriction(),
                event.getImageUrl(),
                null,
                null,
                event.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public EventResponseDto getEventResponseById(Long id) {
        Objects.requireNonNull(id, "Event id cannot be null");
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
        return toResponseDto(event);
    }
}
