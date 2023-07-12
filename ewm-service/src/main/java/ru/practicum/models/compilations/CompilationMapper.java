package ru.practicum.models.compilations;

import org.springframework.stereotype.Service;
import ru.practicum.models.event.Event;
import ru.practicum.models.event.EventMapper;
import ru.practicum.services.EventService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationMapper {

    public static CompilationDto objectToDto(Compilation compilation) {

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream().map(EventMapper::objectToShortDto).collect(Collectors.toList()))
                .pinned(compilation.getPinned())
                .build();
    }

    public Compilation newDtoToObject(NewCompilationDto dto, EventService eventService) {

        List<Event> events;

        if (dto.getEvents() != null) {
            events = dto.getEvents().stream().map(eventService::findEventById).collect(Collectors.toList());
        } else {
            events = new ArrayList<>();
        }

        return Compilation.builder()
                .title(dto.getTitle())
                .events(events)
                .pinned(dto.getPinned())
                .build();
    }
}
