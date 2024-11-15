package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.AddCommentRqDto;
import ru.practicum.shareit.item.dto.AddItemRqDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRelatedDataDto;
import ru.practicum.shareit.item.dto.UpdateItemRqDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(Long ownerId, AddItemRqDto item);

    ItemDto updateItemById(Long ownerId, Long itemId, UpdateItemRqDto updateItemDto);

    ItemWithRelatedDataDto findById(Long userId, Long id);

    List<ItemWithRelatedDataDto> findAllWithRelatedDataByOwner(Long ownerId);

    List<ItemDto> search(Long ownerId, String text);

    CommentDto addNewComment(Long userId, Long itemId, AddCommentRqDto commentDto);
}
