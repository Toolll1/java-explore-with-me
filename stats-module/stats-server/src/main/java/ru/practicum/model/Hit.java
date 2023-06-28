package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "hits")
public class Hit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hit_id")
    private Integer id;
    @Column(name = "hit_app", nullable = false, length = 320)
    private String app;
    @Column(name = "hit_uri", nullable = false, length = 320)
    private String uri;
    @Column(name = "hit_ip", nullable = false, length = 320)
    private String ip;
    @Column(name = "hit_date")
    private LocalDateTime timestamp;
}
