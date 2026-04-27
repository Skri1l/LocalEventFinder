package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Map;
import java.util.Set;

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

    private static final Set<String> INVALID_USERNAMES = Set.of(
            "",
            " ",
            "?",
            "12"
    );

    private static final Set<String> INVALID_EMAILS = Set.of(
            "plain_address",
            "@no-local-part.com",
            "no-at-symbol.com",
            "user@.com",
            "user@com",
            "user@domain..com",
            "user@domain,com",
            "user name@domain.com",
            "user@domain .com",
            "user@-domain.com",
            "user@domain.com-",
            "user@@domain.com",
            ".user@domain.com",
            "user.@domain.com",
            "user@domain.c",
            "user@domain.toolongtld",
            "",
            " "
    );

    private static final Set<String> INVALID_PASSWORDS = Set.of(
            "Short1",
            "alllowercase1",
            "ALLUPPERCASE1",
            "NoDigitsHere",
            "short",
            "12345678",
            "abcdefgh",
            "ABCDEFGH",
            "Abcdefgh",
            "ABCDEF12",
            "abcdef12",
            "Ab1",
            "",
            "        "
    );

    private static final Set<Integer> INVALID_AGES = Set.of(
            -1,
            0,
            200
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long idGenerator = 0L;

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

    private String generateUniqueUsername() {
        return AuthControllerTest.GOOD_USERNAME + this.idGenerator++;
    }

    private String generateUniqueEmail() {
        return AuthControllerTest.GOOD_EMAIL + this.idGenerator++;
    }


    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRequestDto request = new UserRequestDto(
                this.generateUniqueUsername(),
                this.generateUniqueEmail(),
                AuthControllerTest.GOOD_PASSWORD,
                AuthControllerTest.GOOD_AVATAR_URL,
                AuthControllerTest.GOOD_AGE
        );

        MvcResult result = mockMvc.perform(post(AuthControllerTest.REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);

        JsonNode data = root.get(AuthControllerTest.ROOT_NAME);
        assertNotNull(data);

        assertTrue(data.has("access_token"));
        assertTrue(data.has("refresh_token"));

        int expiresIn = data.get("expires_in").asInt();
        assertTrue(expiresIn > 0);
    }

    @Test
    void shouldReturnBadRequest_whenUsernameIsInvalid() throws Exception {
        for (String invalidUsername : AuthControllerTest.INVALID_USERNAMES) {
            UserRequestDto request = new UserRequestDto(
                    invalidUsername,
                    this.generateUniqueEmail(),
                    AuthControllerTest.GOOD_PASSWORD,
                    AuthControllerTest.GOOD_AVATAR_URL,
                    AuthControllerTest.GOOD_AGE
            );
            mockMvc.perform(post(AuthControllerTest.REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Test
    void shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        for (String invalidEmail : AuthControllerTest.INVALID_EMAILS) {
            UserRequestDto request = new UserRequestDto(
                    this.generateUniqueUsername(),
                    invalidEmail,
                    AuthControllerTest.GOOD_PASSWORD,
                    AuthControllerTest.GOOD_AVATAR_URL,
                    AuthControllerTest.GOOD_AGE
            );
            mockMvc.perform(post(AuthControllerTest.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void shouldReturnBadRequest_whenPasswordIsInvalid() throws Exception {
        for (String invalidPassword : AuthControllerTest.INVALID_PASSWORDS) {
            UserRequestDto request = new UserRequestDto(
                    this.generateUniqueUsername(),
                    this.generateUniqueEmail(),
                    invalidPassword,
                    AuthControllerTest.GOOD_AVATAR_URL,
                    AuthControllerTest.GOOD_AGE
            );
            mockMvc.perform(post(AuthControllerTest.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void shouldReturnBadRequest_whenAgeIsInvalid() throws Exception {
        for (int invalidAge : AuthControllerTest.INVALID_AGES) {
            UserRequestDto request = new UserRequestDto(
                    this.generateUniqueUsername(),
                    this.generateUniqueEmail(),
                    AuthControllerTest.GOOD_PASSWORD,
                    AuthControllerTest.GOOD_AVATAR_URL,
                    invalidAge
            );
            mockMvc.perform(post(AuthControllerTest.REGISTER_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
