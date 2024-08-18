package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.AddItemRequestRqDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto addNewItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody AddItemRequestRqDto itemRequestDto) {
        return requestService.addNewItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.findAllUserItemRequests(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto findItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long id) {
        return requestService.findItemRequestById(userId, id);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllItemRequestsFromOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @RequestParam(name = "from") Integer from,
                                                                  @RequestParam(name = "size") Integer size) {
        return requestService.findAllItemRequestsFromOtherUsers(userId, from, size);
    }
}
