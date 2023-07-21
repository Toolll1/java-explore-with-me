package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.BanDto;
import ru.practicum.mappers.BanMapper;
import ru.practicum.models.Ban;
import ru.practicum.repositories.BanRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BanService {

    private final BanRepository banRepository;
    private final UserService userService;

    public BanDto addToBan(Long userId, Long termInDays) {

        Optional<Ban> optionalBan = banRepository.findByCommentatorId(userId);
        LocalDateTime now = LocalDateTime.now();

        if (optionalBan.isPresent()) {

            Ban ban = optionalBan.get();
            ban.setEndOfBan(now.plusDays(termInDays));

            return BanMapper.objectToDto(banRepository.save(ban));
        }

        Ban newBan = Ban.builder().commentator(userService.findUserById(userId)).build();

        newBan.setCreatedOn(now);
        newBan.setEndOfBan(now.plusDays(termInDays));

        return BanMapper.objectToDto(banRepository.save(newBan));
    }

    public void removeFromBanAdmin(Long userId) {

        banRepository.deleteByCommentatorId(userId);
    }

    public List<BanDto> getBanList(Integer from, Integer size) {

        PageRequest pageable = pageableCreator(from, size);

        return banRepository.findAll(pageable)
                .stream()
                .map(BanMapper::objectToDto)
                .collect(Collectors.toList());
    }

    public List<BanDto> getActiveBanList(Integer from, Integer size) {

        PageRequest pageable = pageableCreator(from, size);

        return banRepository.findAllByEndOfBanAfter(LocalDateTime.now(), pageable)
                .stream()
                .map(BanMapper::objectToDto)
                .collect(Collectors.toList());
    }

    public List<BanDto> getOverdueBanList(Integer from, Integer size) {

        PageRequest pageable = pageableCreator(from, size);

        return banRepository.findAllByEndOfBanBefore(LocalDateTime.now(), pageable)
                .stream()
                .map(BanMapper::objectToDto)
                .collect(Collectors.toList());
    }

    public void removeOverdueFromBan() {

        List<Ban> banList = banRepository.findAllByEndOfBanBefore(LocalDateTime.now());

        banRepository.deleteAll(banList);
    }

    private PageRequest pageableCreator(Integer from, Integer size) {

        return PageRequest.of(from / size, size);
    }
}
