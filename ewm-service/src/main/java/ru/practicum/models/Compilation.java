package ru.practicum.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private final Long id;
    @ManyToMany(cascade = CascadeType.ALL, targetEntity = Event.class)
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id", referencedColumnName = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "event_id"))
    private List<Event> events;
    @Column(name = "pinned")
    private Boolean pinned;
    @Column(name = "title", nullable = false)
    private String title;
}
