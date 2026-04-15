package com.local.event.finder.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "email cant be blank")
        String email,

        @NotBlank(message = "password cant be empty")
        String password
) {}
