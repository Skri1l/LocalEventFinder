package com.local.event.finder.event.participant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record EventParticipantResponseDto(
        String username,

        String email,

        @JsonProperty("avatar_url")
        String avatarUrl,

        int age
)
{}
