package com.local.event.finder.event.tag;

import jakarta.validation.constraints.NotBlank;

public record TagDto(
        long id,
        
        @NotBlank
        String name
) {
}
