package com.local.event.finder.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EventParticipantResponseDto(
        String username,

        String email,

        @JsonProperty("avatar_url")
        String avatarUrl,

        int age
)
{}
