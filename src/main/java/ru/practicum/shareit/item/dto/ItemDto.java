package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingIdDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Builder
@Data
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<ItemRequest> requests;
    private BookingIdDto nextBooking;
    private BookingIdDto lastBooking;
    private Long ownerId;
    private List<CommentDto> comments;
}
