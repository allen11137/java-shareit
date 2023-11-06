package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDtoResponse;

import java.util.Collection;

@Builder
@Data
public class ItemWithBookingDto {
    private long id;
    private String name;
    private Boolean available;
    private String description;
    private BookingItemDtoResponse lastBooking;
    private BookingItemDtoResponse nextBooking;
    private Collection<CommentResponseDto> comments;
    private Long requestId;
}
