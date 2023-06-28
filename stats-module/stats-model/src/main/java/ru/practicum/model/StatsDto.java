package ru.practicum.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class StatsDto {

    String app;
    String uri;
    Long hits;

}
