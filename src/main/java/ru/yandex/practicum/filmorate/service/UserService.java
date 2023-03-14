package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public User addToFriends(int id, int friendId) {
        return userStorage.addToFriends(id, friendId);
    }

    public User deleteFromFriends(int id, int friendId) {
        return userStorage.deleteFromFriends(id, friendId);
    }

    public List<User> getFriends(int id) {
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }
}
