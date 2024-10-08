package ru.practicum.shareit.request.dto.mapper;


import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapper {

    public static ItemRequestDto itemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .requestor(itemRequest.getRequestor())
                .description(itemRequest.getDescription())
                .build();
    }
}
