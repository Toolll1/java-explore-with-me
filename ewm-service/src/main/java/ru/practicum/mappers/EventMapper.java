package ru.practicum.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.adapters.DateTimeAdapter;
import ru.practicum.models.Event;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.models.Location;
import ru.practicum.services.CategoryService;

@Service
@RequiredArgsConstructor
public class EventMapper {

    private final CategoryService categoryService;

    public static EventShortDto objectToShortDto(Event event) {

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .eventDate(DateTimeAdapter.dateToString(event.getEventDate()))
                .category(CategoryMapper.objectToDto(event.getCategory()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .initiator(UserMapper.objectToShortDto(event.getInitiator()))
                .views(event.getViews())
                .build();
    }

    public static EventFullDto objectToFullDto(Event event) {

        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .eventDate(DateTimeAdapter.dateToString(event.getEventDate()))
                .category(CategoryMapper.objectToDto(event.getCategory()))
                .location(new Location(event.getLon(), event.getLat()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(DateTimeAdapter.dateToString(event.getCreatedOn()))
                .initiator(UserMapper.objectToShortDto(event.getInitiator()))
                .publishedOn(event.getPublishedOn() != null ? DateTimeAdapter.dateToString(event.getPublishedOn()) : null)
                .state(event.getStatus())
                .views(event.getViews())
                .build();
    }

    public Event dtoToObject(EventDto dto) {

        return Event.builder()
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(DateTimeAdapter.stringToDate(dto.getEventDate()))
                .category(categoryService.findCategoryById(dto.getCategory()))
                .lon(dto.getLocation().getLon())
                .lat(dto.getLocation().getLat())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .build();
    }
}

