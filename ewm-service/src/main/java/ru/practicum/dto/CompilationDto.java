package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CompilationDto {

    private final Long id;
    private final List<EventShortDto> events;
    private final Boolean pinned;
    private final String title;
}
