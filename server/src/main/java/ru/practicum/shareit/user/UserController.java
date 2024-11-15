package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserRqDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @PostMapping
    public UserDto addNewUser(@RequestBody UserDto userDto) {
        return userService.addNewUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUserById(@RequestBody UpdateUserRqDto userDto, @PathVariable("id") Long userId) {
        return userService.updateUserById(userId, userDto);
    }
}
