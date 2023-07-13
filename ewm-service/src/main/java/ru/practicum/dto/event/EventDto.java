package ru.practicum.dto.event;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.models.location.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Builder
public class EventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private final String annotation;
    @NotNull
    private final Long category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private final String description;
    @NotNull
    private String eventDate;
    @NotNull
    private final Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    @NotBlank
    @Size(min = 3, max = 120)
    private final String title;
}
