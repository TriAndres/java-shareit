package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.AddUserRqDto;
import ru.practicum.shareit.user.dto.UpdateUserRqDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        return userClient.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        return userClient.deleteById(id);
    }

    @PostMapping
    public ResponseEntity<Object> addNewUser(@RequestBody @Valid AddUserRqDto addUserRequestDto) {
        return userClient.addNewUser(addUserRequestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUserById(@RequestBody @Valid UpdateUserRqDto userDto, @PathVariable Long id) {
        if (userDto.allFieldsAreEmpty()) {
            throw new IllegalArgumentException("Должно быть задано хотя бы одно изменяемое поле");
        }
        return userClient.updateUserById(id, userDto);
    }
}
