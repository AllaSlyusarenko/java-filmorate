package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage{


    @Override
    public List<User> findALl() {
        return null;
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User put(User user) {
        return null;
    }

    @Override
    public User delete(User user) {
        return null;
    }
}
