package ru.practicum.shareit.user;

import ru.practicum.shareit.user.UserDto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long userId);

    UserDto findById(Long userId);

    void delete(Long userId);
}