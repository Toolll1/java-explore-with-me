package ru.practicum.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HttpClient.StatsClient;
import ru.practicum.adapters.DateTimeAdapter;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.model.HitDto;
import ru.practicum.model.StatsDto;
import ru.practicum.models.event.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;

import ru.practicum.repositories.EventRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatsClient statsClient;

    public EventFullDto createEventPrivate(EventDto dto, int userId) {

        log.info("Received a request to create a event " + dto);

        eventDateControl(dto.getEventDate(), 2);
        categoryService.findCategoryById(dto.getCategory());

        Event event = eventMapper.dtoToObject(dto);
        event.setCreatedOn(LocalDateTime.now());
        event.setStatus(EventState.PENDING);
        event.setViews(0);
        event.setInitiator(userService.findUserById(userId));
        event.setConfirmedRequests(0);

        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        return EventMapper.objectToFullDto(repository.save(event));
    }

    public List<EventShortDto> getEventsPrivate(int userId, Integer from, Integer size) {

        log.info("Received a request to search for all events for params: userId {}, from {}, size {}", userId, from, size);

        PageRequest pageable = pageableCreator(from, size, null);

        return repository.findAllByInitiatorId(userId, pageable).stream().map(EventMapper::objectToShortDto).collect(Collectors.toList());
    }

    public EventFullDto getEventPrivate(int userId, int eventId) {

        log.info("Received a request to search event for params: userId {}, eventId {}", userId, eventId);

        userService.findUserById(userId);

        Event event = findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ObjectNotFoundException("you are not the initiator of this event");
        } else {
            return EventMapper.objectToFullDto(event);
        }
    }

    public List<EventFullDto> getEventsAdmin(List<Integer> users, List<String> states, List<Integer> categories,
                                             String rangeStart, String rangeEnd, Integer from, Integer size) {

        log.info("Received a request to admin search events for params: users {}, states {}, categories {}, rangeStart {}, " +
                "rangeEnd {}, from {}, size {}", users, states, categories, rangeStart, rangeEnd, from, size);

        PageRequest pageable = pageableCreator(from, size, null);
        Page<Event> eventPage = creatingRequestAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
        Set<Event> eventsSet = eventPage.stream().collect(Collectors.toSet());

        return eventsSet.stream().map(EventMapper::objectToFullDto).collect(Collectors.toList());
    }

    public List<EventShortDto> getEventsPublic(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                               String rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                               Integer size, String ip, String path) {

        log.info("Received a request to public search events for params: text {}, categories {}, paid {}, rangeStart {}, " +
                "rangeEnd {}, onlyAvailable {}, sort {}, from {}, size {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        PageRequest pageable = pageableCreator(from, size, sort);
        Page<Event> eventPage = creatingRequestPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        Set<Event> eventsSet = eventPage.stream().collect(Collectors.toSet());

        createHit(ip, path);

        return eventsSet.stream().map(EventMapper::objectToShortDto).collect(Collectors.toList());
    }

    public EventFullDto getEventPublic(int eventId, String ip, String path) {

        log.info("Received a request to public search event for id {}", eventId);

        Event event = findEventById(eventId);

        if (!event.getStatus().equals(EventState.PUBLISHED)) {
            throw new ObjectNotFoundException("the event must be published");
        }

        ResponseEntity<Object> response = statsClient.getStats(event.getCreatedOn(), LocalDateTime.now(), List.of(path), true);
        ObjectMapper mapper = new ObjectMapper();
        List<StatsDto> statsDto = mapper.convertValue(response.getBody(), new TypeReference<List<StatsDto>>() {
        });

        if (!statsDto.isEmpty()) {

            event.setViews(Math.toIntExact(statsDto.get(0).getHits()));
            repository.save(event);
        }

        createHit(ip, path);

        return EventMapper.objectToFullDto(event);
    }

    public EventFullDto updateEventPrivate(UpdateEventDto dto, int userId, int eventId) {

        log.info("Received a request to Private update a event {}. userId = {}, eventId = {}", dto, userId, eventId);

        userService.findUserById(userId);

        int maximumHoursDeviation = 2;
        Event oldEvent = findEventById(eventId);

        if (!oldEvent.getInitiator().getId().equals(userId)) {
            throw new ObjectNotFoundException("you are not the initiator of this event");
        } else if (oldEvent.getStatus().equals(EventState.PUBLISHED)) {
            throw new ConflictException("you can only change canceled events or events in the state of waiting for moderation");
        }

        Event newEvent = eventValidator(dto, oldEvent, maximumHoursDeviation, false);

        return EventMapper.objectToFullDto(repository.save(newEvent));
    }

    public EventFullDto updateEventAdmin(UpdateEventDto dto, int eventId) {

        log.info("Received a request to Admin update a event {}. eventId = {}", dto, eventId);

        Event event = findEventById(eventId);
        int maximumHoursDeviation = 1;
        Event newEvent = eventValidator(dto, event, maximumHoursDeviation, true);

        return EventMapper.objectToFullDto(repository.save(newEvent));
    }

    public void updateEventRequest(Event event) {

        repository.save(event);
    }

    public Event findEventById(int eventId) {

        Optional<Event> event = repository.findById(eventId);

        if (event.isEmpty()) {
            throw new ObjectNotFoundException("There is no event with this id");
        }

        return event.get();
    }

    private void createHit(String ip, String path) {

        statsClient.createHit(HitDto.builder()
                .app("ewm-main-service")
                .uri(path)
                .ip(ip)
                .timestamp(DateTimeAdapter.dateToString(LocalDateTime.now()))
                .build());
    }

    private void eventDateControl(String eventDate, int hours) {

        if (Objects.requireNonNull(DateTimeAdapter.stringToDate(eventDate)).isBefore(LocalDateTime.now().plusHours(hours))) {
            throw new BadRequestException("the date and time for which the event is scheduled cannot be earlier than " +
                    "two hours from the current moment");
        }
    }

    private PageRequest pageableCreator(Integer from, Integer size, String sort) {

        if (from < 0 || size <= 0) {
            throw new BadRequestException("the from parameter must be greater than or equal to 0; size is greater than 0");
        }
        if (sort == null || sort.isEmpty()) {
            return PageRequest.of(from / size, size);
        }

        switch (sort) {
            case "EVENT_DATE":
                return PageRequest.of(from / size, size, Sort.by("eventDate"));
            case "VIEWS":
                return PageRequest.of(from / size, size, Sort.by("views").descending());
            default:
                throw new BadRequestException("Unknown sort: " + sort);
        }
    }

    private Event eventValidator(UpdateEventDto dto, Event event, int hours, boolean checkAdmin) {

        if (dto.getEventDate() != null) {
            eventDateControl(dto.getEventDate(), hours);
            event.setEventDate(DateTimeAdapter.stringToDate(dto.getEventDate()));
        }
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            if (dto.getAnnotation().length() < 20 || dto.getAnnotation().length() > 2000) {
                throw new BadRequestException("incorrect length of the annotation parameter");
            } else {
                event.setAnnotation(dto.getAnnotation());
            }
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryService.findCategoryById(dto.getCategory()));
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            if (dto.getDescription().length() < 20 || dto.getDescription().length() > 7000) {
                throw new BadRequestException("incorrect length of the description parameter");
            } else {
                event.setDescription(dto.getDescription());
            }
        }
        if (dto.getLocation() != null) {
            event.setLat(dto.getLocation().getLat());
            event.setLon(dto.getLocation().getLon());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            if (dto.getParticipantLimit() < 1) {
                throw new BadRequestException("the participantLimit must be positive");
            }
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            if (!checkAdmin) {
                if (event.getStatus().equals(EventState.CANCELED) && dto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                    event.setStatus(EventState.PENDING);
                } else if (!event.getStatus().equals(EventState.CANCELED) && dto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                    event.setStatus(EventState.CANCELED);
                }
            } else {
                if (event.getStatus().equals(EventState.PENDING) && dto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    event.setStatus(EventState.PUBLISHED);
                } else if (!event.getStatus().equals(EventState.PENDING) && dto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    throw new ConflictException("an event can be published only if it is in the waiting state for publication");
                }
                if (!event.getStatus().equals(EventState.PUBLISHED) && dto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                    event.setStatus(EventState.CANCELED);
                    event.setPublishedOn(LocalDateTime.now());
                } else if (event.getStatus().equals(EventState.PUBLISHED) && dto.getStateAction().equals(StateAction.REJECT_EVENT)) {
                    throw new ConflictException("an event can be rejected only if it has not been published yet");
                }
            }
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            if (dto.getTitle().length() < 3 || dto.getTitle().length() > 120) {
                throw new BadRequestException("incorrect length of the title parameter");
            } else {
                event.setTitle(dto.getTitle());
            }
        }

        return event;
    }

    private Page<Event> creatingRequestPublic(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                              String rangeEnd, Boolean onlyAvailable, PageRequest pageable) {

        return repository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Predicate textOr1 = criteriaBuilder.like(criteriaBuilder.upper(root.get("description")), "%" + text.toUpperCase() + "%");
            Predicate textOr2 = criteriaBuilder.like(criteriaBuilder.upper(root.get("annotation")), "%" + text.toUpperCase() + "%");
            Predicate predicateForGrade = criteriaBuilder.or(textOr1, textOr2);
            predicates.add(predicateForGrade);

            if (categories != null && !categories.isEmpty()) {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("category"));
                for (Integer categoriesId : categories) {
                    in.value(categoriesId);
                }
                predicates.add(in);
            }

            if (paid != null) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("paid"), paid)));
            }

            LocalDateTime start = DateTimeAdapter.stringToDate(rangeStart);
            LocalDateTime end = DateTimeAdapter.stringToDate(rangeEnd);
            if (start != null && end != null && start.isAfter(end)) {
                throw new BadRequestException("the beginning of the range cannot start before the end of the range");
            }
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), Objects.requireNonNullElseGet(start, LocalDateTime::now)));
            if (end != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end));
            }

            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("status"), EventState.PUBLISHED)));

            if (onlyAvailable) {
                predicates.add(criteriaBuilder.and(criteriaBuilder.lessThan(root.get("confirmedRequests"), root.get("participantLimit"))));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    private Page<Event> creatingRequestAdmin(List<Integer> users, List<String> states, List<Integer> categories,
                                             String rangeStart, String rangeEnd, PageRequest pageable) {

        return repository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (users != null && !users.isEmpty() && !(users.size() == 1 && users.get(0) == 0)) {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("initiator"));
                for (Integer initiatorId : users) {
                    in.value(initiatorId);
                }
                predicates.add(in);
            }

            if (states != null && !states.isEmpty()) {

                List<EventState> eventStates = states.stream().map(EventState::valueOf).collect(Collectors.toList());

                CriteriaBuilder.In<EventState> in = criteriaBuilder.in(root.get("status"));
                for (EventState status : eventStates) {
                    in.value(status);
                }
                predicates.add(in);
            }

            if (categories != null && !categories.isEmpty()) {
                CriteriaBuilder.In<Integer> in = criteriaBuilder.in(root.get("category"));
                for (Integer categoriesId : categories) {
                    in.value(categoriesId);
                }
                predicates.add(in);
            }

            LocalDateTime start = DateTimeAdapter.stringToDate(rangeStart);
            LocalDateTime end = DateTimeAdapter.stringToDate(rangeEnd);
            if (start != null && end != null && start.isAfter(end)) {
                throw new BadRequestException("the beginning of the range cannot start before the end of the range");
            }
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), Objects.requireNonNullElseGet(start, LocalDateTime::now)));
            if (end != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
