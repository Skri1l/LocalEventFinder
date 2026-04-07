package com.local.event.finder.model.dto;

import com.local.event.finder.model.entity.User;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.data.jpa.domain.AbstractAuditable_.createdBy;

public record EventRequestDto(

        @NotBlank
        @Size(max = 1000)
        String title,

        String description,

        @NotNull
        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        Double latitude,

        @NotNull
        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        Double longitude,

        @NotBlank
        @Size(max = 100)
        String country,

        @NotBlank
        @Size(max = 100)
        String city,

        @NotNull
        LocalDateTime startTime,

        @NotNull
        LocalDateTime endTime,

        @Min(1)
        int maxParticipants,

        @Min(0)
        @Max(100)
        int ageRestriction,

        Set<Long> tagIds,

        Set<Long> categoryIds,

        @URL
        String imageUrl
){}
