package ru.practicum.controllers.publicControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.services.CompilationService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationControllerPublic {

    private final CompilationService service;

    @GetMapping
    public List<CompilationDto> getCompilationPublic(@RequestParam(required = false, defaultValue = "false") Boolean pinned,
                                                     @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {

        log.info("Received a request to public search compilation for params: pinned {}, from {}, size {}", pinned, from, size);

        return service.getCompilationPublic(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationByIdPublic(@PathVariable Long compId) {

        log.info("Received a request to public search compilation for id {}", compId);

        return service.findDtoById(compId);
    }
}
