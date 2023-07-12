package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.models.compilations.CompilationDto;
import ru.practicum.models.compilations.NewCompilationDto;
import ru.practicum.services.CompilationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CompilationController {

    private final CompilationService service;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilationAdmin(@Valid @RequestBody NewCompilationDto dto) {

        return service.createCompilationAdmin(dto);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto updateCompilationAdmin(@RequestBody NewCompilationDto dto,
                                                 @PathVariable int compId) {

        return service.updateCompilationAdmin(dto, compId);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationAdmin(@PathVariable int compId) {

        service.deleteCompilationAdmin(compId);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilationPublic(@RequestParam(required = false, defaultValue = "false") Boolean pinned,
                                                     @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return service.getCompilationPublic(pinned, from, size);
    }

    @GetMapping("compilations/{compId}")
    public CompilationDto getCompilationByIdPublic(@PathVariable int compId) {

        return service.findDtoById(compId);
    }
}
