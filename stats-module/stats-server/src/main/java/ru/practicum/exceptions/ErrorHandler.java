package ru.practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.controller.StatsController;

import java.util.Map;

@ControllerAdvice(assignableTypes = {StatsController.class})
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleBadRequest(final BadRequestException e) {

        return new ResponseEntity<>(
                Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}
