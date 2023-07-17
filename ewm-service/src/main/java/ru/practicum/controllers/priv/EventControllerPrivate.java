package ru.practicum.controllers.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.EventUpdateDto;
import ru.practicum.dto.RequestDto;
import ru.practicum.dto.RequestUpdateResultDto;
import ru.practicum.dto.RequestUpdateDto;
import ru.practicum.services.EventService;
import ru.practicum.services.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventControllerPrivate {

    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getEventRequestPrivate(@PathVariable Long userId, @PathVariable Long eventId) {

        log.info("Received a request to search for all request for userId {} and eventId {}", userId, eventId);

        return requestService.getEventRequestPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestUpdateResultDto updateRequestPrivate(@Valid @RequestBody RequestUpdateDto dto,
                                                       @PathVariable Long userId,
                                                       @PathVariable Long eventId) {

        log.info("Received a request to Private update a request {}. userId = {}, eventId = {}", dto, userId, eventId);

        return requestService.updateRequestPrivate(dto, userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventPrivate(@Valid @RequestBody EventDto dto,
                                           @PathVariable Long userId) {

        log.info("Received a request to create a event " + dto);

        return eventService.createEventPrivate(dto, userId);
    }

    @GetMapping
    public List<EventShortDto> getEventsPrivate(@PathVariable Long userId,
                                                @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {

        log.info("Received a request to search for all events for params: userId {}, from {}, size {}", userId, from, size);

        return eventService.getEventsPrivate(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventPrivate(@PathVariable Long userId,
                                        @PathVariable Long eventId) {

        log.info("Received a request to search event for params: userId {}, eventId {}", userId, eventId);

        return eventService.getEventPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventPrivate(@RequestBody EventUpdateDto dto,
                                           @PathVariable Long userId,
                                           @PathVariable Long eventId) {

        log.info("Received a request to Private update a event {}. userId = {}, eventId = {}", dto, userId, eventId);

        return eventService.updateEventPrivate(dto, userId, eventId);
    }

}
