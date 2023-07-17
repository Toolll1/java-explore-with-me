package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.model.Hit;
import ru.practicum.dto.HitDto;
import ru.practicum.mappers.HitMapper;
import ru.practicum.dto.StatsDto;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository repository;
    private final HitMapper mapper;

    public HitDto createHit(HitDto dto) {

        Hit hit = repository.save(mapper.dtoToObject(dto));

        log.info("I received a request to create a hit " + hit);

        return HitMapper.objectToDto(hit);
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (start.isAfter(end)) {
            log.info("method getStats - BadRequestException \"invalid time interval\"");
            throw new BadRequestException("Invalid time interval");
        }

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return repository.findAllUniqueStats(start, end);
            } else {
                return repository.findAllStats(start, end);
            }
        } else {
            if (unique) {
                return repository.findAllUniqueStatsByUris(start, end, uris);
            } else {
                return repository.findAllStatsByUris(start, end, uris);
            }
        }
    }
}
