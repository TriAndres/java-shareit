package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.AddItemRequestRqDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addNewItemRequest(Long userId, AddItemRequestRqDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + userId));
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(user, itemRequestDto);
        return ItemRequestMapper.mapToItemRequestDto(requestRepository.save(itemRequest), new ArrayList<>());
    }

    @Override
    public List<ItemRequestDto> findAllUserItemRequests(Long userId) {
        List<ItemRequest> requests = requestRepository.findByRequesterId(userId, Sort.by(ItemRequest.Fields.created).descending());
        List<Item> items = itemRepository.findByRequestRequesterId(userId, Sort.by(Item.Fields.id));

        return ItemRequestMapper.mapToItemRequestDto(requests, items);
    }

    @Override
    public ItemRequestDto findItemRequestById(Long userId, Long id) {
        ItemRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден запрос с id = " + id));
        List<Item> items = itemRepository.findByRequestId(id, Sort.by(Item.Fields.id));

        return ItemRequestMapper.mapToItemRequestDto(request, items);
    }

    @Override
    public List<ItemRequestDto> findAllItemRequestsFromOtherUsers(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from, size, Sort.by(ItemRequest.Fields.created).descending());
        List<ItemRequest> requests = requestRepository.findByRequesterIdNot(userId, page);
        List<Item> items = itemRepository.findByRequestIn(requests, Sort.by(Item.Fields.id));

        return ItemRequestMapper.mapToItemRequestDto(requests, items);
    }
}
