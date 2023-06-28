package ru.practicum.model;

import lombok.*;

import javax.validation.constraints.NotEmpty;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class HitDto {

    @NotEmpty
    String app;
    @NotEmpty
    String uri;
    @NotEmpty
    String ip;
    @NotEmpty
    String timestamp;
}
