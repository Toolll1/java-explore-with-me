package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ObjectNotFoundException;
import ru.practicum.models.user.User;
import ru.practicum.models.user.UserDto;
import ru.practicum.models.user.UserMapper;
import ru.practicum.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserDto createUser(UserDto dto) {

        log.info("Received a request to create a user " + dto);

        User user = repository.save(mapper.dtoToObject(dto));

        return UserMapper.objectToDto(user);
    }

    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {

        log.info("Received a request to search for all users for params: ids {}, from {}, size {}", ids, from, size);

        if (from < 0 || size <= 0) {
            throw new BadRequestException("the from parameter must be greater than or equal to 0; size is greater than 0");
        }

        if (ids == null || ids.isEmpty()) {
            return repository.findAll(PageRequest.of(from / size, size)).stream().map(UserMapper::objectToDto).collect(Collectors.toList());
        } else {
            return repository.findAllByIdIn(ids, PageRequest.of(from / size, size)).stream().map(UserMapper::objectToDto).collect(Collectors.toList());
        }
    }

    public void deleteUser(int userId) {

        log.info("Received a request to delete a user with an id " + userId);

        findUserById(userId);

        repository.deleteById(userId);
    }

    public User findUserById(int userId) {

        Optional<User> user = repository.findById(userId);

        if (user.isEmpty()) {
            throw new ObjectNotFoundException("There is no user with this id");
        } else {
            return user.get();
        }
    }
}
