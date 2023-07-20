package ru.practicum.controllers.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.services.EventService;

import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "comments")
public class CommentControllerPublic {

    private final EventService service;

    @GetMapping("/{commentId}")
    public Object getComment(@PathVariable Long commentId) {

        log.info("Received a request to search comment for commentId {}", commentId);

        return service.getCommentDto(commentId);
    }

    @GetMapping("/events/{eventId}")
    public List<Object> getComments(@PathVariable Long eventId,
                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {

        log.info("Received a request to search for all comments for params: from {}, size {}", from, size);

        return service.getComments(null, eventId, from, size);
    }
}
