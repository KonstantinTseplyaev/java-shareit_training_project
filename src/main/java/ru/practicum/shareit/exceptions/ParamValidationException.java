package ru.practicum.shareit.exceptions;

public class ParamValidationException extends RuntimeException {

    public ParamValidationException(String message) {
        super(message);
    }
}
