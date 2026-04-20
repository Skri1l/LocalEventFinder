package com.local.event.finder.event.tag;

import jakarta.validation.constraints.NotBlank;

public record TagDto(
        @NotBlank
        String name
) {
}
