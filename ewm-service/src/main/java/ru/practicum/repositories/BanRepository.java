package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.models.Ban;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BanRepository extends JpaRepository<Ban, Long> {
    List<Ban> findAllByEndOfBanBefore(LocalDateTime now);

    List<Ban> findAllByEndOfBanBefore(LocalDateTime now, PageRequest pageable);

    List<Ban> findAllByEndOfBanAfter(LocalDateTime now, PageRequest pageable);

    void deleteByCommentatorId(Long userId);

    Optional<Ban> findByCommentatorId(Long userId);
}
