package com.local.event.finder.event;

import com.local.event.finder.event.category.CategoryRepository;
import com.local.event.finder.event.participant.EventParticipantResponseDto;
import com.local.event.finder.event.category.Category;
import com.local.event.finder.event.category.EventCategory;
import com.local.event.finder.event.participant.EventParticipant;
import com.local.event.finder.event.tag.EventTag;
import com.local.event.finder.event.tag.EventTagRepository;
import com.local.event.finder.event.tag.Tag;
import com.local.event.finder.event.tag.TagRepository;
import com.local.event.finder.logging.AppLogger;
import com.local.event.finder.logging.LoggerFactory;
import com.local.event.finder.notifications.EmailModel;
import com.local.event.finder.notifications.EmailService;
import com.local.event.finder.user.User;
import com.local.event.finder.event.category.EventCategoryRepository;
import com.local.event.finder.event.participant.EventParticipantRepository;
import com.local.event.finder.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final static AppLogger log = LoggerFactory.getLogger(EventServiceImpl.class);


    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final CategoryRepository categoryRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final TagRepository tagRepository;
    private final EventTagRepository eventTagRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public long create(EventRequestDto eventDto) {

        Objects.requireNonNull(eventDto, "Event cannot be null");

        if (eventDto.endTime().isBefore(eventDto.startTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        boolean exists = eventRepository.existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitude(
                eventDto.title(),
                eventDto.startTime(),
                eventDto.endTime(),
                eventDto.latitude(),
                eventDto.longitude()
        );

        if (exists) {
            throw new IllegalStateException("This event already exists");
        }

        User user = getCurrentUser();

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

        // IMPORTANT: ensure collections are initialized
        event.setEventTags(new HashSet<>());
        event.setEventCategory(new HashSet<>());

        // tags
        if (eventDto.tagIds() != null && !eventDto.tagIds().isEmpty()) {

            Set<EventTag> eventTags = eventDto.tagIds()
                    .stream()
                    .map(tagId -> {

                        Tag tag = tagRepository.findById(tagId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException("Tag not found: " + tagId));

                        EventTag eventTag = new EventTag();
                        eventTag.setEvent(event);
                        eventTag.setTag(tag);

                        return eventTag;
                    })
                    .collect(Collectors.toSet());

            event.getEventTags().addAll(eventTags);
        }

        // categories
        if (eventDto.categoryIds() != null && !eventDto.categoryIds().isEmpty()) {

            Set<EventCategory> eventCategories = eventDto.categoryIds()
                    .stream()
                    .map(categoryId -> {

                        Category category = categoryRepository.findById(categoryId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException("Category not found: " + categoryId));

                        EventCategory eventCategory = new EventCategory();
                        eventCategory.setEvent(event);
                        eventCategory.setCategory(category);

                        return eventCategory;
                    })
                    .collect(Collectors.toSet());

            event.getEventCategory().addAll(eventCategories);
        }

        Event saved = eventRepository.save(event);
        return saved.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto getEventResponseById(Long id) {
        Event event = getById(id);
        User user = getCurrentUser();

        if (event.getAgeRestriction() > user.getAge()) {
            throw new AccessDeniedException("You are not allowed to view this event");
        }
        return toResponseDto(event);
    }


    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAll(EventFilterRequestDto filter) {

        User user = getCurrentUser();

        Pageable pageable = PageRequest.of(
                filter.getOffset() / filter.getLimit(),
                filter.getLimit()
        );

        Specification<Event> specification =
                EventSpecification.withFilters(filter, user.getAge());

        return eventRepository.findAll(specification, pageable)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> getByCurrentUser() {
        User user = getCurrentUser();

        return eventParticipantRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::toEventResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public EventResponseDto update(Long id, EventRequestDto eventDto) {

        Objects.requireNonNull(id, "Event id cannot be null");
        Objects.requireNonNull(eventDto, "Event request cannot be null");

        if (eventDto.endTime().isBefore(eventDto.startTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        boolean exists = eventRepository
                .existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitudeAndIdNot(
                        eventDto.title(),
                        eventDto.startTime(),
                        eventDto.endTime(),
                        eventDto.latitude(),
                        eventDto.longitude(),
                        id
                );

        if (exists) {
            throw new IllegalArgumentException("This event already exists");
        }

        Event existingEvent = getById(id);

        validateEventCreator(existingEvent);

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

        // update tags
        if (eventDto.tagIds() != null) {

            existingEvent.getEventTags().clear();

            Set<EventTag> eventTags = eventDto.tagIds()
                    .stream()
                    .map(tagId -> {
                        Tag tag = tagRepository.getReferenceById(tagId);

                        EventTag eventTag = new EventTag();
                        eventTag.setEvent(existingEvent);
                        eventTag.setTag(tag);

                        return eventTag;
                    })
                    .collect(Collectors.toSet());

            existingEvent.getEventTags().addAll(eventTags);
        }

        // update categories
        if (eventDto.categoryIds() != null) {

            existingEvent.getEventCategory().clear();

            Set<EventCategory> categories = eventDto.categoryIds()
                    .stream()
                    .map(categoryId -> {
                        Category category = categoryRepository.getReferenceById(categoryId);

                        EventCategory eventCategory = new EventCategory();
                        eventCategory.setEvent(existingEvent);
                        eventCategory.setCategory(category);

                        return eventCategory;
                    })
                    .collect(Collectors.toSet());

            existingEvent.getEventCategory().addAll(categories);
        }

        Event savedEvent = eventRepository.save(existingEvent);

        return toResponseDto(savedEvent);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Objects.requireNonNull(id, "Event id cannot be null");
        Event existingEvent = getById(id);
        validateEventCreator(existingEvent);
        eventRepository.delete(existingEvent);
    }


    private EventResponseDto toResponseDto(Event event) {

        Set<Long> tagIds = event.getEventTags() == null
                ? Set.of()
                : event.getEventTags()
                    .stream()
                    .map(et -> et.getTag().getId())
                    .collect(Collectors.toSet());

        Set<Long> categoryIds = event.getEventCategory() == null
                ? Set.of()
                : event.getEventCategory()
                    .stream()
                    .map(ec -> ec.getCategory().getId())
                    .collect(Collectors.toSet());

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
                tagIds,
                categoryIds,
                event.getCreatedAt()
        );
    }


    private EventResponseDto toEventResponseDto(EventParticipant participant) {
        return this.toResponseDto(participant.getEvent());
    }


    @Override
    @Transactional(readOnly = true)
    public Event getById(Long id) {
        Objects.requireNonNull(id, "Event id cannot be null");
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
    }

    @Override
    @Transactional
    public void joinEvent(Long eventId) {
        Objects.requireNonNull(eventId, "Event id cannot be null");

        Event event = getById(eventId);
        User user = getCurrentUser();

        if (event.getAgeRestriction() > user.getAge()) {
            throw new AccessDeniedException("You are not allowed to join this event");
        }
        if (event.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("Event creator cant join event");
        }
        if (eventParticipantRepository.existsByEventIdAndUserId(eventId, user.getId())){
            throw new AccessDeniedException("User already joined this event");
        }
        if (isHeldSameTimeAsOther(eventId, user)){
            throw new AccessDeniedException("This event overlaps with another event in your schedule");
        }
        long participantsCount = eventParticipantRepository.countByEventId(eventId);
        if (participantsCount >= event.getMaxParticipants()) {
            throw new IllegalStateException("Event max participants reached");
        }
        EventParticipant eventParticipant = new EventParticipant();
        eventParticipant.setEvent(event);
        eventParticipant.setUser(user);
        eventParticipant.setDateTime(LocalDateTime.now());
        eventParticipantRepository.save(eventParticipant);
    }

    private boolean isHeldSameTimeAsOther(Long eventId, User user) {
        Objects.requireNonNull(eventId, "Event id cannot be null");

        Event event = getById(eventId);

        List<Event> events = eventParticipantRepository
                .findAllByUserId(user.getId())
                .stream()
                .map(EventParticipant::getEvent)
                .toList();

        for(Event other : events){
            if (other.getId().equals(event.getId())) {
                continue;
            }
            boolean isIntersect = other.getStartTime().isBefore(event.getEndTime())
                    &&  other.getEndTime().isAfter(event.getStartTime());

            if (isIntersect) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void leaveEvent(Long eventId){
        Objects.requireNonNull(eventId, "Event id cannot be null");

        Event event = getById(eventId);

        User user = getCurrentUser();

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
        User user = getCurrentUser();

        if (!event.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only event creator can modify this event");
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

    @Override
    @Transactional
    public void assignTag(Long eventId, Long tagId) {
        Objects.requireNonNull(eventId, "Event id cannot be null");
        Objects.requireNonNull(tagId, "Tag id cannot be null");

        Event event = getById(eventId);
        validateEventCreator(event);

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag with id " + tagId + " not found"));

        if (eventTagRepository.existsByEventIdAndTagId(eventId, tagId)) {
            throw new EntityNotFoundException("Tag already assigned to this event");
        }

        EventTag eventTag = new EventTag();
        eventTag.setEvent(event);
        eventTag.setTag(tag);
        eventTagRepository.save(eventTag);
    }

    @Override
    @Transactional
    public void removeTag(Long eventId, Long tagId) {
        Objects.requireNonNull(eventId, "Event id cannot be null");
        Objects.requireNonNull(tagId, "Tag id cannot be null");

        Event event = getById(eventId);
        validateEventCreator(event);

        if (!eventTagRepository.existsByEventIdAndTagId(eventId, tagId)) {
            throw new IllegalArgumentException("Tag is not assigned to this event");
        }

        eventTagRepository.deleteByEventIdAndTagId(eventId, tagId);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Scheduled(fixedRate = 60_000)
    public void sendEventRemind() {
        log.info("Scheduler started");
        List<Event> events = eventRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (Event event : events) {
            LocalDateTime reminderTime = event.getStartTime().minusDays(1);
            log.info("Reminder time: " + reminderTime);
            if (!now.isBefore(reminderTime) && now.isBefore(reminderTime.plusMinutes(10))) {
                log.info("Reminder condition PASSED");
                List<EventParticipant> participants =
                        eventParticipantRepository
                                .findAllByEventId(event.getId());
                log.info("Participants found: " + participants.size());
                for (EventParticipant participant : participants) {
                    log.info("Sending to: " + participant.getUser().getEmail());
                    EmailModel model = new EmailModel(
                            participant.getUser().getEmail(),
                            "",
                            ""
                    );

                    emailService.sendEmailNotification(model, event);
                    log.info("Email send called");
                }
            }
        }
    }
}
