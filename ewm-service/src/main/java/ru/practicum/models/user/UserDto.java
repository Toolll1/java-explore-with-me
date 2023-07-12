package ru.practicum.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Builder
public class UserDto {

    private final Integer id;
    @NotBlank
    @Size(min = 2, max = 250)
    private final String name;
    @Email
    @NotBlank
    @Size(min = 6, max = 254)
    private final String email;
}
