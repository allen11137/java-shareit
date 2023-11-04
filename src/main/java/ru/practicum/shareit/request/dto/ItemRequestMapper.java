package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequestDtoResponse mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDtoResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() != null ? itemRequest.getItems().stream().map(ItemMapper::mapItemToItemDto).collect(Collectors.toList()) : null)
                .build();
    }

    public static List<ItemRequestDtoResponse> mapToItemRequestsDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::mapToItemRequestDto).collect(Collectors.toList());
    }

    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requesterId(user.getId())
                .build();
    }
}