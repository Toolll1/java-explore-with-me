package ru.practicum.models.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.models.category.Category;
import ru.practicum.models.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private final Integer id;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User initiator;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column(name = "lon")
    private Float lon;
    @Column(name = "lat")
    private Float lat;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Column(name = "title", nullable = false)
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventState status;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "views")
    private Integer views;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;
}
