package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hits")
public class Hit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hit_id")
    private final Long id;
    @Column(name = "hit_app", nullable = false, length = 320)
    private final String app;
    @Column(name = "hit_uri", nullable = false, length = 320)
    private final String uri;
    @Column(name = "hit_ip", nullable = false, length = 320)
    private final String ip;
    @Column(name = "hit_date")
    private final LocalDateTime timestamp;
}
