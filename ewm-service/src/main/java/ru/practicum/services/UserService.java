package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.models.user.User;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mappers.UserMapper;
import ru.practicum.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserDto createUser(UserDto dto) {

        User user = repository.save(mapper.dtoToObject(dto));

        return UserMapper.objectToDto(user);
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {

        if (from < 0 || size <= 0) {
            log.info("method getUsers - " +
                    "BadRequestException \"the from parameter must be greater than or equal to 0; size is greater than 0\"");
            throw new BadRequestException("the from parameter must be greater than or equal to 0; size is greater than 0");
        }

        if (ids == null || ids.isEmpty()) {
            return repository.findAll(PageRequest.of(from / size, size)).stream().map(UserMapper::objectToDto).collect(Collectors.toList());
        } else {
            return repository.findAllByIdIn(ids, PageRequest.of(from / size, size)).stream().map(UserMapper::objectToDto).collect(Collectors.toList());
        }
    }

    public void deleteUser(Long userId) {

        findUserById(userId);

        repository.deleteById(userId);
    }

    public User findUserById(Long userId) {

        return repository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("There is no user with this id"));
    }
}
