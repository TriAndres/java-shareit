package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UpdateUserRqDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addNewUser(UserDto userDto);

    UserDto updateUserById(Long userId, UpdateUserRqDto userDto);

    List<UserDto> findAll();

    UserDto findById(Long id);

    void deleteById(Long id);
}
