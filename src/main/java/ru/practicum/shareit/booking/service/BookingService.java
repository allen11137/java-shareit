package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingRequest bookingDto, Long userId);

    BookingDto approveBooking(Long bookingId, Boolean approved, Long userId);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBookings(BookingState state, Long userId);

    List<BookingDto> getAllOwnerBooking(BookingState state, Long userId);

}
