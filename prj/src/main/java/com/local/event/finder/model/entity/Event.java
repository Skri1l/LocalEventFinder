package com.local.event.finder.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    @NotBlank
    @Size(max = 1000)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @NotBlank
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @Column(nullable = false)
    @NotBlank
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    @Size(max = 100)
    private String country;

    @Size(max = 100)
    private String city;

    @Column(nullable = false)
    @NotBlank
    private LocalDateTime startTime;

    @Column(nullable = false)
    @NotBlank
    private LocalDateTime endTime;

    @Min(1)
    private int maxParticipants;

    @Min(0)
    @Max(100)
    private int ageRestriction;

    @URL
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id",  nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    @PastOrPresent
    private LocalDateTime createdAt;
}
