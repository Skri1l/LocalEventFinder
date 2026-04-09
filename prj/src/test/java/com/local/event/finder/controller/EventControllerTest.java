package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.event.finder.model.dto.EventRequestDto;
import com.local.event.finder.model.entity.Event;
import com.local.event.finder.model.entity.User;
import com.local.event.finder.repository.EventRepository;
import com.local.event.finder.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    private static final String EVENT_URL = "/events";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPasswordHash("12345");
        user.setAge(19);
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createEventTest() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "test",
                "testDescription",
                30.0,
                60.0,
                "Lithuania",
                "Kalas",
                LocalDateTime.of(2026,4,1,17,0),
                LocalDateTime.of(2026,4,1,19,30),
                10,
                19,
                null,
                null,
                "https://test.com/image.png");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createEventTestShouldFailWhenTitleIsNull() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                null,
                "testDescription",
                30.0,
                60.0,
                "Lithuania",
                "Kalas",
                LocalDateTime.of(2026,4,1,17,0),
                LocalDateTime.of(2026,4,1,19,30),
                10,
                19,
                null,
                null,
                "https://test.com/image.png");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void  createEventTestShouldFailWhenLatitudeIsNull() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "test",
                "testDescription",
                null,
                60.0,
                "Lithuania",
                "Kalas",
                LocalDateTime.of(2026,4,1,17,0),
                LocalDateTime.of(2026,4,1,19,30),
                10,
                19,
                null,
                null,
                "https://test.com/image.png");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void  createEventTestShouldFailWhenLongitudeIsNull() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "test",
                "testDescription",
                60.0,
                null,
                "Lithuania",
                "Kalas",
                LocalDateTime.of(2026,4,1,17,0),
                LocalDateTime.of(2026,4,1,19,30),
                10,
                19,
                null,
                null,
                "https://test.com/image.png");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void  createEventTestShouldFailWhenCountryIsNull() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "test",
                "testDescription",
                60.0,
                60.0,
                null,
                "Kalas",
                LocalDateTime.of(2026,4,1,17,0),
                LocalDateTime.of(2026,4,1,19,30),
                10,
                19,
                null,
                null,
                "https://test.com/image.png");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void  createEventTestShouldFailWhenCityIsNull() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "test",
                "testDescription",
                60.0,
                60.0,
                "country",
                null,
                LocalDateTime.of(2026,4,1,17,0),
                LocalDateTime.of(2026,4,1,19,30),
                10,
                19,
                null,
                null,
                "https://test.com/image.png");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void  createEventTestShouldFailWhenStartTimeIsNull() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "test",
                "testDescription",
                60.0,
                60.0,
                "country",
                "Kalas",
                null,
                LocalDateTime.of(2026,4,1,19,30),
                10,
                19,
                null,
                null,
                "https://test.com/image.png");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void  createEventTestShouldFailWhenEndTimeIsNull() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "test",
                "testDescription",
                60.0,
                60.0,
                "country",
                "Kalas",
                LocalDateTime.of(2026,4,1,17,0),
                null,
                10,
                19,
                null,
                null,
                "https://test.com/image.png");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void  createEventTestShouldFailWhenMaxParticipantsIsZero() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "test",
                "testDescription",
                60.0,
                60.0,
                "country",
                "Kalas",
                LocalDateTime.of(2026,4,1,17,0),
                LocalDateTime.of(2026,4,1,19,30),
                0,
                19,
                null,
                null,
                "https://test.com/image.png");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void  createEventTestShouldFailWhenAgeRestrictionIsNegative() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "test",
                "testDescription",
                60.0,
                60.0,
                "country",
                "Kalas",
                LocalDateTime.of(2026,4,1,17,0),
                LocalDateTime.of(2026,4,1,19,30),
                15,
                -19,
                null,
                null,
                "https://test.com/image.png");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void  createEventTestShouldFailWhenImageUrlIsInvalid() throws Exception {
        EventRequestDto eventRequestDto = new EventRequestDto(
                "test",
                "testDescription",
                60.0,
                60.0,
                "country",
                "Kalas",
                LocalDateTime.of(2026,4,1,17,0),
                LocalDateTime.of(2026,4,1,19,30),
                15,
                19,
                null,
                null,
                "not-a-url");

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getEventByIdTestShouldReturnEvent() throws Exception {
        User user = userRepository.findByUsername("testuser").orElseThrow();
        Event event = new Event();
        event.setTitle("newTest");
        event.setDescription("newTestDescription");
        event.setLatitude(60.0);
        event.setLongitude(60.0);
        event.setCountry("newCountry");
        event.setCity("newCity");
        event.setStartTime(LocalDateTime.of(2026, 4, 1, 17, 0));
        event.setEndTime(LocalDateTime.of(2026, 4, 1, 19, 30));
        event.setMaxParticipants(15);
        event.setAgeRestriction(19);
        event.setImageUrl("https://test.com/image.png");
        event.setCreatedBy(user);

        Event savedEvent = eventRepository.save(event);

        mockMvc.perform(get(EVENT_URL + "/{id}", savedEvent.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("newTest"))
                .andExpect(jsonPath("$.city").value("newCity"))
                .andExpect(jsonPath("$.country").value("newCountry"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getEventByIdTestShouldReturnNotFoundWhenEventDoesNotExist() throws Exception {

        mockMvc.perform(get(EVENT_URL + "/{id}", 999L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getEventsByTitleShouldReturnAllEventsWhenSeveralEventsHaveSameTitle() throws Exception {
        User user = userRepository.findByUsername("testuser").orElseThrow();
        Event firstEvent = new Event();
        firstEvent.setTitle("newTest");
        firstEvent.setDescription("first description");
        firstEvent.setLatitude(60.0);
        firstEvent.setLongitude(60.0);
        firstEvent.setCountry("newCountry");
        firstEvent.setCity("newCity");
        firstEvent.setStartTime(LocalDateTime.of(2026, 4, 1, 17, 0));
        firstEvent.setEndTime(LocalDateTime.of(2026, 4, 1, 19, 30));
        firstEvent.setMaxParticipants(15);
        firstEvent.setAgeRestriction(19);
        firstEvent.setImageUrl("https://test.com/image1.png");
        firstEvent.setCreatedBy(user);

        Event secondEvent = new Event();
        secondEvent.setTitle("newTest");
        secondEvent.setDescription("second description");
        secondEvent.setLatitude(61.0);
        secondEvent.setLongitude(61.0);
        secondEvent.setCountry("newCountry2");
        secondEvent.setCity("newCity2");
        secondEvent.setStartTime(LocalDateTime.of(2026, 4, 2, 17, 0));
        secondEvent.setEndTime(LocalDateTime.of(2026, 4, 2, 19, 30));
        secondEvent.setMaxParticipants(20);
        secondEvent.setAgeRestriction(18);
        secondEvent.setImageUrl("https://test.com/image2.png");
        secondEvent.setCreatedBy(user);

        eventRepository.save(firstEvent);
        eventRepository.save(secondEvent);

        mockMvc.perform(get(EVENT_URL + "/title")
                        .param("title", "newTest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", everyItem(is("newTest"))))
                .andExpect(jsonPath("$[*].city", containsInAnyOrder("newCity", "newCity2")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getEventsByTitleShouldReturnEmptyListWhenEventDoesNotExist() throws Exception {
        mockMvc.perform(get(EVENT_URL + "/title")
                        .param("title", "notFoundTest"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllEventsShouldReturnAllEvents()  throws Exception {
        User user = userRepository.findByUsername("testuser").orElseThrow();
        Event firstEvent = new Event();
        firstEvent.setTitle("Event1");
        firstEvent.setDescription("first description");
        firstEvent.setLatitude(60.0);
        firstEvent.setLongitude(60.0);
        firstEvent.setCountry("newCountry");
        firstEvent.setCity("newCity");
        firstEvent.setStartTime(LocalDateTime.of(2026, 4, 1, 17, 0));
        firstEvent.setEndTime(LocalDateTime.of(2026, 4, 1, 19, 30));
        firstEvent.setMaxParticipants(15);
        firstEvent.setAgeRestriction(19);
        firstEvent.setImageUrl("https://test.com/image1.png");
        firstEvent.setCreatedBy(user);

        Event secondEvent = new Event();
        secondEvent.setTitle("Event2");
        secondEvent.setDescription("second description");
        secondEvent.setLatitude(61.0);
        secondEvent.setLongitude(61.0);
        secondEvent.setCountry("newCountry2");
        secondEvent.setCity("newCity2");
        secondEvent.setStartTime(LocalDateTime.of(2026, 4, 2, 17, 0));
        secondEvent.setEndTime(LocalDateTime.of(2026, 4, 2, 19, 30));
        secondEvent.setMaxParticipants(20);
        secondEvent.setAgeRestriction(18);
        secondEvent.setImageUrl("https://test.com/image2.png");
        secondEvent.setCreatedBy(user);

        eventRepository.save(firstEvent);
        eventRepository.save(secondEvent);

        mockMvc.perform(get(EVENT_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Event1", "Event2")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllEventsShouldReturnEmptyListWhenEventDoesNotExist() throws Exception {
        mockMvc.perform(get(EVENT_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateEventByIdTestShouldReturnUpdatedEvent() throws Exception {
        User user = userRepository.findByUsername("testuser").orElseThrow();
        Event oldEvent = new Event();
        oldEvent.setTitle("oldEvent");
        oldEvent.setDescription("old description");
        oldEvent.setLatitude(60.0);
        oldEvent.setLongitude(60.0);
        oldEvent.setCountry("oldCountry");
        oldEvent.setCity("oldCity");
        oldEvent.setStartTime(LocalDateTime.of(2026, 4, 1, 17, 0));
        oldEvent.setEndTime(LocalDateTime.of(2026, 4, 1, 19, 30));
        oldEvent.setMaxParticipants(15);
        oldEvent.setAgeRestriction(19);
        oldEvent.setImageUrl("https://oldtest.com/image1.png");
        oldEvent.setCreatedBy(user);

        Event savedEvent = eventRepository.save(oldEvent);

        Long id = savedEvent.getId();

        Event newEvent = new Event();
        newEvent.setTitle("newEvent");
        newEvent.setDescription("new description");
        newEvent.setLatitude(61.0);
        newEvent.setLongitude(61.0);
        newEvent.setCountry("newCountry");
        newEvent.setCity("newCity");
        newEvent.setStartTime(LocalDateTime.of(2026, 4, 2, 17, 0));
        newEvent.setEndTime(LocalDateTime.of(2026, 4, 2, 19, 30));
        newEvent.setMaxParticipants(20);
        newEvent.setAgeRestriction(18);
        newEvent.setImageUrl("https://newtest.com/image.png");
        newEvent.setCreatedBy(user);

        mockMvc.perform(patch(EVENT_URL + "/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEvent)))
                .andExpect(status().isOk());

        Event updatedEvent = eventRepository.findById(id).orElseThrow();

        assertEquals("newEvent", updatedEvent.getTitle());
        assertEquals("new description", updatedEvent.getDescription());
        assertEquals("newCountry", updatedEvent.getCountry());
        assertEquals("newCity", updatedEvent.getCity());
        assertEquals(61.0, updatedEvent.getLatitude());
        assertEquals(61.0, updatedEvent.getLongitude());
    }
}
