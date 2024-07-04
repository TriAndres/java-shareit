package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) throws ConfigDataException {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toDto(userRepository.create(user));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) throws ConfigDataException {
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        return UserMapper.toDto(userRepository.update(user, id));
    }

    @Override
    public List<UserDto> findAllUsers() {
        Collection<User> list = userRepository.findAllUsers();
        List<UserDto> listDto = new ArrayList<>();
        for (User user : list) {
            listDto.add(UserMapper.toDto(user));
        }
        return listDto;
    }


    @Override
    public UserDto findUserById(Long id) {
        return UserMapper.toDto(userRepository.getUser(id));
    }

    @Override
    public UserDto delete(Long id) {
        return UserMapper.toDto(userRepository.getUser(id));
    }
}