package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {


    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}