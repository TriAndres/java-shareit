package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AddBookingRqDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
