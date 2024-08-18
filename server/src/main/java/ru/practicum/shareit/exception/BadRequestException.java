package ru.practicum.shareit.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String m) {
        super(m);
    }
}
