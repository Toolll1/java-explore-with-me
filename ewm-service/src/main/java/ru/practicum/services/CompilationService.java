package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.models.compilations.Compilation;
import ru.practicum.models.compilations.CompilationDto;
import ru.practicum.models.compilations.CompilationMapper;
import ru.practicum.models.compilations.NewCompilationDto;
import ru.practicum.repositories.CompilationRepository;

import java.util.List;
import java.util.Optional;
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

        log.info("Received a request to create a compilation " + dto);

        if (dto.getPinned() == null) {
            dto.setPinned(false);
        }

        eventVerification(dto.getEvents());

        Compilation compilation = repository.save(mapper.newDtoToObject(dto, eventService));

        return CompilationMapper.objectToDto(compilation);
    }

    public void deleteCompilationAdmin(int compId) {

        log.info("Received a request to delete a compilation with an id " + compId);

        findCompilationById(compId);

        repository.deleteById(compId);
    }

    public CompilationDto updateCompilationAdmin(NewCompilationDto dto, int compId) {

        log.info("Received a request to update a compilation with an id " + compId);

        Compilation compilation = findCompilationById(compId);

        if (dto.getTitle() != null && !dto.getTitle().isEmpty() && !dto.getTitle().isBlank()) {

            if (dto.getTitle().length() > 50) {

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

        log.info("Received a request to public search compilation for params: pinned {}, from {}, size {}", pinned, from, size);

        PageRequest pageable = pageableCreator(from, size);

        List<Compilation> compilations = repository.findAllByPinned(pinned, pageable);

        return compilations.stream().map(CompilationMapper::objectToDto).collect(Collectors.toList());
    }

    public CompilationDto findDtoById(int compId) {

        log.info("Received a request to public search compilation for id {}", compId);

        return CompilationMapper.objectToDto(findCompilationById(compId));
    }

    private Compilation findCompilationById(int compId) {

        Optional<Compilation> compilation = repository.findById(compId);

        if (compilation.isEmpty()) {
            throw new ObjectNotFoundException("There is no compilation with this id");
        } else {
            return compilation.get();
        }
    }

    private PageRequest pageableCreator(Integer from, Integer size) {

        if (from < 0 || size <= 0) {
            throw new BadRequestException("the from parameter must be greater than or equal to 0; size is greater than 0");
        }

        return PageRequest.of(from / size, size);
    }

    private void eventVerification(List<Integer> events) {

        if (events != null) {
            for (Integer eventId : events) {

                eventService.findEventById(eventId);
            }
        }
    }
}
