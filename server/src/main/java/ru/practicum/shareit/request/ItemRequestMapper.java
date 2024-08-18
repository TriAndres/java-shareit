package ru.practicum.shareit.request;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddItemRequestRqDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemRequestMapper {
    public static ItemRequest mapToItemRequest(User user, AddItemRequestRqDto itemRequestDto) {
        return new ItemRequest(null, itemRequestDto.getDescription(), user, LocalDateTime.now());
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestDto(itemRequest.getId(),
                                  itemRequest.getDescription(),
                                  itemRequest.getRequester().getName(),
                                  itemRequest.getCreated(),
                                  ItemMapper.mapToItemBasicInfoDto(items));
    }

    public static List<ItemRequestDto> mapToItemRequestDto(List<ItemRequest> requests, List<Item> items) {
        Map<ItemRequest, List<Item>> itemsByRequestMap = new HashMap<>();
        items.forEach(item -> itemsByRequestMap.getOrDefault(item.getRequest(), new ArrayList<>()).add(item));

        List<ItemRequestDto> requestDtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            requestDtos.add(mapToItemRequestDto(request,
                                                Optional.ofNullable(itemsByRequestMap.get(request))
                                                        .orElse(new ArrayList<>())));
        }

        return requestDtos;
    }
}
