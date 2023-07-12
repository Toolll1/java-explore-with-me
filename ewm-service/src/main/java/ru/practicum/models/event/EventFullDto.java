package ru.practicum.models.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.models.category.CategoryDto;
import ru.practicum.models.location.Location;
import ru.practicum.models.user.UserShortDto;

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
    private final Integer id;
    private final UserShortDto initiator;
    private final Location location;
    private final Boolean paid;
    private final Integer participantLimit;
    private final String publishedOn;
    private final Boolean requestModeration;
    private final EventState state;
    private final String title;
    private final Integer views;
}
