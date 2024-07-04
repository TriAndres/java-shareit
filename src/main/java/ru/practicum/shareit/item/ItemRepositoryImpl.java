package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item create(Item item) {
        item.setId(createId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long itemId, Item item1) {
        Item item = items.get(itemId);
        if (item1.getAvailable() != null ) {
            item.setAvailable(item1.getAvailable());
        }
        if (item1.getName() != null && Objects.equals(item1.getName(), "")) {
            item.setName(item1.getName());
        }
        if (item1.getDescription() != null && !Objects.equals(item1.getDescription(), "")) {
            item.setDescription(item1.getDescription());
        }
        return item;
    }

    @Override
    public Item getItemOfId(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItems(Long aLong) {
        List<Item> list = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getId().equals(aLong)) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public List<Item> getItemOfText(String text) {
        List<Item> list = new ArrayList<>();
        for (Item item : items.values()) {
            boolean a = item.getAvailable().equals(true) && item.getDescription().toLowerCase().contains(text.toLowerCase());
            boolean b = item.getAvailable().equals(true) && item.getName().toLowerCase().contains(text.toLowerCase());
            if (a || b) {
                list.add(item);
            }
        }
        return list;
    }

    protected Long createId() {
        return ++id;
    }
}
