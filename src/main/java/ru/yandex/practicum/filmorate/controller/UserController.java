package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private int idUser = 1;
    protected int generateIdUser() {
        return idUser++;
    }

    @GetMapping
    public List<User> findALl() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Ошибка валидации - электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Ошибка валидации - логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()|| user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Ошибка валидации - дата рождения не может быть в будущем");
        }
        user.setId(generateIdUser());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                throw new ValidationException("Ошибка обновления - логин не может быть пустым и содержать пробелы");
            }
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Ошибка обновления - дата рождения не может быть в будущем");
            }
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Ошибка обновления - невозможно обновить несуществующего пользователя");
        }
    }
}
