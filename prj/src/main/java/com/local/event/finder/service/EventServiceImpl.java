package com.local.event.finder.service;

import com.local.event.finder.category.CategoryRepository;
import com.local.event.finder.model.dto.EventParticipantResponseDto;
import com.local.event.finder.model.dto.EventRequestDto;
import com.local.event.finder.model.dto.EventResponseDto;
import com.local.event.finder.model.entity.Category;
import com.local.event.finder.model.entity.Event;
import com.local.event.finder.model.entity.EventCategory;
import com.local.event.finder.model.entity.EventParticipant;
import com.local.event.finder.model.entity.User;
import com.local.event.finder.repository.EventCategoryRepository;
import com.local.event.finder.repository.EventParticipantRepository;
import com.local.event.finder.repository.EventRepository;
import com.local.event.finder.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final CategoryRepository categoryRepository;
    private final EventCategoryRepository eventCategoryRepository;

    @Override
    @Transactional
    public void create(EventRequestDto eventDto) {
        Objects.requireNonNull(eventDto, "Event cannot be null");
        if(eventRepository.existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitude(eventDto.title(),
                eventDto.startTime(), eventDto.endTime(), eventDto.latitude(), eventDto.longitude())){
            throw new RuntimeException("This event already exists");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
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
        eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEventResponseById(Long id) {
        return toResponseDto(getById(id));
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
    @Transactional(readOnly = true)
    public Event getById(Long id) {
        Objects.requireNonNull(id, "Event id cannot be null");
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public void joinEvent(Long eventId) {
        Objects.requireNonNull(eventId, "Event id cannot be null");

        Event event = getById(eventId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (event.getCreatedBy().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Event creator cant join event");
        }
        if (eventParticipantRepository.existsByEventIdAndUserId(eventId, user.getId())){
            throw new EntityNotFoundException("User already joined this event");
        }
        long participantsCount = eventParticipantRepository.countByEventId(eventId);
        if (participantsCount >= event.getMaxParticipants()) {
            throw new EntityNotFoundException("Event max participants reached");
        }
        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setEvent(event);
        eventParticipant.setUser(user);
        eventParticipant.setDateTime(LocalDateTime.now());
        eventParticipantRepository.save(eventParticipant);
    }

    @Override
    @Transactional(readOnly = true)
    public void leaveEvent(Long eventId){
        Objects.requireNonNull(eventId, "Event id cannot be null");

        Event event = getById(eventId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!eventParticipantRepository.existsByEventIdAndUserId(eventId, user.getId())) {
            throw new EntityNotFoundException("User is not participant of this event");
        }

        eventParticipantRepository.deleteByEventIdAndUserId(eventId, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventParticipantResponseDto> getParticipants(Long eventId) {
        Objects.requireNonNull(eventId, "Event id cannot be null");
        getById(eventId);

        return eventParticipantRepository.findAllByEventId(eventId).stream()
                .map(eventParticipant -> {
                    User user = eventParticipant.getUser();
                    return new EventParticipantResponseDto(
                            user.getUsername(),
                            user.getEmail(),
                            user.getAvatarUrl(),
                            user.getAge()
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public void assignCategory(Long eventId, Long categoryId) {
        Objects.requireNonNull(eventId, "Event id cannot be null");
        Objects.requireNonNull(categoryId, "Category id cannot be null");

        Event event = getById(eventId);
        validateEventCreator(event);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + categoryId + " not found"));

        if (eventCategoryRepository.existsByEventIdAndCategoryId(eventId, categoryId)){
            throw new EntityNotFoundException("Category already assigned to this event");
        }

        EventCategory eventCategory = new EventCategory();
        eventCategory.setEvent(event);
        eventCategory.setCategory(category);
        eventCategoryRepository.save(eventCategory);
    }

    private void validateEventCreator(Event event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!event.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Only event creator can modify this event");
        }
    }

    @Override
    @Transactional
    public void removeCategory(Long eventId, Long categoryId) {
        Objects.requireNonNull(eventId, "Event id cannot be null");
        Objects.requireNonNull(categoryId, "Category id cannot be null");

        Event event = getById(eventId);
        validateEventCreator(event);

        if (!eventCategoryRepository.existsByEventIdAndCategoryId(eventId, categoryId)) {
            throw new IllegalArgumentException("Category is not assigned to this event");
        }

        eventCategoryRepository.deleteByEventIdAndCategoryId(eventId, categoryId);
    }
}
