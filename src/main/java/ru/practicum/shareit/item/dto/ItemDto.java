package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@Valid
public class ItemDto {
    @NotNull
    private Long id;
    @NotBlank
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    @NotEmpty
    private Boolean available;
    private ItemRequest request;
    @NotBlank
    private Long owner;
}
