package ru.practicum.controllers.publicControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.services.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventControllerPublic {

    private final EventService service;

    @GetMapping
    public List<EventShortDto> getEventsPublic(@RequestParam(value = "text", defaultValue = "") String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(value = "rangeStart", defaultValue = "no date") String rangeStart,
                                               @RequestParam(value = "rangeEnd", defaultValue = "no date") String rangeEnd,
                                               @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(value = "sort", defaultValue = "") String sort,
                                               @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size,
                                               HttpServletRequest request) {

        log.info("Received a request to public search events for params: text {}, categories {}, paid {}, rangeStart {}, " +
                        "rangeEnd {}, onlyAvailable {}, sort {}, from {}, size {}", text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);

        return service.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping("/{id}")
    public EventFullDto getEventPublic(@PathVariable Long id, HttpServletRequest request) {

        log.info("Received a request to public search event for id {}", id);

        return service.getEventPublic(id, request.getRemoteAddr(), request.getRequestURI());
    }
}
