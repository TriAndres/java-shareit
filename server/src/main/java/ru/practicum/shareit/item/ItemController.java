package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.AddCommentRqDto;
import ru.practicum.shareit.item.dto.AddItemRqDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRelatedDataDto;
import ru.practicum.shareit.item.dto.UpdateItemRqDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody AddItemRqDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemWithRelatedDataDto findById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        return itemService.findById(userId, id);
    }

    @GetMapping
    public List<ItemWithRelatedDataDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllWithRelatedDataByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam(defaultValue = "") String text) {
        return itemService.search(userId, text);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("id") Long itemId,
                                  @RequestBody UpdateItemRqDto itemDto) {
        return itemService.updateItemById(userId, itemId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable("id") Long itemId,
                                    @RequestBody AddCommentRqDto commentDto) {
        return itemService.addNewComment(userId, itemId, commentDto);
    }

}
