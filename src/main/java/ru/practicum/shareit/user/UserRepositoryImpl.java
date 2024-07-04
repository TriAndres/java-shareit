package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exseption.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    private Long createId() {
        return ++id;
    }

    @Override
    public User create(User user) throws ConflictException {
        Collection<User> listUser = users.values();
        if (listUser.stream()
                .allMatch(s -> s.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("адрес почты занят");
        }
        user.setId(createId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, Long id) throws ConflictException {
        if (!users.containsKey(id)) {
            Collection<User> listUser = users.values();
            if (listUser.stream()
                    .allMatch(s -> s.getEmail().equals(user.getEmail()))) {
                throw new ConflictException("адрес почты занят");
            }
            User user1 = users.get(id);
            if (user.getName() != null && !Objects.equals(user1.getName(), "")) {
                user1.setName(user1.getName());
            }
            if (user.getEmail() != null && !Objects.equals(user1.getEmail(), "")) {
                user1.setEmail(user1.getEmail());
            }
            return user1;
        }
        throw  new ConflictException("пользователь с таким идентификатором не найден");
    }
    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }


    @Override
    public User getUser(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new ConflictException("пользователь с таким идентификатором не найден");
    }

    @Override
    public User delete(Long id) {
        if (users.containsKey(id)) {
            User user = users.get(id);
            users.remove(id);
            return user;
        }
        throw new ConflictException("пользователь с таким идентификатором не найден");
    }
}