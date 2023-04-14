package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    public List<User> findAll();

    public User findUserById(int id);

    public User create(User user);

    public User put(User user);

    public boolean addToFriends(int id, int friendId);

    public boolean deleteFromFriends(int id, int friendId);

    public List<User> getFriends(int id);

    public List<User> getCommonFriends(int id, int otherId);
}
