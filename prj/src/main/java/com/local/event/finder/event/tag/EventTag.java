package com.local.event.finder.event.tag;

import com.local.event.finder.event.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "event_tags")
@Entity
public class EventTag {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
