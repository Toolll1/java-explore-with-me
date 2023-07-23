package ru.practicum.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private final Long id;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "update_on")
    private LocalDateTime updateOn;
    @Column(name = "comment_text", nullable = false, length = 7000)
    private String text;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User commentator;
    @Column(name = "parent_comment_id")
    private Long parentId;
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, targetEntity = Comment.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", referencedColumnName = "comment_id")
    private List<Comment> subComments = new ArrayList<>();
}
