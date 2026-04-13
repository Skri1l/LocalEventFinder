package com.local.event.finder.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "username cant be blank")
        String username,

        @NotBlank(message = "password cant be empty")
        String password
) {}
