package ru.practicum.controllers.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.services.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestControllerPrivate {

    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@RequestParam Long eventId,
                                    @PathVariable Long userId) {

        log.info("Received a request to create a request for params: userId {}, eventId {}", userId, eventId);

        return service.createRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getRequest(@PathVariable Long userId) {

        log.info("Received a request to search for all request for userId {}", userId);

        return service.getRequestDto(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto updateRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {

        log.info("Received a request to update a request. userId = {}, requestId = {}", userId, requestId);

        return service.updateRequest(userId, requestId);
    }
}
