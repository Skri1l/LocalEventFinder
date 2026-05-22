package com.local.event.finder.authentication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequestDto(
        @JsonProperty("reset-token")
        String resetToken,

        @NotBlank(message = "password cant be empty")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "Password must contain at least 8 characters, one uppercase, one lowercase and one digit"
        )
        String password
) {
}
