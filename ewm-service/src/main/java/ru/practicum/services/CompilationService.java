package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.models.compilation.Compilation;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.repositories.CompilationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationRepository repository;
    private final CompilationMapper mapper;
    private final EventService eventService;

    public CompilationDto createCompilationAdmin(NewCompilationDto dto) {

        if (dto.getPinned() == null) {
            dto.setPinned(false);
        }

        eventVerification(dto.getEvents());

        Compilation compilation = repository.save(mapper.newDtoToObject(dto, eventService));

        return CompilationMapper.objectToDto(compilation);
    }

    public void deleteCompilationAdmin(Long compId) {

        findCompilationById(compId);

        repository.deleteById(compId);
    }

    public CompilationDto updateCompilationAdmin(NewCompilationDto dto, Long compId) {

        Compilation compilation = findCompilationById(compId);

        if (dto.getTitle() != null && !dto.getTitle().isEmpty() && !dto.getTitle().isBlank()) {

            if (dto.getTitle().length() > 50) {
                log.info("method updateCompilationAdmin - " +
                        "BadRequestException \"the length of the title field must be in the range from 0 to 50 characters\"");
                throw new BadRequestException("the length of the title field must be in the range from 0 to 50 characters");
            }

            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {

            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {

            eventVerification(dto.getEvents());
            compilation.setEvents(dto.getEvents().stream().map(eventService::findEventById).collect(Collectors.toList()));
        }

        return CompilationMapper.objectToDto(repository.save(compilation));
    }

    public List<CompilationDto> getCompilationPublic(Boolean pinned, Integer from, Integer size) {

        PageRequest pageable = pageableCreator(from, size);

        List<Compilation> compilations = repository.findAllByPinned(pinned, pageable);

        return compilations.stream().map(CompilationMapper::objectToDto).collect(Collectors.toList());
    }

    public CompilationDto findDtoById(Long compId) {

        return CompilationMapper.objectToDto(findCompilationById(compId));
    }

    private Compilation findCompilationById(Long compId) {

        return repository.findById(compId).orElseThrow(() -> new ObjectNotFoundException("There is no compilation with this id"));
    }

    private PageRequest pageableCreator(Integer from, Integer size) {

        if (from < 0 || size <= 0) {
            log.info("method pageableCreator - " +
                    "BadRequestException \"the from parameter must be greater than or equal to 0; size is greater than 0\"");
            throw new BadRequestException("the from parameter must be greater than or equal to 0; size is greater than 0");
        }

        return PageRequest.of(from / size, size);
    }

    private void eventVerification(List<Long> events) {

        if (events != null) {
            for (Long eventId : events) {

                eventService.findEventById(eventId);
            }
        }
    }
}
