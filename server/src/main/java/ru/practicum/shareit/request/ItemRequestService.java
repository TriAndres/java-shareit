package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.AddItemRequestRqDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNewItemRequest(Long userId, AddItemRequestRqDto itemRequestDto);

    List<ItemRequestDto> findAllUserItemRequests(Long userId);

    ItemRequestDto findItemRequestById(Long userId, Long id);

    List<ItemRequestDto> findAllItemRequestsFromOtherUsers(Long userId, Integer from, Integer size);
}
