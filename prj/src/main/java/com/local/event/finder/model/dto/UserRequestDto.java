package com.local.event.finder.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDto(

        @NotBlank(message = "username cant be blank")
        @Size(min = 3, max = 30, message = "username length must be from 3 to 30")
        String username,

        @Email(message = "invalid email")
        @NotBlank(message = "email cant be empty")
        String email,

        @NotBlank(message = "password cant be empty")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "Password must contain at least 8 characters, one uppercase, one lowercase and one digit"
        )
        String password
){}
