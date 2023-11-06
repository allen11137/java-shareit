package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDtoResponse;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Builder
@Data
public class ItemWithBookingDto {
    private long id;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    private Boolean available;
    @NotNull
    @NotEmpty
    private String description;
    private BookingItemDtoResponse lastBooking;
    private BookingItemDtoResponse nextBooking;
    private Collection<CommentResponseDto> comments;
    private Long requestId;
}
