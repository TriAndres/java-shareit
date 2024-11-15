package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingBasicInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemWithRelatedDataDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private BookingBasicInfoDto lastBooking;
    private BookingBasicInfoDto nextBooking;
    private List<CommentDto> comments;
}
