package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);
    UserDto update(UserDto userDto, Long id);
    List<UserDto> findAllUsers();
    UserDto findUserById(Long id);
    UserDto delete(Long userId);
}