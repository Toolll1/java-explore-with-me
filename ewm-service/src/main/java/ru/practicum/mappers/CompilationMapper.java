package ru.practicum.mappers;

import org.springframework.stereotype.Service;
import ru.practicum.models.Compilation;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationNewDto;
import ru.practicum.services.EventService;

import java.util.ArrayList;
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

    public Compilation newDtoToObject(CompilationNewDto dto, EventService eventService) {

        return Compilation.builder()
                .title(dto.getTitle())
                .events(dto.getEvents() != null ? dto.getEvents().stream().map(eventService::findEventById).collect(Collectors.toList()) : new ArrayList<>())
                .pinned(dto.getPinned())
                .build();
    }
}
