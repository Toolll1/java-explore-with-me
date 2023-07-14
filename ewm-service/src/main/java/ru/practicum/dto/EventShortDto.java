package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EventShortDto {

    private final String annotation;
    private final CategoryDto category;
    private final Integer confirmedRequests;
    private final String eventDate;
    private final Long id;
    private final UserShortDto initiator;
    private final Boolean paid;
    private final String title;
    private final Integer views;
}
