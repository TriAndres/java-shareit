package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emailsSet = new HashSet<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        log.info("getUserById");
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        Collection<User> listUser = users.values();
        if (listUser.stream()
                .anyMatch(s -> s.getName().equals(user.getName()))) {
            throw new IllegalArgumentException("адрес почты уже занят");
        }
        log.info("createUser: {}", user);
        user.setId(getId());
        users.put(user.getId(), user);
        return null;
    }

    @Override
    public User updateUser(User user) {
        Collection<User> listUser = users.values();
        if (listUser.stream()
                .anyMatch(s -> s.getName().equals(user.getName()))) {
            throw new IllegalArgumentException("адрес почты уже занят");
        }
        log.debug("updateUser id: {}, user: {}", user.getId(), user);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        log.info("deleteUser: {}", id);
        users.remove(id);
    }

    private long getId() {
        long lastId = users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}