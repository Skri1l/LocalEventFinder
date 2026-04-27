package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.event.finder.authentication.dto.LoginRequest;
import com.local.event.finder.event.EventRequestDto;
import com.local.event.finder.refreshToken.RefreshTokenRepository;
import com.local.event.finder.user.UserRepository;
import com.local.event.finder.user.UserRequestDto;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    private static final String CATEGORY_URL = "/categories";
    private static final String EVENTS_URL = "/events";

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


        MvcResult result = mockMvc.perform(post(REGISTER)
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
                "Desc",
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
    void shouldCreateCategory() throws Exception {
        String token = registerAndLogin();

        String request = """
                {
                  "name": "Sports"
                }
                """;

        mockMvc.perform(post(CATEGORY_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequest_whenInvalidCategory() throws Exception {
        String token = registerAndLogin();

        String request = """
                {
                  "name": ""
                }
                """;

        mockMvc.perform(post(CATEGORY_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllCategories_withoutAuth() throws Exception {
        mockMvc.perform(get(CATEGORY_URL))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAssignAndRemoveCategoryFromEvent() throws Exception {
        String token = registerAndLogin();

        mockMvc.perform(post(CATEGORY_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Music\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post(EVENTS_URL)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validEvent())))
                .andExpect(status().isCreated());

        mockMvc.perform(post(EVENTS_URL + "/1/categories/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(delete(EVENTS_URL + "/1/categories/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}