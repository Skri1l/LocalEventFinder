package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.event.finder.model.dto.EventRequestDto;
import com.local.event.finder.model.entity.User;
import com.local.event.finder.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPasswordHash("12345");
        user.setAge(19);
        userRepository.save(user);

        mockMvc.perform(post(EVENT_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
