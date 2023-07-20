package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommentFullDto {

    private final Long id;
    private final String createdOn;
    private final UserShortDto commentator;
    private final String text;
}
