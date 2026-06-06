package com.local.event.finder.event;
import com.local.event.finder.event.category.Category;
import com.local.event.finder.event.category.CategoryRepository;
import com.local.event.finder.event.category.EventCategoryRepository;
import com.local.event.finder.event.participant.EventParticipantRepository;
import com.local.event.finder.event.tag.EventTagRepository;
import com.local.event.finder.event.tag.Tag;
import com.local.event.finder.event.tag.TagRepository;
import com.local.event.finder.notifications.EmailService;
import com.local.event.finder.user.User;
import com.local.event.finder.user.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class EventServiceCreateTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventParticipantRepository eventParticipantRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventCategoryRepository eventCategoryRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private EventTagRepository eventTagRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private EventServiceImpl eventService;

    private void mockCurrentUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setAge(25);

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken("test@test.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
    }

    private EventRequestDto buildDto(Set<Long> tagIds, Set<Long> categoryIds) {
        return new EventRequestDto(
            "Party",
            "Description",
            54.6872,
            25.2797,
            "Lithuania",
            "Vilnius",
            LocalDateTime.of(2026, 6, 10, 18, 0),
            LocalDateTime.of(2026, 6, 10, 20, 0),
            10,
            18,
            tagIds,
            categoryIds,
            "https://img.com/test.png"
        );
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldThrowExceptionWhenEventDtoIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> eventService.create(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenEndTimeBeforeStartTime() {

        EventRequestDto dto = new EventRequestDto(
            "Party",
            "Description",
            54.6872,
            25.2797,
            "Lithuania",
            "Vilnius",
            LocalDateTime.of(2026, 6, 10, 20, 0),
            LocalDateTime.of(2026, 6, 10, 18, 0),
            10,
            18,
            Set.of(),
            Set.of(),
            "https://img.com/test.png"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> eventService.create(dto)
        );
    }

    @Test
    void shouldThrowExceptionWhenEventAlreadyExists() {

        EventRequestDto dto = new EventRequestDto(
            "Party",
            "Description",
            54.6872,
            25.2797,
            "Lithuania",
            "Vilnius",
            LocalDateTime.of(2026, 6, 10, 18, 0),
            LocalDateTime.of(2026, 6, 10, 20, 0),
            10,
            18,
            Set.of(),
            Set.of(),
            "https://img.com/test.png"
        );

        when(eventRepository.existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitude(
                dto.title(),
                dto.startTime(),
                dto.endTime(),
                dto.latitude(),
                dto.longitude()
        )).thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> eventService.create(dto)
        );
    }

    @Test
    void shouldThrowExceptionWhenTagNotFound() {
        mockCurrentUser();

        EventRequestDto dto = buildDto(Set.of(99L), Set.of());

        when(eventRepository.existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitude(
                dto.title(), dto.startTime(), dto.endTime(), dto.latitude(), dto.longitude()
        )).thenReturn(false);

        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
            IllegalArgumentException.class,
            () -> eventService.create(dto)
        );
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        mockCurrentUser();

        EventRequestDto dto = buildDto(Set.of(), Set.of(99L));

        when(eventRepository.existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitude(
                dto.title(), dto.startTime(), dto.endTime(), dto.latitude(), dto.longitude()
        )).thenReturn(false);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
            IllegalArgumentException.class,
            () -> eventService.create(dto)
        );
    }

    @Test
    void shouldCreateEventSuccessfullyWithTagsAndCategories() {
        mockCurrentUser();

        EventRequestDto dto = buildDto(Set.of(1L), Set.of(2L));

        when(eventRepository.existsByTitleAndStartTimeAndEndTimeAndLatitudeAndLongitude(
                dto.title(), dto.startTime(), dto.endTime(), dto.latitude(), dto.longitude()
        )).thenReturn(false);

        Tag tag = new Tag();
        tag.setId(1L);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Category category = new Category();
        category.setId(2L);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        when(eventRepository.save(any())).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            e.setId(42L);
            return e;
        });

        long id = eventService.create(dto);

        assertEquals(42L, id);
    }
}
