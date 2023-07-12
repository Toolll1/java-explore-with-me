package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.models.event.EventDto;
import ru.practicum.models.event.EventFullDto;
import ru.practicum.models.event.EventShortDto;
import ru.practicum.models.event.UpdateEventDto;
import ru.practicum.services.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class EventController {

    private final EventService service;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventPrivate(@Valid @RequestBody EventDto dto,
                                           @PathVariable int userId) {

        return service.createEventPrivate(dto, userId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsPublic(@RequestParam(value = "text", defaultValue = "") String text,
                                               @RequestParam(required = false) List<Integer> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(value = "rangeStart", defaultValue = "no date") String rangeStart,
                                               @RequestParam(value = "rangeEnd", defaultValue = "no date") String rangeEnd,
                                               @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(value = "sort", defaultValue = "") String sort,
                                               @RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size,
                                               HttpServletRequest request) {

        return service.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventPublic(@PathVariable int id, HttpServletRequest request) {

        return service.getEventPublic(id, request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEventsPrivate(@PathVariable int userId,
                                                @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return service.getEventsPrivate(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getEventPrivate(@PathVariable int userId,
                                        @PathVariable int eventId) {

        return service.getEventPrivate(userId, eventId);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getEventsAdmin(@RequestParam(required = false) List<Integer> users,
                                             @RequestParam(required = false) List<String> states,
                                             @RequestParam(required = false) List<Integer> categories,
                                             @RequestParam(value = "rangeStart", defaultValue = "no date") String rangeStart,
                                             @RequestParam(value = "rangeEnd", defaultValue = "no date") String rangeEnd,
                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size) {

        return service.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventPrivate(@RequestBody UpdateEventDto dto,
                                           @PathVariable int userId,
                                           @PathVariable int eventId) {

        return service.updateEventPrivate(dto, userId, eventId);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEventAdmin(@RequestBody UpdateEventDto dto,
                                         @PathVariable int eventId) {

        return service.updateEventAdmin(dto, eventId);
    }
}
