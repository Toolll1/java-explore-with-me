package ru.practicum.models;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private final Long id;
    @Column(name = "name", nullable = false, length = 320)
    private final String name;
    @Column(name = "email", nullable = false, length = 320, unique = true)
    private final String email;
}
