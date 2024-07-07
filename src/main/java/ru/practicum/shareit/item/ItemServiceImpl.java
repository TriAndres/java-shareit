package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper mapper;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(ItemDto itemDto, Optional<Long> userId) {
        if (userId.isPresent() && userId.get() > 0) {
            if (userStorage.getUser(userId.get()) == null) {
                throw new NoSuchElementException("пользователь не существует");
            }
            if (checkName(itemDto.getName()) && checkDescription(itemDto.getDescription())
                    && itemDto.getAvailable() != null) {
                Item item = mapper.toItem(itemDto);
                item.setUserId(userId.get());
                return mapper.toItemDto(itemStorage.createItem(item));
            }
            throw new RuntimeException("У вещи неправильно заданы параметры:" + itemDto);
        }
        throw new RuntimeException("идентификатор пользователя отрицательный или отсутствует");
    }

    public Boolean checkName(String name) {
        if (name != null && !name.isEmpty()) {
            return true;
        }
        throw new RuntimeException("имя не должно быть пустым");
    }

    public Boolean checkDescription(String desc) {
        if (desc != null) {
            return true;
        }
        throw new RuntimeException("описание не должно быть пустым");
    }

    @Override
    public ItemDto updateItem(Optional<Long> userId, Long itemId, ItemDto itemDto) {
        if (userId.isPresent() && userId.get() > 0) {
            Item item = mapper.toItem(itemDto);
            log.info("вещь для редактирования: {}", item);
            if (itemStorage.getItemOfId(itemId).getUserId().equals(userId.get())) {
                return mapper.toItemDto(itemStorage.updateItem(itemId, item));
            }
            throw new NoSuchElementException("нельзя редактировать чужие вещи!");
        }
        throw new RuntimeException("идентификатор пользователя отрицательный или отсутствует");
    }

    @Override
    public List<ItemDto> getItems(Optional<Long> userId) {
        if (userId.isPresent() && userId.get() > 0) {
            List<Item> its = itemStorage.getItems(userId.get());
            log.info("вещи пользователя: {} {}", userId, its);
            List<ItemDto> list = new ArrayList<>();
            for (Item item : its) {
                list.add(mapper.toItemDto(item));
            }
            return list;
        }
        throw new RuntimeException("идентификатор пользователя отрицательный или отсутствует");
    }

    @Override
    public ItemDto getItemOfId(Long userId, Long itemId) {
        if (userId > 0 && itemId > 0) {
            return mapper.toItemDto(itemStorage.getItemOfId(itemId));
        }
        throw new RuntimeException("идентификатор пользователя отрицательный или отсутствует");
    }

    @Override
    public List<ItemDto> getItemOfText(Optional<Long> userId, String text) {
        if (userId.isPresent() && userId.get() > 0) {
            if (text == null || text.isEmpty()) return new ArrayList<>();
            List<Item> its = itemStorage.getItemOfText(text);
            List<ItemDto> list = new ArrayList<>();
            for (Item item : its) {
                list.add(mapper.toItemDto(item));
            }
            return list;
        }
        throw new RuntimeException("идентификатор пользователя отрицательный или отсутствует");
    }
}
