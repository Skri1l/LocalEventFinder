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
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,  nullable = false)
    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @Column(unique = true,  nullable = false)
    @Email
    @NotBlank
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String passwordHash;

    @URL
    private String avatarUrl;

    @Column(nullable = false)
    @Min(0)
    @Max(100)
    private int age;

    @Column(nullable = false, updatable = false)
    @PastOrPresent
    private LocalDateTime createdAt;
}
