package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingBasicInfoDto {
    private Long id;
    private Long bookerId;
}
