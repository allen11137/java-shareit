package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ErrorException extends RuntimeException {
    @JsonProperty("Error message")
    private String errorMessage;

    public ErrorException(String message) {
        super(message);
    }

    public ErrorException(String message, String errorMessage) {
        super(message);
        this.errorMessage = errorMessage;
    }

    public ErrorException() {
    }
}
