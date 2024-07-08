package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final UserRepository userRepository;
    private  final Map<Long, List<Item>> items = new HashMap<>();
    private long itemId = 0;

    @Override
    public Collection<Item> getItemsByUserId(long userId) {
        return items.get(userId);
    }

    @Override
    public Item getItemsById(long itemId) {
        log.debug("getItemsById: {} ", itemId);
        Item item = null;
        for (long userId : items.keySet()) {
            item = items.get(userId).stream().filter(x -> x.getId() == itemId).findFirst().orElse(null);
        }
        return item;
    }

    @Override
    public Item createItem(Item item, long userId) {
        item.setId(++itemId);
        item.setOwner((userRepository.getUserById(userId)));
        items.compute(userId, (ownerId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        log.debug("createItem {}", item);
        int index = findItemIndexInList(itemId, userId);
        return items.get(userId).get(index);
    }

    @Override
    public Item updateItem(Item item, long userId) {
        log.info("updateItem {}  ", item);
        if (userId != item.getOwner().getId()) {
            throw new RuntimeException("Owner id is incorrect!");
        }

        int index = findItemIndexInList(itemId, userId);
        items.get(userId).set(index, item);
        return items.get(userId).get(index);
    }

    @Override
    public Collection<Item> getItemBySearch(String text) {
        Collection<Item> availableItems = new ArrayList<>();
        for (long userId : items.keySet()) {
            availableItems.addAll(items.get(userId).stream()
                    .filter(x -> x.getAvailable().equals(true))
                    .filter(x -> x.getDescription().toLowerCase().contains(text))
                    .collect(Collectors.toSet()));    //collect(Collectors.toList())
        }
        return availableItems;
    }

    private int findItemIndexInList(long itemId, long userId) {
        return IntStream.range(0, items.get(userId).size())
                .filter(i -> items.get(userId).get(i).getId() == itemId)
                .findFirst()
                .orElse(-1);
    }
}