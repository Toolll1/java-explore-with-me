package ru.practicum.mappers;

import org.springframework.stereotype.Service;
import ru.practicum.adapters.DateTimeAdapter;
import ru.practicum.dto.BanDto;
import ru.practicum.models.Ban;

@Service
public class BanMapper {

    public static BanDto objectToDto(Ban ban) {

        return BanDto.builder()
                .id(ban.getId())
                .createdOn(DateTimeAdapter.dateToString(ban.getCreatedOn()))
                .endOfBan(DateTimeAdapter.dateToString(ban.getEndOfBan()))
                .commentator(UserMapper.objectToShortDto(ban.getCommentator()))
                .build();
    }
}
