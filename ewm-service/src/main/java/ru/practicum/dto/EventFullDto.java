package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.models.EventState;
import ru.practicum.models.Location;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class EventFullDto {

    private final String annotation;
    private final CategoryDto category;
    private final Integer confirmedRequests;
    private final String createdOn;
    private final String description;
    private final String eventDate;
    private final Long id;
    private final UserShortDto initiator;
    private final Location location;
    private final Boolean paid;
    private final Integer participantLimit;
    private final String publishedOn;
    private final Boolean requestModeration;
    private final EventState state;
    private final String title;
    private final Integer views;
    private List<Object> comments;
}
