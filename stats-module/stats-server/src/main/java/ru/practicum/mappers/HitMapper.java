package ru.practicum.mappers;

import org.springframework.stereotype.Service;
import ru.practicum.model.Hit;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class HitMapper {

    public static HitDto objectToDto(Hit hit) {

        return HitDto.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(String.valueOf(hit.getTimestamp()))
                .build();
    }

    public Hit dtoToObject(HitDto dto) {

        return Hit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(LocalDateTime.parse(dto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
