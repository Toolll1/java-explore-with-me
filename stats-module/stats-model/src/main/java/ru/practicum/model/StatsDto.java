package ru.practicum.model;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class StatsDto {

    private final String app;
    private final String uri;
    private final Long hits;
}
