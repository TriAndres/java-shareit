package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import ru.practicum.shareit.user.model.User;

@AllArgsConstructor
@Builder
public class ItemRequestDto {

    private String description;
    private User requestor;
}
