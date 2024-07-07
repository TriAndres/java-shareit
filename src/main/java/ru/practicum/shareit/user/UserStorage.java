package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user, Long id);

    Collection<User> getAllUsers();

    User getUser(Long id);

    User deleteUser(Long id);
}
