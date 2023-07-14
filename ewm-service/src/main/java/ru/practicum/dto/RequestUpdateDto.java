package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.models.RequestState;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RequestUpdateDto {

    @NotEmpty
    private final List<Long> requestIds;
    @NotNull
    private final RequestState status;
}
