package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    List<Item> itemByText(Long userId, String text);

    Item getItemById(long itemId, long userId);

    CommentResponseDto addComment(CommentDto dto, Long itemId, Long userId);

    ItemWithBookingDto getItem(long itemId, long userId);

    List<ItemWithBookingDto> findItemByUserId(long userId, int from, int size);

}
