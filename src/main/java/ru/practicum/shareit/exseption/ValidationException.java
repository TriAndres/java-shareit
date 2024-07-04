package ru.practicum.shareit.exseption;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
