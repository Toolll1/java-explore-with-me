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
import ru.practicum.dto.*;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.mappers.CommentMapper;
import ru.practicum.mappers.EventMapper;
import ru.practicum.models.*;

import javax.persistence.criteria.*;
import javax.validation.constraints.Min;

import ru.practicum.repositories.BanRepository;
import ru.practicum.repositories.CommentRepository;
import ru.practicum.repositories.EventRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final BanRepository banRepository;
    private final CommentRepository commentRepository;
    private final EventMapper eventMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatsClient statsClient;

    public CommentFullDto createSubComment(CommentCreateDto dto, Long userId, Long commentId) {

        Comment comment = findCommentById(commentId);
        User commentator = userService.findUserById(userId);

        banControl(userId);

        Comment subComment = commentMapper.dtoToObject(dto);

        subComment.setCreatedOn(LocalDateTime.now());
        subComment.setCommentator(commentator);

        Comment newSubComment = commentRepository.save(subComment);

        if (comment.getSubComments() == null) {
            comment.setSubComments(List.of(newSubComment));
        } else {
            comment.getSubComments().add(newSubComment);
        }

        return CommentMapper.objectToFullDto(newSubComment);
    }

    public CommentFullDto createComment(CommentCreateDto dto, Long userId, Long eventId) {

        User commentator = userService.findUserById(userId);
        Event event = findEventById(eventId);

        if (!event.getStatus().equals(EventState.PUBLISHED)) {
            throw new BadRequestException("you can only comment on published events");
        }

        banControl(userId);

        Comment comment = commentMapper.dtoToObject(dto);

        comment.setCreatedOn(LocalDateTime.now());
        comment.setCommentator(commentator);
        comment.setEvent(event);

        Comment newComment = commentRepository.save(comment);

        return CommentMapper.objectToFullDto(newComment);
    }

    public CommentFullDto updateComment(CommentCreateDto dto, Long commentId, Long userId, Long eventId) {

        Comment comment = availabilityControl(commentId, userId, eventId);

        comment.setUpdateOn(LocalDateTime.now());
        comment.setText(dto.getText());

        return CommentMapper.objectToFullDto(commentRepository.save(comment));
    }

    public void deleteCommentPrivate(Long commentId, Long userId, Long eventId) {

        availabilityControl(commentId, userId, eventId);

        commentRepository.deleteById(commentId);
    }

    public CommentFullDto getCommentDto(Long commentId) {

        Comment comment = findCommentById(commentId);

        return CommentMapper.objectToFullDto(comment);
    }

    public List<CommentFullDto> getComments(Event event, Long eventId, Integer from, Integer size) {

        List<Comment> comments;

        if (event == null) {
            findEventById(eventId);

            PageRequest pageable = pageableCreator(from, size, "COMMENT_DATE");
            comments = commentRepository.findAllByEventId(eventId, pageable);
        } else {
            comments = event.getComments().stream().sorted(Comparator.comparing(Comment::getCreatedOn))
                    .skip(from / size).limit(size).collect(Collectors.toList());
        }

        return comments.stream().map(CommentMapper::objectToFullDto).collect(Collectors.toList());
    }

    public void deleteCommentAdmin(Long commentId, Long eventId) {

        Comment comment = findCommentById(commentId);
        findEventById(eventId);

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new BadRequestException("this event does not have a comment with this id");
        }

        commentRepository.deleteById(commentId);
    }

    public EventFullDto createEventPrivate(EventDto dto, Long userId) {

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

        return EventMapper.objectToFullDto(eventRepository.save(event), new ArrayList<>());
    }

    public List<EventShortDto> getEventsPrivate(Long userId, Integer from, Integer size) {

        PageRequest pageable = pageableCreator(from, size, null);

        return eventRepository.findAllByInitiatorId(userId, pageable).stream().map(EventMapper::objectToShortDto).collect(Collectors.toList());
    }

    public EventFullDto getEventPrivate(Long userId, Long eventId, Integer commentFrom, Integer commentSize) {

        userService.findUserById(userId);

        Event event = findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            log.info("method getEventPrivate - ObjectNotFoundException \"you are not the initiator of this event\"");
            throw new ObjectNotFoundException("you are not the initiator of this event");
        } else {


            return EventMapper.objectToFullDto(event, getComments(event, null, commentFrom, commentSize));
        }
    }

    public List<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                             String rangeStart, String rangeEnd, Integer from, Integer size, @Min(0) Integer commentFrom, @Min(1) Integer commentSize) {

        PageRequest pageable = pageableCreator(from, size, null);
        Page<Event> eventPage = creatingRequestAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
        Set<Event> eventsSet = eventPage.stream().collect(Collectors.toSet());

        return eventsSet.stream()
                .map(x -> EventMapper.objectToFullDto(x, getComments(x, null, commentFrom, commentSize)))
                .sorted(Comparator.comparing(EventFullDto::getId))
                .collect(Collectors.toList());
    }

    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, String rangeStart,
                                               String rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                               Integer size, String ip, String path) {

        PageRequest pageable = pageableCreator(from, size, sort);
        Page<Event> eventPage = creatingRequestPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        Set<Event> eventsSet = eventPage.stream().collect(Collectors.toSet());

        createHit(ip, path);

        return eventsSet.stream().map(EventMapper::objectToShortDto).collect(Collectors.toList());
    }

    public EventFullDto getEventPublic(Long eventId, String ip, String path, @Min(0) Integer commentFrom, @Min(1) Integer commentSize) {

        Event event = findEventById(eventId);

        if (!event.getStatus().equals(EventState.PUBLISHED)) {
            log.info("method getEventPublic - ObjectNotFoundException \"the event must be published\"");
            throw new ObjectNotFoundException("the event must be published");
        }

        ResponseEntity<Object> response = statsClient.getStats(event.getCreatedOn(), LocalDateTime.now(), List.of(path), true);
        ObjectMapper mapper = new ObjectMapper();
        List<StatsDto> statsDto = mapper.convertValue(response.getBody(), new TypeReference<List<StatsDto>>() {
        });

        if (!statsDto.isEmpty()) {

            event.setViews(Math.toIntExact(statsDto.get(0).getHits()));
            eventRepository.save(event);
        }

        createHit(ip, path);

        return EventMapper.objectToFullDto(event, getComments(event, null, commentFrom, commentSize));
    }

    public EventFullDto updateEventPrivate(EventUpdateDto dto, Long userId, Long eventId, @Min(0) Integer commentFrom, @Min(1) Integer commentSize) {

        userService.findUserById(userId);

        int maximumHoursDeviation = 2;
        Event oldEvent = findEventById(eventId);

        if (!oldEvent.getInitiator().getId().equals(userId)) {
            log.info("method updateEventPrivate - ObjectNotFoundException \"you are not the initiator of this event\"");
            throw new ObjectNotFoundException("you are not the initiator of this event");
        } else if (oldEvent.getStatus().equals(EventState.PUBLISHED)) {
            log.info("method updateEventPrivate - ObjectNotFoundException \"you can only change canceled events or " +
                    "events in the state of waiting for moderation\"");
            throw new ConflictException("you can only change canceled events or events in the state of waiting for moderation");
        }

        Event event = eventValidator(dto, oldEvent, maximumHoursDeviation, false);
        eventRepository.save(event);

        return EventMapper.objectToFullDto(event, getComments(event, null, commentFrom, commentSize));
    }

    public EventFullDto updateEventAdmin(EventUpdateDto dto, Long eventId, @Min(0) Integer commentFrom, @Min(1) Integer commentSize) {

        Event oldEvent = findEventById(eventId);
        int maximumHoursDeviation = 1;
        Event event = eventValidator(dto, oldEvent, maximumHoursDeviation, true);
        eventRepository.save(event);

        return EventMapper.objectToFullDto(event, getComments(event, null, commentFrom, commentSize));
    }

    public void updateEventRequest(Event event) {

        eventRepository.save(event);
    }

    public Event findEventById(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(() -> new ObjectNotFoundException("There is no event with this id"));
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
            log.info("method eventDateControl - BadRequestException \"the date and time for which the event is scheduled " +
                    "cannot be earlier than two hours from the current moment\"");
            throw new BadRequestException("the date and time for which the event is scheduled cannot be earlier than " +
                    "two hours from the current moment");
        }
    }

    private PageRequest pageableCreator(Integer from, Integer size, String sort) {

        if (sort == null || sort.isEmpty()) {
            return PageRequest.of(from / size, size);
        }

        switch (sort) {
            case "EVENT_DATE":
                return PageRequest.of(from / size, size, Sort.by("eventDate"));
            case "VIEWS":
                return PageRequest.of(from / size, size, Sort.by("views").descending());
            case "COMMENT_DATE":
                return PageRequest.of(from / size, size, Sort.by("createdOn"));
            default:
                throw new BadRequestException("Unknown sort: " + sort);
        }
    }

    private Event eventValidator(EventUpdateDto dto, Event event, int hours, boolean checkAdmin) {

        if (dto.getEventDate() != null) {
            eventDateControl(dto.getEventDate(), hours);
            event.setEventDate(DateTimeAdapter.stringToDate(dto.getEventDate()));
        }
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            if (dto.getAnnotation().length() < 20 || dto.getAnnotation().length() > 2000) {
                log.info("method eventValidator - BadRequestException \"incorrect length of the annotation parameter\"");
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
                log.info("method eventValidator - BadRequestException \"incorrect length of the description parameter\"");
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
                log.info("method eventValidator - BadRequestException \"the participantLimit must be positive\"");
                throw new BadRequestException("the participantLimit must be positive");
            }
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            if (!checkAdmin) {
                if (event.getStatus().equals(EventState.CANCELED) && dto.getStateAction().equals(EventStateAction.SEND_TO_REVIEW)) {
                    event.setStatus(EventState.PENDING);
                } else if (!event.getStatus().equals(EventState.CANCELED) && dto.getStateAction().equals(EventStateAction.CANCEL_REVIEW)) {
                    event.setStatus(EventState.CANCELED);
                }
            } else {
                if (event.getStatus().equals(EventState.PENDING) && dto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                    event.setStatus(EventState.PUBLISHED);
                } else if (!event.getStatus().equals(EventState.PENDING) && dto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                    log.info("method eventValidator - ConflictException \"an event can be published only if it is in the waiting state for publication\"");
                    throw new ConflictException("an event can be published only if it is in the waiting state for publication");
                }
                if (!event.getStatus().equals(EventState.PUBLISHED) && dto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                    event.setStatus(EventState.CANCELED);
                    event.setPublishedOn(LocalDateTime.now());
                } else if (event.getStatus().equals(EventState.PUBLISHED) && dto.getStateAction().equals(EventStateAction.REJECT_EVENT)) {
                    log.info("method eventValidator - ConflictException \"an event can be rejected only if it has not been published yet\"");
                    throw new ConflictException("an event can be rejected only if it has not been published yet");
                }
            }
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            if (dto.getTitle().length() < 3 || dto.getTitle().length() > 120) {
                log.info("method eventValidator - BadRequestException \"incorrect length of the title parameter\"");
                throw new BadRequestException("incorrect length of the title parameter");
            } else {
                event.setTitle(dto.getTitle());
            }
        }

        return event;
    }

    private Page<Event> creatingRequestPublic(String text, List<Long> categories, Boolean paid, String rangeStart,
                                              String rangeEnd, Boolean onlyAvailable, PageRequest pageable) {

        return eventRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Predicate textOr1 = criteriaBuilder.like(criteriaBuilder.upper(root.get("description")), "%" + text.toUpperCase() + "%");
            Predicate textOr2 = criteriaBuilder.like(criteriaBuilder.upper(root.get("annotation")), "%" + text.toUpperCase() + "%");
            Predicate predicateForGrade = criteriaBuilder.or(textOr1, textOr2);
            predicates.add(predicateForGrade);

            if (categories != null && !categories.isEmpty()) {
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("category"));
                for (Long categoriesId : categories) {
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
                log.info("method creatingRequestPublic - BadRequestException \"the beginning of the range cannot start before the end of the range\"");
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

    private Page<Event> creatingRequestAdmin(List<Long> users, List<String> states, List<Long> categories,
                                             String rangeStart, String rangeEnd, PageRequest pageable) {

        return eventRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (users != null && !users.isEmpty() && !(users.size() == 1 && users.get(0) == 0)) {
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("initiator"));
                for (Long initiatorId : users) {
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
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("category"));
                for (Long categoriesId : categories) {
                    in.value(categoriesId);
                }
                predicates.add(in);
            }

            LocalDateTime start = DateTimeAdapter.stringToDate(rangeStart);
            LocalDateTime end = DateTimeAdapter.stringToDate(rangeEnd);
            if (start != null && end != null && start.isAfter(end)) {
                log.info("method creatingRequestAdmin - BadRequestException \"the beginning of the range cannot start before the end of the range\"");
                throw new BadRequestException("the beginning of the range cannot start before the end of the range");
            }
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), Objects.requireNonNullElseGet(start, LocalDateTime::now)));
            if (end != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    private Comment availabilityControl(Long commentId, Long userId, Long eventId) {

        Comment comment = findCommentById(commentId);

        userService.findUserById(userId);

        findEventById(eventId);
        if (!comment.getCommentator().getId().equals(userId)) {
            log.info("method updateComment - BadRequestException \"you can't edit someone else's comment\"");
            throw new BadRequestException("you can't edit someone else's comment");
        }
        if (comment.getEvent() != null && !comment.getEvent().getId().equals(eventId)) {
            log.info("method updateComment - BadRequestException \"the event id is specified incorrectly\"");
            throw new BadRequestException("the event id is specified incorrectly");
        }

        return comment;
    }

    private void banControl(Long userId) {

        Optional<Ban> optionalBan = banRepository.findByCommentatorId(userId);

        if (optionalBan.isPresent()) {
            Ban ban = optionalBan.get();

            if (ban.getEndOfBan().isAfter(LocalDateTime.now())) {
                throw new BadRequestException("You are not allowed to write comments before " + DateTimeAdapter.dateToString(ban.getEndOfBan()));
            } else {
                banRepository.delete(ban);
            }
        }
    }

    public Comment findCommentById(Long commentId) {

        return commentRepository.findById(commentId).orElseThrow(() -> new ObjectNotFoundException("There is no comment with this id"));
    }
}
