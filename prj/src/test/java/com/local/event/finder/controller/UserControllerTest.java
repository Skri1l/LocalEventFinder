package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.event.finder.event.EventRepository;
import com.local.event.finder.refreshToken.RefreshTokenRepository;
import com.local.event.finder.user.UserRepository;
import com.local.event.finder.user.UserRequestDto;
import com.local.event.finder.user.UserUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String USERS_URL = "/users";
    private static final String REGISTER = "/auth/register";

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

    private long id = 0;

    @BeforeEach
    void clean() {
        eventRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String registerAndLogin(String email, String username) throws Exception {
        UserRequestDto user = new UserRequestDto(
                username,
                email,
                "Password123!",
                "https://img.com/a.png",
                25
        );

        MvcResult result = mockMvc.perform(post(REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get(ROOT).get(ACCESS).asText();
    }

    private String newEmail() {
        return "test" + (id++) + "@mail.com";
    }

    private String newUsername() {
        return "user" + (id++);
    }

    @Test
    void shouldUpdateOwnUser() throws Exception {
        String email = newEmail();
        String username = newUsername();

        String token = registerAndLogin(email, username);

        UserUpdateRequestDto update = new UserUpdateRequestDto(
                "newUsername",
                null,
                null,
                null
        );

        MvcResult result = mockMvc.perform(patch(USERS_URL + "/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(root.get(ROOT));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidUpdate() throws Exception {
        String token = registerAndLogin(newEmail(), newUsername());

        UserUpdateRequestDto invalid = new UserUpdateRequestDto(
                "a",          // too short
                "bad-email",  // invalid
                "not-url",    // invalid
                200           // invalid age
        );

        mockMvc.perform(patch(USERS_URL + "/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteOwnUser() throws Exception {
        String token = registerAndLogin(newEmail(), newUsername());

        mockMvc.perform(delete(USERS_URL + "/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}