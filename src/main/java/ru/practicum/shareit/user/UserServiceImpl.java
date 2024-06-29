package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exseption.EmailConflictExceptin;
import ru.practicum.shareit.exseption.UserValidationException;
import ru.practicum.shareit.user.UserDto.UserDto;
import ru.practicum.shareit.user.UserDto.UserMapper;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.mapToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null) {
            log.warn("Email must not be null");
            throw new UserValidationException("Email must not be null");
        }
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("Created user: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        User oldUser = UserMapper.toUser(findById(userId));
        boolean updated = false;
        if (userDto.getEmail() != null || !Objects.equals(null, oldUser.getEmail())) {
            oldUser.setEmail(userDto.getEmail());
            updated = true;
        }
        if(userDto.getName() != null) {
            oldUser.setName(userDto.getName());
            updated = true;
        }
        if (updated) {
            return UserMapper.toUserDto(userRepository.save(oldUser));
        }
        log.warn("update of user with id {} failed", userId);
        throw new EmailConflictExceptin("Unable to update user with given email");
    }

    @Override
    public UserDto findById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId));
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}