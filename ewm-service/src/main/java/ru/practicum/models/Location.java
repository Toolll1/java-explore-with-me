package ru.practicum.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Location {
    private float lon;
    private float lat;
}
