package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    User create(User user);

    User update(User user, Long id);

    Collection<User> findAllUsers();

    User getUser(Long id);

    User delete(Long id);
}