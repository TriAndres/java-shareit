package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserRepository {

    Collection<User> findAll();

    User save(User user);

    User findById(Long userId);

    void deleteById(Long userId);
}