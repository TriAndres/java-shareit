package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.ObjectUtils;

@Data
@AllArgsConstructor
public class UpdateUserRqDto {
    private String name;
    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Неправильный формат email")
    private String email;

    public boolean allFieldsAreEmpty() {
        return ObjectUtils.isEmpty(name) && ObjectUtils.isEmpty(email);
    }
}
