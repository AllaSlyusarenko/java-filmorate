package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    //методы добавления, удаления и модификации объектов
    //CRUD

    public List<User> findALl();

    public User create(User user);

    public User put(User user);

    public User delete(User user);
}
