package com.local.event.finder.authentification.dto;

import org.junit.jupiter.api.Test;

import com.local.event.finder.authentication.dto.MessageDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageDtoTest {

    @Test
    void shouldCreateMessageDto() {
        String message = "Password reset successfully";

        MessageDto dto = new MessageDto(message);

        assertEquals(message, dto.message());
    }
}
