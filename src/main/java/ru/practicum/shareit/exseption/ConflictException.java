package ru.practicum.shareit.exseption;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}