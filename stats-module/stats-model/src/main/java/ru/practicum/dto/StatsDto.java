package ru.practicum.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsDto {

    private String app;
    private String uri;
    private Long hits;
}
