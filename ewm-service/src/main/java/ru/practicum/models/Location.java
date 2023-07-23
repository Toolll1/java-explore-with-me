package ru.practicum.models;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public class Location {
    private float lon;
    private float lat;
}
