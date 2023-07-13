package ru.practicum.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@Builder
public class HitDto {

    @NotEmpty
    private final String app;
    @NotEmpty
    private final String uri;
    @NotEmpty
    private final String ip;
    @NotEmpty
    private final String timestamp;
}
