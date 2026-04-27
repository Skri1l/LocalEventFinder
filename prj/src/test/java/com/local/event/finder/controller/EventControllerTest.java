package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.event.finder.authentication.dto.LoginRequest;
import com.local.event.finder.user.UserRepository;
import com.local.event.finder.user.UserRequestDto;
import com.local.event.finder.refreshToken.RefreshTokenRepository;
import com.local.event.finder.event.EventRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private long id = 0;

    @BeforeEach
    void clean() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
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

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEvent())))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get(EVENTS_URL + "/1")
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

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEvent())))
                .andExpect(status().isCreated());

        EventRequestDto updated = validEvent();

        mockMvc.perform(patch(EVENTS_URL + "/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteEvent() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEvent())))
                .andExpect(status().isCreated());

        mockMvc.perform(delete(EVENTS_URL + "/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldJoinAndLeaveEvent() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEvent())))
                .andExpect(status().isCreated());

        mockMvc.perform(post(EVENTS_URL + "/1/participants")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(delete(EVENTS_URL + "/1/participants/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetParticipants() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(get(EVENTS_URL + "/1/participants")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}