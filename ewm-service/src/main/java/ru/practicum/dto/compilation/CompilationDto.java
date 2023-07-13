package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.event.EventShortDto;

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
