package ru.practicum.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.services.EventService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventControllerAdmin {

    private final EventService service;

    @GetMapping
    public List<EventFullDto> getEventsAdmin(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<String> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(value = "rangeStart", defaultValue = "no date") String rangeStart,
                                             @RequestParam(value = "rangeEnd", defaultValue = "no date") String rangeEnd,
                                             @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {

        log.info("Received a request to admin search events for params: users {}, states {}, categories {}, rangeStart {}, " +
                "rangeEnd {}, from {}, size {}", users, states, categories, rangeStart, rangeEnd, from, size);

        return service.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@RequestBody UpdateEventDto dto,
                                         @PathVariable Long eventId) {

        log.info("Received a request to Admin update a event {}. eventId = {}", dto, eventId);

        return service.updateEventAdmin(dto, eventId);
    }
}
