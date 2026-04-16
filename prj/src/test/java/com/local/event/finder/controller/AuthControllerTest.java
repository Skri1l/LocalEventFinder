package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.event.finder.repository.RefreshTokenRepository;
import com.local.event.finder.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * In test uses only for cleaning all users in test database.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * In test uses only for cleaning all users in test database.
     */
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void clean() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        Map<String, Object> request = Map.of(
                "username", "testUsername",
                "email", "john3@example.com",
                "password", "av2bB123?0",
                "avatar_url", "https://example.com/avatar.png",
                "age", 25
        );

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);

        JsonNode data = root.get("data");
        assertNotNull(data, "Response should have field 'data'");

        assertTrue(data.has("access_token"),
                "Answer doesn't have access_token");

        assertTrue(data.has("refresh_token"),
                "В doesn't have refresh_token");

        int expiresIn = data.get("expires_in").asInt();
        assertTrue(expiresIn > 0,
                "expires_in should be > 0, but was: " + expiresIn);
    }
}
