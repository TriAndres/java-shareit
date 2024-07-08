package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {
    Collection<Item> getItemsByUserId(long userId);

    Item getItemsById(long itemId);

    Item createItem(Item item, long userId);

    Item updateItem(Item item, long userId);

    Collection<Item> getItemBySearch(String text);
}