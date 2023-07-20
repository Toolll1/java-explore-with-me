package ru.practicum.controllers.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.services.EventService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "comments/users/{userId}")
public class CommentControllerPrivate {

    private final EventService service;

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@Valid @RequestBody CommentCreateDto dto,
                                        @PathVariable Long userId,
                                        @PathVariable Long eventId) {

        log.info("Received a request to create a comment " + dto);

        return service.createComment(dto, userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    public CommentFullUpdateDto updateComment(@Valid @RequestBody CommentCreateDto dto,
                                              @RequestParam Long commentId,
                                              @PathVariable Long userId,
                                              @PathVariable Long eventId) {

        log.info("Received a request to update a comment. dto = {}, commentId = {}, userId = {}, eventId = {}", dto, commentId, userId, eventId);

        return service.updateComment(dto, commentId, userId, eventId);
    }

    @DeleteMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@RequestParam Long commentId,
                              @PathVariable Long userId,
                              @PathVariable Long eventId) {

        log.info("Received a request to delete a comment with an id " + commentId);

        service.deleteCommentPrivate(commentId, userId, eventId);
    }
}
