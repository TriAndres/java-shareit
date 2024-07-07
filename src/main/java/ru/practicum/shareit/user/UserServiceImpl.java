package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
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

    private final UserStorage userStorage;
    private final UserMapper mapper;

    @Override
    public UserDto createUser(UserDto userDto)  {
        User user = mapper.toUser(userDto);
        return mapper.toDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id)  {
        User user = mapper.toUser(userDto);
        user.setId(id);
        return mapper.toDto(userStorage.updateUser(user, id));
    }

    @Override
    public List<UserDto> findAllUsers() {
        Collection<User> list = userStorage.getAllUsers();
        List<UserDto> listDto = new ArrayList<>();
        for (User user : list) {
            listDto.add(mapper.toDto(user));
        }
        return listDto;
    }

    @Override
    public UserDto findUserById(Long id) {
        return mapper.toDto(userStorage.getUser(id));
    }

    @Override
    public UserDto deleteUser(Long id) {
        return mapper.toDto(userStorage.deleteUser(id));
    }
}
