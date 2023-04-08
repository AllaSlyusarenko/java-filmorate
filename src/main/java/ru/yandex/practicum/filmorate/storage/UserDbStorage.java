package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public class UserDbStorage implements UserStorage{
    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User findUserById(int id) {
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
    public User addToFriends(int id, int friendId) {
        return null;
    }

    @Override
    public User deleteFromFriends(int id, int friendId) {
        return null;
    }

    @Override
    public List<User> getFriends(int id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        return null;
    }
}
