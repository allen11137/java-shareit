package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public static BookingDto mapBookingToBookingDto(Booking booking, User booker) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapItemToItemDto(booking.getItem()))
                .booker(UserMapper.mapUserToUserDto(booker))
                .available(booking.getAvailable())
                .status(booking.getStatus())
                .build();
    }

    public static BookingItemDtoResponse mapBookingToBookingItemDtoResponse(Booking booking, User booker) {
        return BookingItemDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.mapItemToItemDto(booking.getItem()))
                .bookerId(booker.getId())
                .status(booking.getStatus())
                .build();
    }


    public Booking toBooking(BookingRequest dto, Item item, Long userId) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBookerId(userId);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(dto.getStatus() != null ? dto.getStatus() : Status.WAITING);
        booking.setAvailable(true);
        return booking;
    }

}
