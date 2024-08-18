package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.ObjectUtils;

@Data
@AllArgsConstructor
public class UpdateItemRqDto {
    private String name;
    private String description;
    private Boolean available;

    public boolean allFieldsAreEmpty() {
        return ObjectUtils.isEmpty(name) && ObjectUtils.isEmpty(description) && available == null;
    }
}
