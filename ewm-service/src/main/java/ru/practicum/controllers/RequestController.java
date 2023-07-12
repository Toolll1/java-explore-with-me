package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.models.request.RequestDto;
import ru.practicum.models.request.RequestUpdateResult;
import ru.practicum.models.request.UpdateRequestDto;
import ru.practicum.services.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}")
public class RequestController {

    private final RequestService service;

    @GetMapping("/events/{eventId}/requests")
    public List<RequestDto> getEventRequestPrivate(@PathVariable int userId, @PathVariable int eventId) {

        return service.getEventRequestPrivate(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public RequestUpdateResult updateRequestPrivate(@Valid @RequestBody UpdateRequestDto dto,
                                                    @PathVariable int userId,
                                                    @PathVariable int eventId) {

        return service.updateRequestPrivate(dto, userId, eventId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@RequestParam int eventId,
                                    @PathVariable int userId) {

        return service.createRequest(userId, eventId);
    }

    @GetMapping("/requests")
    public List<RequestDto> getRequest(@PathVariable int userId) {

        return service.getRequestDto(userId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public RequestDto updateRequest(@PathVariable int userId,
                                    @PathVariable int requestId) {

        return service.updateRequest(userId, requestId);
    }
}
