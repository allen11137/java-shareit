package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ErrorException extends RuntimeException {

    public ErrorException(String message) {
        super(message);
    }

    public ErrorException() {
    }
}
