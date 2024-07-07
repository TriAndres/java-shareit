package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item getItemOfId(Long itemId);

    Item updateItem(Long itemId, Item item);

    List<Item> getItems(Long userId);

    List<Item> getItemOfText(String text);
}
