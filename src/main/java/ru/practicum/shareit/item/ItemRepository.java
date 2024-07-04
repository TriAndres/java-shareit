package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(Item item);

    Item update(Long itemId, Item item);

    Item getItemOfId(Long itemId);

    List<Item> getItems(Long aLong);

    List<Item> getItemOfText(String text);
}
