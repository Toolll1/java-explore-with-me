package ru.practicum.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationNewDto;
import ru.practicum.services.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationControllerAdmin {

    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilationAdmin(@Valid @RequestBody CompilationNewDto dto) {

        log.info("Received a request to create a compilation " + dto);

        return service.createCompilationAdmin(dto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilationAdmin(@RequestBody CompilationNewDto dto,
                                                 @PathVariable Long compId) {

        log.info("Received a request to update a compilation with an id " + compId);

        return service.updateCompilationAdmin(dto, compId);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationAdmin(@PathVariable Long compId) {

        log.info("Received a request to delete a compilation with an id " + compId);

        service.deleteCompilationAdmin(compId);
    }
}
