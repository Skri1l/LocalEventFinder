package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.event.finder.authentication.dto.LoginRequest;
import com.local.event.finder.event.EventRepository;
import com.local.event.finder.refreshToken.RefreshRequestDto;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    private static final String AUTH_URL = "/auth";
    private static final String REGISTER_URL = AUTH_URL + "/register";
    private static final String LOGIN_URL = AUTH_URL + "/login";
    private static final String REFRESH_URL = AUTH_URL + "/refresh";
    private static final String LOGOUT_URL = AUTH_URL + "/logout";
    private static final String ROOT_NAME = "data";

    private static final String ACCESS_TOKEN_NAME = "access_token";
    private static final String REFRESH_TOKEN_NAME = "refresh_token";
    private static final String EXPIRATION_IN_TOKEN_NAME = "expires_in";

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

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void clean() {
        eventRepository.deleteAll();
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

        assertTrue(data.has(AuthControllerTest.ACCESS_TOKEN_NAME));
        assertTrue(data.has(AuthControllerTest.REFRESH_TOKEN_NAME));

        int expiresIn = data.get(AuthControllerTest.EXPIRATION_IN_TOKEN_NAME).asInt();
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

    @Test
    void shouldReturnBadRequest_whenNullData() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("username", null);
        request.put("email", null);
        request.put("password", null);
        request.put("avatar_url", null);
        request.put("age", null);

        mockMvc.perform(post(AuthControllerTest.REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRegisterAndLoginSuccessfully() throws Exception {
        LoginRequest newUser = new LoginRequest(this.generateUniqueEmail(), AuthControllerTest.GOOD_PASSWORD);
        UserRequestDto registration = new UserRequestDto(
                this.generateUniqueUsername(),
                newUser.email(),
                newUser.password(),
                AuthControllerTest.GOOD_AVATAR_URL,
                AuthControllerTest.GOOD_AGE
        );

        mockMvc.perform(post(AuthControllerTest.REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post(AuthControllerTest.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);

        JsonNode data = root.get(AuthControllerTest.ROOT_NAME);
        assertNotNull(data);

        assertTrue(data.has(AuthControllerTest.ACCESS_TOKEN_NAME));
        assertTrue(data.has(AuthControllerTest.REFRESH_TOKEN_NAME));

        int expiresIn = data.get(AuthControllerTest.EXPIRATION_IN_TOKEN_NAME).asInt();
        assertTrue(expiresIn > 0);
    }

    @Test
    void shouldReturnIsForbidden_whenLoginNewUser() throws Exception {
        LoginRequest newUser = new LoginRequest(this.generateUniqueEmail(), AuthControllerTest.GOOD_PASSWORD);
        mockMvc.perform(post(AuthControllerTest.LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newUser)))
            .andExpect(status().isForbidden())
            .andReturn();
    }

    @Test
    void shouldRegisterAndLoginAndRefreshAndLogoutSuccessfully() throws Exception {
        LoginRequest newUser = new LoginRequest(this.generateUniqueEmail(), AuthControllerTest.GOOD_PASSWORD);
        UserRequestDto registration = new UserRequestDto(
                this.generateUniqueUsername(),
                newUser.email(),
                newUser.password(),
                AuthControllerTest.GOOD_AVATAR_URL,
                AuthControllerTest.GOOD_AGE
        );

        mockMvc.perform(post(AuthControllerTest.REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post(AuthControllerTest.LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);

        JsonNode data = root.get(AuthControllerTest.ROOT_NAME);
        assertNotNull(data);
        assertTrue(data.has(AuthControllerTest.REFRESH_TOKEN_NAME));
        String refresh = data.get(AuthControllerTest.REFRESH_TOKEN_NAME).asText();

        RefreshRequestDto refreshRequestDto = new RefreshRequestDto(refresh);

        result = mockMvc.perform(post(AuthControllerTest.REFRESH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        json = result.getResponse().getContentAsString();
        root = objectMapper.readTree(json);

        data = root.get(AuthControllerTest.ROOT_NAME);
        assertNotNull(data);
        assertTrue(data.has(AuthControllerTest.ACCESS_TOKEN_NAME));
        String access = data.get(AuthControllerTest.ACCESS_TOKEN_NAME).asText();

        mockMvc.perform(post(AuthControllerTest.LOGOUT_URL)
            .header("Authorization", "Bearer " + access)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mockMvc.perform(post(AuthControllerTest.LOGOUT_URL)
                .header("Authorization", "Bearer " + access))
                .andExpect(status().isForbidden());
    }
}
