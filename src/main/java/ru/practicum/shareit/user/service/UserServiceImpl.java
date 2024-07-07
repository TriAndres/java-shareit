package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.mapper.UserMapper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("getAllUsers");
        return userRepository.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        checkingId(id);
        log.info("getUserById: {}",id);
        return toUserDto(userRepository.getUserById(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new RuntimeException("Email id empty");
        }
        log.info("createUser: {}",userDto);
        return toUserDto(userRepository.createUser(toUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        checkingId(userDto.getId());
        log.info("updateUser: {}",userDto);
        return toUserDto(userRepository
                .updateUser(toUserUpdate(userDto, userRepository.getUserById(userDto.getId()))));
    }

    @Override
    public void deleteUser(Long id) {
        checkingId(id);
        log.info("deleteUser: {}",id);
        userRepository.deleteUser(id);
    }

    private void checkingId(Long id) {
        if (userRepository.getUserById(id) == null) {
            throw new RuntimeException("Нет пользователя с идентификатором: " + id);
        }
    }
}