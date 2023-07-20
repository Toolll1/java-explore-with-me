package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BanDto {

    private final Long id;
    private final String createdOn;
    private final String endOfBan;
    private final UserShortDto commentator;
}
