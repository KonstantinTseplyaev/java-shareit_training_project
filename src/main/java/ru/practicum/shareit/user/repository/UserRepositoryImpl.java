package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> usersRepository = new HashMap<>();

    @Override
    public List<User> getAll() {
        return new ArrayList<>(usersRepository.values());
    }

    @Override
    public User getById(Long id) {
        checkUserId(id);
        return usersRepository.get(id);
    }

    @Override
    public User create(User user) {
        usersRepository.put(user.getId(), user);
        return usersRepository.get(user.getId());
    }

    @Override
    public User update(User user) {
        checkUserId(user.getId());
        usersRepository.put(user.getId(), user);
        return usersRepository.get(user.getId());
    }

    @Override
    public void deleteAll() {
        usersRepository.clear();
    }

    @Override
    public void deleteById(Long id) {
        usersRepository.remove(id);
    }

    public void checkUserId(Long id) {
        if (!usersRepository.containsKey(id))
            throw new UserNotFoundException("пользователя с id " + id + " не существует");
    }
}
