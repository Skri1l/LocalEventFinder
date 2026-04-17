package com.local.event.finder.event.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(
        @NotBlank
        String name) {
}
