package ru.practicum.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventUpdateDto;
import ru.practicum.services.EventService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventControllerAdmin {

    private final EventService service;

    @DeleteMapping("/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long eventId,
                              @PathVariable Long commentId) {

        log.info("(Admin) Received a request to delete a comment with an id {} for event id {}", commentId, eventId);

        service.deleteCommentAdmin(commentId, eventId);
    }

    @GetMapping
    public List<EventFullDto> getEventsAdmin(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<String> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(value = "rangeStart", defaultValue = "no date") String rangeStart,
                                             @RequestParam(value = "rangeEnd", defaultValue = "no date") String rangeEnd,
                                             @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size,
                                             @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer commentFrom,
                                             @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer commentSize) {

        log.info("Received a request to admin search events for params: users {}, states {}, categories {}, rangeStart {}, " +
                "rangeEnd {}, from {}, size {}", users, states, categories, rangeStart, rangeEnd, from, size);

        return service.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size, commentFrom, commentSize);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@RequestBody EventUpdateDto dto,
                                         @PathVariable Long eventId,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer commentFrom,
                                         @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer commentSize) {

        log.info("Received a request to Admin update a event {}. eventId = {}", dto, eventId);

        return service.updateEventAdmin(dto, eventId, commentFrom, commentSize);
    }
}
