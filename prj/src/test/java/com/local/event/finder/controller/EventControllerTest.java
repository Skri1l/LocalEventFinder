package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.event.finder.event.EventRepository;
import com.local.event.finder.event.participant.EventParticipantRepository;
import com.local.event.finder.user.UserRepository;
import com.local.event.finder.user.UserRequestDto;
import com.local.event.finder.refreshToken.RefreshTokenRepository;
import com.local.event.finder.event.EventRequestDto;
import com.local.event.finder.event.EventResponseDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    private static final String EVENTS_URL = "/events";

    private static final String AUTH_REGISTER = "/auth/register";

    private static final String ROOT = "data";
    private static final String ACCESS = "access_token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    private long id = 0;

    @BeforeEach
    void clean() {
        eventParticipantRepository.deleteAllInBatch();
        eventParticipantRepository.flush();

        eventRepository.deleteAllInBatch();
        eventRepository.flush();

        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    private String registerAndLogin() throws Exception {
        String email = "test" + id + "@mail.com";
        String username = "user" + id++;

        UserRequestDto user = new UserRequestDto(
                username,
                email,
                "Password123!",
                "https://img.com/a.png",
                25
        );

        MvcResult result = mockMvc.perform(post(AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get(ROOT).get(ACCESS).asText();
    }

    private EventRequestDto validEvent() {
        return new EventRequestDto(
                "Title",
                "Description",
                54.6,
                25.2,
                "Lithuania",
                "Vilnius",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                10,
                18,
                Set.of(),
                Set.of(),
                "https://img.com/event.png"
        );
    }

    private long createEventAndReturnId(String token) throws Exception {
        MvcResult resultPost = mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEvent())))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(resultPost.getResponse().getContentAsString());
        return root
                .get(ROOT)
                .get("id")
                .asLong();
    }

    @Test
    void shouldCreateEvent() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEvent())))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequest_whenInvalidEvent() throws Exception {
        String token = registerAndLogin();

        EventRequestDto invalid = new EventRequestDto(
                "",
                null,
                null,
                null,
                "",
                "",
                null,
                null,
                0,
                200,
                null,
                null,
                "not-url"
        );

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetEventById() throws Exception {
        String token = registerAndLogin();
        long eventId = createEventAndReturnId(token);

        MvcResult result = mockMvc.perform(get(EVENTS_URL + "/" + eventId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(root.get(ROOT));
    }

    @Test
    void shouldGetAllEvents() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(get(EVENTS_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateEvent() throws Exception {
        String token = registerAndLogin();
        long eventId = createEventAndReturnId(token);

        EventRequestDto updated = validEvent();

        mockMvc.perform(patch(EVENTS_URL + "/" + eventId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteEvent() throws Exception {
        String token = registerAndLogin();
        long eventId = createEventAndReturnId(token);

        mockMvc.perform(delete(EVENTS_URL + "/" + eventId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldJoinAndLeaveEvent() throws Exception {
        String creatorUserToken = registerAndLogin();
        long eventId = createEventAndReturnId(creatorUserToken);

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + creatorUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEvent())))
                .andExpect(status().isCreated());

        String userToken = registerAndLogin();

        mockMvc.perform(post(EVENTS_URL + "/" + eventId + "/participants")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete(EVENTS_URL + "/" + eventId + "/participants/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetParticipants() throws Exception {
        String token = registerAndLogin();
        long eventId = createEventAndReturnId(token);

        mockMvc.perform(get(EVENTS_URL + "/" + eventId+ "/participants")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotGetEvent_whenAgeRestrictionNotMet() throws Exception {
        String token = registerAndLogin();

        EventRequestDto event = new EventRequestDto(
                "Age restricted event",
                "desc",
                54.6,
                25.2,
                "Lithuania",
                "Vilnius",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                10,
                60, // age restriction 60+
                Set.of(),
                Set.of(),
                "https://img.com/event.png"
        );

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated());

        MvcResult resultGet = mockMvc.perform(get(EVENTS_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(resultGet.getResponse().getContentAsString());

        JsonNode data = root.get(ROOT);

        assertNotNull(data);
        assertTrue(data.isArray());
        assertEquals(0, data.size());
    }

    @Test
    void shouldNotAllowJoin_whenMaxParticipantsReached() throws Exception {
        String token = registerAndLogin();

        EventRequestDto event = new EventRequestDto(
                "Full event",
                "desc",
                54.6,
                25.2,
                "Lithuania",
                "Vilnius",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1,   // max participants = 1
                0,
                Set.of(),
                Set.of(),
                "https://img.com/event.png"
        );

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated());

        MvcResult resultGet = mockMvc.perform(get(EVENTS_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(resultGet.getResponse().getContentAsString());

        long eventId = root
                .get(ROOT)
                .get(0)
                .get("id")
                .asLong();

        String userToken1 = registerAndLogin();

        mockMvc.perform(post(EVENTS_URL + "/" + eventId + "/participants")
                        .header("Authorization", "Bearer " + userToken1))
                .andExpect(status().isOk());

        String userToken2 = registerAndLogin();

        mockMvc.perform(post(EVENTS_URL + "/" + eventId + "/participants")
                        .header("Authorization", "Bearer " + userToken2))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotAllowCreatorToJoinEvenIfFull() throws Exception {
        String token = registerAndLogin();

        EventRequestDto event = new EventRequestDto(
                "Edge event",
                "desc",
                54.6,
                25.2,
                "Lithuania",
                "Vilnius",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1,
                0,
                Set.of(),
                Set.of(),
                "https://img.com/event.png"
        );

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated());

        MvcResult resultGet = mockMvc.perform(get(EVENTS_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(resultGet.getResponse().getContentAsString());

        long eventId = root
                .get(ROOT)
                .get(0)
                .get("id")
                .asLong();

        mockMvc.perform(post(EVENTS_URL + "/" + eventId + "/participants")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetAllEventsThatJoined() throws Exception {
        String joinedEventName = "Joined Event Name";
        String notJoinedEventName = "Not Joined Event Name";

        String token = registerAndLogin();

        EventRequestDto joinedEvent = new EventRequestDto(
                joinedEventName,
                "Description",
                54.6,
                25.2,
                "Lithuania",
                "Vilnius",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                10,
                18,
                Set.of(),
                Set.of(),
                "https://img.com/event.png"
        );
        
        MvcResult resultPost = mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinedEvent)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(resultPost.getResponse().getContentAsString());
        long joinedEventId = root.get(ROOT).get("id").asLong();

        mockMvc.perform(post(EVENTS_URL + "/" + joinedEventId + "/participants")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        EventRequestDto notJoinedEvent = new EventRequestDto(
                notJoinedEventName,
                "Description",
                54.6,
                25.2,
                "Lithuania",
                "Vilnius",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                10,
                18,
                Set.of(),
                Set.of(),
                "https://img.com/event.png"
        );
        
        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notJoinedEvent)))
                .andExpect(status().isCreated());

        MvcResult resultJoinedEvents = mockMvc.perform(get(EVENTS_URL + "/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        root = objectMapper.readTree(resultJoinedEvents.getResponse().getContentAsString());

        JsonNode events = root.get(ROOT);

        List<String> titles = new ArrayList<>();
        events.forEach(e -> titles.add(e.get("title").asText()));
        System.out.println("json: " + resultJoinedEvents.getResponse().getContentAsString());
        System.out.println("events: " + events);
        assertThat(titles)
                .contains(joinedEventName)
                .doesNotContain(notJoinedEventName);
    }
}