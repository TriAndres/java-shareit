package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private Long id = 0L;
    private final Map<Long, User> users = new HashMap<>();

    private Long createId() {
        return ++id;
    }

    @Override
    public User createUser(User user) {
        Collection<User> listUser = users.values();
        if (listUser.stream()
                .anyMatch(s -> s.getEmail().equals(user.getEmail()))) {
            throw new RuntimeException("адрес почты уже занят");
        }
        Long id = createId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user, Long id) {
        if (users.containsKey(id)) {
            Collection<User> listUser = users.values();
            if (listUser.stream()
                    .anyMatch(s -> s.getEmail().equals(user.getEmail()))) {
                throw new RuntimeException("адрес почты занят");
            }
            User user1 = users.get(id);
            if (user.getName() != null && !Objects.equals(user.getName(), "")) user1.setName(user.getName());

            if (user.getEmail() != null && !Objects.equals(user.getEmail(), "") && user.getEmail().contains("@")) {
                user1.setEmail(user.getEmail());
            }
            return user1;
        }

        throw new NoSuchElementException("пользователь с таким идентификатором не найден");
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUser(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new NoSuchElementException("пользователь с таким идентификатором не найден");

    }

    @Override
    public User deleteUser(Long id) {
        if (users.containsKey(id)) {
            User user = users.get(id);
            users.remove(id);
            return user;
        }
        throw new NoSuchElementException("пользователь с таким идентификатором не найден");
    }
}
