package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UnsupportedBookingStatusFilterException extends RuntimeException {
    public UnsupportedBookingStatusFilterException(String message) {
        super(message);
    }
}
