package ru.practicum.mappers;

import org.springframework.stereotype.Service;
import ru.practicum.models.user.User;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

@Service
public class UserMapper {

    public static UserDto objectToDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static UserShortDto objectToShortDto(User user) {

        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public User dtoToObject(UserDto dto) {

        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .name(dto.getName())
                .build();
    }
}
