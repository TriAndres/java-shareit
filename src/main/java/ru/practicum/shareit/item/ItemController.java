package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ResponseEntity<?> createItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                        @RequestBody ItemDto itemDto) {
        log.info("поступил запрос на добавление вещи: {} пользователем: {}", itemDto, userId);
        return new ResponseEntity<>(itemService.createItem(itemDto, userId), HttpStatus.CREATED);
    }

    // только для владельца
    @PatchMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId, @PathVariable Long itemId,
                                        @RequestBody ItemDto itemDto) {
        log.info("поступил запрос на редактирование вещи: {} владельцем: {}", itemDto, userId);
        return new ResponseEntity<>(itemService.updateItem(userId, itemId, itemDto), HttpStatus.OK);
    }

    // для любого пользователя
    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItemOfId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("поступил запрос на просмотр вещи по идентификатору: {}", itemId);
        return new ResponseEntity<>(itemService.getItemOfId(userId, itemId), HttpStatus.OK);
    }

    // только для владельца
    @GetMapping()
    public ResponseEntity<?> getItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        log.info("поступил запрос на просмотр владельцем всех своих вещей,idUser: {}", userId);
        return new ResponseEntity<>(itemService.getItems(userId), HttpStatus.OK);
    }

    // только доступные для аренды вещи
    @GetMapping("/search")
    public ResponseEntity<?> getItemOfText(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                           @RequestParam("text") String text) {
        log.info("поступил запрос на просмотр доступной для аренды вещи: {}", text);
        return new ResponseEntity<>(itemService.getItemOfText(userId, text), HttpStatus.OK);
    }
}