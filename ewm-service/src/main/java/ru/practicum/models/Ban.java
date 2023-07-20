package ru.practicum.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Entity
@Table(name = "ban")
public class Ban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ban_id")
    private final Long id;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "end_of_ban")
    private LocalDateTime endOfBan;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User commentator;
}
