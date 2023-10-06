package ru.practicum.shareit.exceptions;

public class UnknownBookingStateException extends RuntimeException {
    public UnknownBookingStateException(String message) {
        super(message);
    }
}
