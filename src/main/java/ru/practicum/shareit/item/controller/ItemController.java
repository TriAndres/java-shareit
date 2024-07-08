package ru.practicum.shareit.item.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> getItemsByUserId(HttpServletRequest request) {
        return itemService.getItemsByUserId(request.getIntHeader("X-Sharer-User-Id"));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemsById(@PathVariable long itemId, HttpServletRequest request) {
        return itemService.getItemsById(itemId, request.getIntHeader("X-Sharer-User-Id"));
    }

    @PatchMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, HttpServletRequest request) {
        return itemService.createItem(itemDto, request.getIntHeader("X-Sharer-User-Id"));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto, HttpServletRequest request) {
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, request.getIntHeader("X-Sharer-User-Id"));
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemBySearch(@RequestParam String text) {
        return itemService.getItemBySearch(text);
    }
}