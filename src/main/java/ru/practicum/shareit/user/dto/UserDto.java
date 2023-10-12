package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Valid
public class UserDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    @Email
    private String email;
}
