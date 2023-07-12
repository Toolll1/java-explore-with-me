package ru.practicum.models.compilations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.models.event.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CompilationDto {

    private final Integer id;
    private final List<EventShortDto> events;
    private final Boolean pinned;
    private final String title;
}
