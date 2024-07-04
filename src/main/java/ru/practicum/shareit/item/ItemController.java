package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exseption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                    @RequestBody ItemDto itemDto) throws ValidationException {
        return new ResponseEntity<>(itemService.create(itemDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<?> update(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                    @PathVariable Long itemId,
                                    @RequestBody ItemDto itemDto) throws ValidationException {
        return new ResponseEntity<>(itemService.update(userId, itemId, itemDto), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItemOfId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) throws ValidationException {
        return new ResponseEntity<>(itemService.getItemOfId(userId, itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) throws ValidationException {
        return new ResponseEntity<>(itemService.getItems(userId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getItemOfText(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                           @RequestParam("text") String text) throws ValidationException {
        return new ResponseEntity<>(itemService.getItemOfText(userId, text), HttpStatus.OK);
    }
}