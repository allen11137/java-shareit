package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static ItemDto mapItemToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .build();
    }

    public static Item mapItemDtoToItem(ItemDto dto, ItemRequest itemRequest) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .itemRequest(itemRequest)
                .ownerId(dto.getOwner())
                .build();

    }

    public ItemWithBookingDto itemWithBookingDto(Item item, BookingItemDtoResponse lastBooking,
                                                 BookingItemDtoResponse nextBooking,
                                                 List<CommentResponseDto> comments) {
        ItemWithBookingDto build = ItemWithBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();

        if (item.getItemRequest() != null) {
            build.setRequestId(item.getItemRequest().getId());
        }
        return build;
    }
}
