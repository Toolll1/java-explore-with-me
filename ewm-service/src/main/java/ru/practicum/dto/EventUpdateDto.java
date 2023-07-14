package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.models.EventStateAction;
import ru.practicum.models.Location;

@Data
@AllArgsConstructor
@Builder
public class EventUpdateDto {

    private final String annotation;
    private final Long category;
    private final String description;
    private String eventDate;
    private final Location location;
    private final Boolean paid;
    private final Integer participantLimit;
    private final Boolean requestModeration;
    private EventStateAction stateAction;
    private final String title;
}
