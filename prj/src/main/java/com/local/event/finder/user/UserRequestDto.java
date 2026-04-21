package com.local.event.finder.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

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
        String password,

        @URL
        String avatarUrl,

        @NotNull
        @Positive
        @Min(1)
        @Max(100)
        int age
){}
