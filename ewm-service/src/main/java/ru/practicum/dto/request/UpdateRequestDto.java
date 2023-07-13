package ru.practicum.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.models.request.RequestState;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UpdateRequestDto {

    @NotEmpty
    private final List<Long> requestIds;
    @NotNull
    private final RequestState status;
}
