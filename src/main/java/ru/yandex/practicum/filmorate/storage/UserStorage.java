package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    public Map<Integer, User> getUsers();

    public List<User> findAll();

    public User findUserById(int id);

    public User create(User user);

    public User put(User user);

    public User addToFriends(int id, int friendId);

    public User deleteFromFriends(int id, int friendId);

    public List<User> getFriends(int id);

    public List<User> getCommonFriends(int id, int otherId);
}
