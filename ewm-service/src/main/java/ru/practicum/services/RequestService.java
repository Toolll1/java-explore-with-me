package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.adapters.DateTimeAdapter;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.models.event.Event;
import ru.practicum.models.event.EventState;
import ru.practicum.models.request.*;
import ru.practicum.repositories.RequestRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository repository;
    private final RequestMapper mapper;
    private final EventService eventService;
    private final UserService userService;

    public RequestDto createRequest(int userId, int eventId) {

        log.info("Received a request to create a request for params: userId {}, eventId {}", userId, eventId);

        userService.findUserById(userId);
        eventService.findEventById(eventId);

        RequestDto dto = newRequestValidate(userId, eventId);
        Request request = repository.save(mapper.dtoToObject(dto));

        updateEventRequest(request, 1);

        return RequestMapper.objectToDto(request);
    }

    public List<RequestDto> getRequestDto(int userId) {

        log.info("Received a request to search for all request for userId {}", userId);

        userService.findUserById(userId);

        return repository.findByRequesterId(userId).stream().map(RequestMapper::objectToDto).collect(Collectors.toList());
    }

    public List<RequestDto> getEventRequestPrivate(int userId, int eventId) {

        log.info("Received a request to search for all request for userId {} and eventId {}", userId, eventId);

        userService.findUserById(userId);
        Event event = eventService.findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("you cannot upload requests to confirm participation in an event initiated by another user");
        }

        return repository.findByEventInitiatorIdAndEventId(userId, eventId).stream().map(RequestMapper::objectToDto)
                .sorted(Comparator.comparing(RequestDto::getStatus)).collect(Collectors.toList());
    }

    public RequestDto updateRequest(int userId, int requestId) {

        log.info("Received a request to update a request. userId = {}, requestId = {}", userId, requestId);

        userService.findUserById(userId);
        Request request = findRequestById(requestId);

        updateEventRequest(request, -1);

        request.setStatus(RequestState.CANCELED);

        return RequestMapper.objectToDto(repository.save(request));
    }

    public RequestUpdateResult updateRequestPrivate(UpdateRequestDto dto, int userId, int eventId) {

        log.info("Received a request to Private update a request {}. userId = {}, eventId = {}", dto, userId, eventId);

        userService.findUserById(userId);
        Event event = eventService.findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("you cannot update requests to confirm participation in an event initiated by another user");
        }

        requestProcessing(event, dto);

        List<Integer> ids = dto.getRequestIds();

        List<RequestDto> confirmedRequests = repository
                .findAllByIdInAndEventInitiatorIdAndEventIdAndStatus(ids, userId, eventId, RequestState.CONFIRMED)
                .stream()
                .map(RequestMapper::objectToDto)
                .collect(Collectors.toList());
        List<RequestDto> rejectedRequests = repository
                .findAllByIdInAndEventInitiatorIdAndEventIdAndStatus(ids, userId, eventId, RequestState.REJECTED)
                .stream()
                .map(RequestMapper::objectToDto)
                .collect(Collectors.toList());

        return RequestUpdateResult.builder().confirmedRequests(confirmedRequests).rejectedRequests(rejectedRequests).build();
    }

    private void requestProcessing(Event event, UpdateRequestDto dto) {

        for (Integer requestId : dto.getRequestIds()) {

            Request request = findRequestById(requestId);

            if (!request.getStatus().equals(RequestState.PENDING)) {
                throw new ConflictException("an application with an id " + requestId + " is in a status other than PENDING");
            }

            switch (dto.getStatus()) {
                case CONFIRMED:
                    canceledRequests(event);
                    request.setStatus(RequestState.CONFIRMED);
                    repository.save(request);
                    updateEventRequest(request, 1);
                    break;
                case REJECTED:
                    request.setStatus(RequestState.REJECTED);
                    repository.save(request);
                    updateEventRequest(request, -1);
                    break;
                default:
                    throw new BadRequestException("don't do that");
            }
        }
    }

    private void canceledRequests(Event event) {

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {

            List<Request> canceledRequests = repository.findAllByEventIdAndStatus(event.getId(), RequestState.PENDING).stream()
                    .peek(request -> request.setStatus(RequestState.CANCELED))
                    .collect(Collectors.toList());

            repository.saveAll(canceledRequests);

            throw new ConflictException("the limit on applications for this event has already been reached");
        }
    }

    private Request findRequestById(int requestId) {

        Optional<Request> request = repository.findById(requestId);

        if (request.isEmpty()) {
            throw new ObjectNotFoundException("There is no request with this id");
        } else {
            return request.get();
        }
    }

    private void updateEventRequest(Request request, int change) {

        if (request.getStatus().equals(RequestState.CONFIRMED)) {
            Event event = request.getEvent();
            event.setConfirmedRequests(event.getConfirmedRequests() + change);

            eventService.updateEventRequest(event);
        }
    }

    private RequestDto newRequestValidate(int requesterId, int eventId) {

        RequestDto dto = RequestDto.builder()
                .requester(requesterId)
                .event(eventId)
                .created(DateTimeAdapter.dateToString(LocalDateTime.now()))
                .build();

        Optional<Request> checkRequest = repository.findAllByEventIdAndRequesterId(dto.getEvent(), requesterId);
        Event event = eventService.findEventById(dto.getEvent());

        if (checkRequest.isPresent()) {
            throw new ConflictException("you cannot add a repeat request");
        }
        if (event.getInitiator().getId().equals(requesterId)) {
            throw new ConflictException("the initiator of the event cannot add a request to participate in his event");
        }
        if (!event.getStatus().equals(EventState.PUBLISHED)) {
            throw new ConflictException("you cannot participate in an unpublished event");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException("the event has reached the limit of participation requests");
        }
        if (event.getParticipantLimit() == 0) {
            dto.setStatus("CONFIRMED");
        } else if (event.getRequestModeration()) {
            dto.setStatus("PENDING");
        } else {
            dto.setStatus("CONFIRMED");
        }

        return dto;
    }
}
