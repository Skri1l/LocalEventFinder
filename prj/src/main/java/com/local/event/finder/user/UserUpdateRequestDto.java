package com.local.event.finder.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UserUpdateRequestDto(
        @Size(min = 3, max = 30, message = "username length must be from 3 to 30")
        String username,
        @Email(message = "invalid email")
        String email,
        @URL
        String avatarUrl,
        @Min(1)
        @Max(100)
        Integer age
) {
}
