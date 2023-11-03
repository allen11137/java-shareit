package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;

import java.util.List;


public interface ItemRequestService {
    ItemRequestDtoResponse createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoResponse> findByUserId(Long userId);

    List<ItemRequestDtoResponse> findAll(Integer from, Integer size, Long userId);

    ItemRequestDtoResponse findRequest(Long requestId, Long userId);
}
