package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RequestUpdateResultDto {

    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
