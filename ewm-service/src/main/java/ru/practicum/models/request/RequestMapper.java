package ru.practicum.models.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.adapters.DateTimeAdapter;
import ru.practicum.services.EventService;
import ru.practicum.services.UserService;

@Service
@RequiredArgsConstructor
public class RequestMapper {

    private final EventService eventService;
    private final UserService userService;

    public Request dtoToObject(RequestDto dto) {

        return Request.builder()
                .id(dto.getId())
                .status(RequestState.valueOf(dto.getStatus()))
                .created(DateTimeAdapter.stringToDate(dto.getCreated()))
                .event(eventService.findEventById(dto.getEvent()))
                .requester(userService.findUserById(dto.getRequester()))
                .build();
    }

    public static RequestDto objectToDto(Request request) {

        return RequestDto.builder()
                .id(request.getId())
                .status(request.getStatus().toString())
                .created(DateTimeAdapter.dateToString(request.getCreated()))
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .build();
    }
}
