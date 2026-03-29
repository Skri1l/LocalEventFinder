package com.local.event.finder.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<EventCategory> eventCategory;
}
