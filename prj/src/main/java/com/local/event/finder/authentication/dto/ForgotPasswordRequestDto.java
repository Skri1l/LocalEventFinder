package com.local.event.finder.authentication.dto;

import jakarta.validation.constraints.Email;

public record ForgotPasswordRequestDto(
        @Email
        String email
) {
}
