package ru.practicum.shareit.exceptions;

public class ItemAvailableException extends RuntimeException {
    public ItemAvailableException(String message) {
        super(message);
    }
}
