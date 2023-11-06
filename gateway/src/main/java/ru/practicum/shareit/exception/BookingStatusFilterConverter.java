package ru.practicum.shareit.exception;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingState;

@Component
public class BookingStatusFilterConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        try {
            return BookingState.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedBookingStatusFilterException("Unknown state: " + source);
        }
    }
}
