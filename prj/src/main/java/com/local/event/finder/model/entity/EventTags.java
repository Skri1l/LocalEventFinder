package com.local.event.finder.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "event_tags")
@Entity
public class EventTags {

    @Id
    @GeneratedValue
    private Long eventId;

    @Id
    @GeneratedValue
    private Long tagId;
}
