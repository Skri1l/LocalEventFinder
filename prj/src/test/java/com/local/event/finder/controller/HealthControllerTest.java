package com.local.event.finder.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HealthControllerTest {
    private static final String REGISTER_URL = "/health";
    private static final String ROOT_NAME = "data";
    private static final String STATUS_FIELD_NAME = "status";
    private static final String GOOD_STATUS = "OK";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnOkAndStatus200() throws Exception {
        MvcResult result = mockMvc.perform(get(HealthControllerTest.REGISTER_URL))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(json);

        JsonNode data = root.get(HealthControllerTest.ROOT_NAME);
        assertNotNull(data);

        assertTrue(data.has(HealthControllerTest.STATUS_FIELD_NAME));
        String status = data.get(HealthControllerTest.STATUS_FIELD_NAME).asText();
        assertEquals(HealthControllerTest.GOOD_STATUS, status);
    }
}
