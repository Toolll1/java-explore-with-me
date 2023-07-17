package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RequestDto {

    private final String created;
    private Long event;
    private final Long id;
    private Long requester;
    private String status;
}
