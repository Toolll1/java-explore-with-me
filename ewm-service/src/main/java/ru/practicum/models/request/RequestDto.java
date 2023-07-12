package ru.practicum.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RequestDto {

    private final String created;
    private Integer event;
    private final Integer id;
    private Integer requester;
    private String status;
}
