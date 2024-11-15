package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
    ALL,// Все
    CURRENT,// Текущие
    FUTURE,// Будущие
    PAST,// Завершенные
    REJECTED,// Отклоненные
    WAITING;// Ожидающие подтверждения

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
