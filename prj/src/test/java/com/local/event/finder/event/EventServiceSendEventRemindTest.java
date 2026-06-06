package com.local.event.finder.event;

import com.local.event.finder.event.category.CategoryRepository;
import com.local.event.finder.event.category.EventCategoryRepository;
import com.local.event.finder.event.participant.EventParticipant;
import com.local.event.finder.event.participant.EventParticipantRepository;
import com.local.event.finder.event.tag.EventTagRepository;
import com.local.event.finder.event.tag.TagRepository;
import com.local.event.finder.notifications.EmailService;
import com.local.event.finder.user.User;
import com.local.event.finder.user.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;;

@ExtendWith(MockitoExtension.class)
class EventServiceSendEventRemindTest {

    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @Mock private EventParticipantRepository eventParticipantRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private EventCategoryRepository eventCategoryRepository;
    @Mock private TagRepository tagRepository;
    @Mock private EventTagRepository eventTagRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event buildEvent(LocalDateTime startTime) {
        Event event = new Event();
        event.setId(1L);
        event.setStartTime(startTime);
        event.setEndTime(startTime.plusHours(2));
        return event;
    }

    private EventParticipant buildParticipant(Event event, String email) {
        User user = new User();
        user.setEmail(email);

        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(user);
        return participant;
    }

    @Test
    void shouldSendEmailWhenNowIsExactlyAtReminderTime() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        Event event = buildEvent(startTime);
        EventParticipant participant = buildParticipant(event, "user@test.com");

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventParticipantRepository.findAllByEventId(1L)).thenReturn(List.of(participant));

        eventService.sendEventRemind();

        verify(emailService, times(1)).sendEmailNotification(any(), eq(event));
    }

    @Test
    void shouldSendEmailWhenNowIsInsideReminderWindow() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).minusMinutes(5);
        Event event = buildEvent(startTime);
        EventParticipant participant = buildParticipant(event, "user@test.com");

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventParticipantRepository.findAllByEventId(1L)).thenReturn(List.of(participant));

        eventService.sendEventRemind();

        verify(emailService, times(1)).sendEmailNotification(any(), eq(event));
    }

    @Test
    void shouldNotSendEmailWhenNowIsAfterReminderWindow() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).minusMinutes(11);
        Event event = buildEvent(startTime);

        when(eventRepository.findAll()).thenReturn(List.of(event));

        eventService.sendEventRemind();

        verify(emailService, never()).sendEmailNotification(any(), any());
    }

    @Test
    void shouldNotSendEmailWhenNowIsBeforeReminderTime() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(2);
        Event event = buildEvent(startTime);

        when(eventRepository.findAll()).thenReturn(List.of(event));

        eventService.sendEventRemind();

        verify(emailService, never()).sendEmailNotification(any(), any());
    }

    @Test
    void shouldDoNothingWhenNoEvents() {
        when(eventRepository.findAll()).thenReturn(List.of());

        eventService.sendEventRemind();

        verify(emailService, never()).sendEmailNotification(any(), any());
    }

    @Test
    void shouldNotSendEmailWhenEventHasNoParticipants() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        Event event = buildEvent(startTime);

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventParticipantRepository.findAllByEventId(1L)).thenReturn(List.of());

        eventService.sendEventRemind();

        verify(emailService, never()).sendEmailNotification(any(), any());
    }

    @Test
    void shouldSendEmailToEachParticipant() {
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        Event event = buildEvent(startTime);
        EventParticipant p1 = buildParticipant(event, "alice@test.com");
        EventParticipant p2 = buildParticipant(event, "bob@test.com");

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventParticipantRepository.findAllByEventId(1L)).thenReturn(List.of(p1, p2));

        eventService.sendEventRemind();

        verify(emailService, times(2)).sendEmailNotification(any(), eq(event));
    }
}