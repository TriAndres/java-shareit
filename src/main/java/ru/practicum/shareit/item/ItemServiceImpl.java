package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exseption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Optional<Long> userId) throws ValidationException {
        if (userId.isPresent() && userId.get() > 0) {
            if (userRepository.getUser(userId.get()) == null) {
                throw new NoSuchElementException("пользователя не существует");
            }
            Item item = ItemMapper.toItem(itemDto);
            item.setUserId(userId.get());
            return ItemMapper.toItemDto(itemRepository.create(item));
        }
        throw new ValidationException("идификатор пользователя отсутствует");
    }

    @Override
    public ItemDto update(Optional<Long> userId, Long itemId, ItemDto itemDto) throws ValidationException {
        if (userId.isPresent() && userId.get() > 0) {
            Item item = ItemMapper.toItem(itemDto);
            if (itemRepository.getItemOfId(itemId).getUserId().equals(userId.get())) {
                return ItemMapper.toItemDto(itemRepository.update(itemId, item));
            }
            throw new NoSuchElementException("пользователя не существует");
        }
        throw new ValidationException("идификатор пользователя отсутствует");
    }

    @Override
    public ItemDto getItemOfId(Long userId, Long itemId) throws ValidationException {
       if (userId > 0 && itemId > 0) {
           return ItemMapper.toItemDto(itemRepository.getItemOfId(itemId));
       }
        throw new ValidationException("идификатор пользователя отсутствует");
    }

    @Override
    public List<ItemDto> getItems(Optional<Long> userId) throws ValidationException {
        if (userId.isPresent() && userId.get() > 0) {
            List<Item> its = itemRepository.getItems(userId.get());
            List<ItemDto> list = new ArrayList<>();
            for (Item item : its) {
                list.add(ItemMapper.toItemDto(item));
            }
            return list;
        }
        throw new ValidationException("идификатор пользователя отсутствует");
    }

    @Override
    public List<ItemDto> getItemOfText(Optional<Long> userId, String text) throws ValidationException {
        if (userId.isPresent() && userId.get() > 0) {
            if (text != null && !text.isEmpty()) {
                return new ArrayList<>();
            }
            List<Item> its = itemRepository.getItemOfText(text);
            List<ItemDto> list = new ArrayList<>();
            for (Item item : its) {
                list.add(ItemMapper.toItemDto(item));
            }
            return list;
        }
        throw new ValidationException("идификатор пользователя отсутствует");
    }
}
