package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.event.finder.refreshToken.RefreshTokenRepository;
import com.local.event.finder.user.UserRepository;
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
    private static final String REGISTER_URL = "/auth/register";
    private static final String ROOT_NAME = "data";
    private static final String GOOD_USERNAME = "GoodUsername";
    private static final String GOOD_EMAIL = "GoodEmail@exmaple.com";
    private static final String GOOD_PASSWORD = "av2bB123?0";
    private static final String GOOD_AVATAR_URL = "https://example.com/avatar.png";
    private static final int GOOD_AGE = 25;

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

    static Map<String, Object> getRequest(String username, String email, String password, String avatarUrl, int age) {
        return Map.of(
                "username", username,
                "email", email,
                "password", password,
                "avatar_url", avatarUrl,
                "age", age
        );

    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        Map<String, Object> request = getRequest(GOOD_USERNAME, GOOD_EMAIL, GOOD_PASSWORD, GOOD_AVATAR_URL, GOOD_AGE);

        MvcResult result = mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);

        JsonNode data = root.get(ROOT_NAME);
        assertNotNull(data, "Response should have field '" + ROOT_NAME + "'");

        assertTrue(data.has("access_token"),
                "Answer doesn't have access_token");

        assertTrue(data.has("refresh_token"),
                "В doesn't have refresh_token");

        int expiresIn = data.get("expires_in").asInt();
        assertTrue(expiresIn > 0,
                "expires_in should be > 0, but was: " + expiresIn);
    }

    @Test
    void shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        Map<String, Object> request = getRequest(
                GOOD_USERNAME,
                "invalid-email",
                GOOD_PASSWORD,
                GOOD_AVATAR_URL,
                GOOD_AGE
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenPasswordTooShort() throws Exception {
        Map<String, Object> request = getRequest(
                GOOD_USERNAME,
                GOOD_EMAIL,
                "Ab1", // too short
                GOOD_AVATAR_URL,
                GOOD_AGE
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenPasswordMissingUppercase() throws Exception {
        Map<String, Object> request = getRequest(
                GOOD_USERNAME,
                GOOD_EMAIL,
                "password123!", // no uppercase
                GOOD_AVATAR_URL,
                GOOD_AGE
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenPasswordMissingDigit() throws Exception {
        Map<String, Object> request = getRequest(
                GOOD_USERNAME,
                GOOD_EMAIL,
                "Password!!!", // no digit
                GOOD_AVATAR_URL,
                GOOD_AGE
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenEmailIsNull() throws Exception {
        Map<String, Object> request = getRequest(
                GOOD_USERNAME,
                null,
                GOOD_PASSWORD,
                GOOD_AVATAR_URL,
                GOOD_AGE
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflict_whenUserAlreadyExists() throws Exception {
        Map<String, Object> request = getRequest(
                GOOD_USERNAME,
                GOOD_EMAIL,
                GOOD_PASSWORD,
                GOOD_AVATAR_URL,
                GOOD_AGE
        );

        // first registration
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // second registration with same email
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenRequiredFieldsAreMissing() throws Exception {
        Map<String, Object> request = Map.of(
                "email", "john@example.com"
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailWhenFieldsAreNull() throws Exception {
        Map<String, Object> request = getRequest(
                null,
                null,
                null,
                null,
                0
        );

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
