package com.local.event.finder.model.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.LocalDateTime;
import java.util.Set;

public record EventResponseDto (
        Long id,

        String title,

        String description,

        Double latitude,

        Double longitude,

        String country,

        String city,

        LocalDateTime startTime,

        LocalDateTime endTime,

        int maxParticipants,

        int ageRestriction,

        String imageUrl,

        Set<Long> tagIds,

        Set<Long> categoryIds,

        LocalDateTime createdAt
){}
