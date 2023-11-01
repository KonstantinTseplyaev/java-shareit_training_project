package ru.practicum.shareit.exception;

public class UnknownBookingStateException extends RuntimeException {
    public UnknownBookingStateException(String message) {
        super(message);
    }
}
