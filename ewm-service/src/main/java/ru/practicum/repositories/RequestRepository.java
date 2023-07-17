package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.models.Request;
import ru.practicum.models.RequestState;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findAllByEventIdAndRequesterId(Long event, Long userId);

    List<Request> findByRequesterId(Long userId);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestState requestState);

    List<Request> findByEventInitiatorIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByIdInAndEventInitiatorIdAndEventIdAndStatus(List<Long> ids, Long userId, Long eventId, RequestState confirmed);
}
