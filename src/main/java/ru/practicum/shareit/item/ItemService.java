package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Optional<Long> userId);

    ItemDto updateItem(Optional<Long> userId, Long itemId, ItemDto itemDto);

    ItemDto getItemOfId(Long userId, Long itemId);

    List<ItemDto> getItems(Optional<Long> userId);

    List<ItemDto> getItemOfText(Optional<Long> userId, String text);
}
