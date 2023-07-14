package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CompilationNewDto {

    private List<Long> events;
    private Boolean pinned;
    @NotBlank
    @Size(min = 1, max = 50)
    private final String title;
}
