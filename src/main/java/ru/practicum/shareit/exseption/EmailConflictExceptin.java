package ru.practicum.shareit.exseption;

public class EmailConflictExceptin extends RuntimeException {
    public EmailConflictExceptin(String message) {
        super(message);
    }
}