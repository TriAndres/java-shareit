package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.mapper.ItemMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public Collection<ItemDto> getItemsByUserId(long userId) {
        checkingUserId(userId);
        log.info("Getting items by userId: {}", userId);
        return itemRepository.getItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemsById(long itemId, long userId) {
        checkingItemId(itemId);
        checkingUserId(userId);
        log.info("Getting item by id: {} for user {}", itemId, userId);
        return toItemDto(itemRepository.getItemsById(itemId));
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        checkingUserId(userId);
        if (itemDto.getAvailable() == null || itemDto.getDescription() == null || itemDto.getName() == null) {
            throw new RuntimeException("Null or invalid item");
        }
        if (itemDto.getName().isEmpty() || itemDto.getDescription().isEmpty()) {
            throw new RuntimeException("Empty or invalid item");
        }
        log.info("createItem: {} for user {}", itemDto, userId);
        return toItemDto(itemRepository.createItem(toItem(itemDto), userId));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId) {
        checkingItemId(itemDto.getId());
        checkingUserId(userId);
        log.info("updateItem: {} for user {}", itemDto, userId);
        return toItemDto(itemRepository.updateItem(toItemUpdate(itemDto, itemRepository
                .getItemsById(itemDto.getId())), userId));
    }

    @Override
    public Collection<ItemDto> getItemBySearch(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        log.info("getItemBySearch {}", text);
        return itemRepository.getItemBySearch(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkingUserId(long userId) {
        if (userId == -1) {
            throw new RuntimeException("checkingUserId " + userId);
        }
        if (userService.getAllUsers().stream().map(UserDto::getId).noneMatch(x -> x.equals(userId))) {
            throw new RuntimeException("checkingUserId " + userId);
        }
    }

    private void checkingItemId(long itemId) {
        if (itemRepository.getItemsById(itemId) == null) {
            throw new RuntimeException("checkingItemId " + itemId);
        }
    }
}
