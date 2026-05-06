//package com.local.event.finder.controller;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.local.event.finder.refreshToken.RefreshTokenRepository;
//import com.local.event.finder.user.UserRepository;
//import com.local.event.finder.user.UserRequestDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class TagControllerTest {
//    private static final String TAGS_URL = "/tags";
//
//    private static final String REGISTER = "/auth/register";
//
//    private static final String ROOT = "data";
//    private static final String ACCESS = "access_token";
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private RefreshTokenRepository refreshTokenRepository;
//
//    private long id = 0;
//
//    @BeforeEach
//    void clean() {
//        refreshTokenRepository.deleteAll();
//        userRepository.deleteAll();
//    }
//
//    private String registerAndLogin() throws Exception {
//        String email = "test" + id + "@mail.com";
//        String username = "user" + id++;
//
//        UserRequestDto user = new UserRequestDto(
//                username,
//                email,
//                "Password123!",
//                "https://img.com/a.png",
//                25
//        );
//
//        MvcResult result = mockMvc.perform(post(REGISTER)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isCreated())
//                .andReturn();
//
//        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
//        return root.get(ROOT).get(ACCESS).asText();
//    }
//
//    @Test
//    void shouldCreateTag() throws Exception {
//        String token = registerAndLogin();
//
//        String request = """
//                {
//                  "name": "Music"
//                }
//                """;
//
//        mockMvc.perform(post(TAGS_URL)
//                        .header("Authorization", "Bearer " + token)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(request))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void shouldReturnBadRequest_whenInvalidTag() throws Exception {
//        String token = registerAndLogin();
//
//        String request = """
//                {
//                  "name": ""
//                }
//                """;
//
//        mockMvc.perform(post(TAGS_URL)
//                        .header("Authorization", "Bearer " + token)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(request))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void shouldGetAllTags_withoutAuth() throws Exception {
//        mockMvc.perform(get(TAGS_URL))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void shouldReturnListOfTags() throws Exception {
//        String token = registerAndLogin();
//
//        mockMvc.perform(post(TAGS_URL)
//                        .header("Authorization", "Bearer " + token)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"name\":\"Tech\"}"))
//                .andExpect(status().isOk());
//
//        MvcResult result = mockMvc.perform(get(TAGS_URL))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String json = result.getResponse().getContentAsString();
//
//        assertTrue(json.contains("Tech"));
//    }
//}