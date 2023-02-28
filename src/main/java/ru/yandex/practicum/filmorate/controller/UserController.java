package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
public class UserController {
    static Map<Integer, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    static int idUser = 1;

    protected static int generateIdUser() {
        return idUser++;
    }

    @GetMapping
    public List<User> findALl() {
        log.info("Список всех пользователей");
        return new ArrayList<>(users.values());
    }
    @ResponseBody
    @PostMapping
    public static User create(@RequestBody User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Ошибка валидации - электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Ошибка валидации - логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("дата рождения не может быть в будущем");
            throw new ValidationException("Ошибка валидации - дата рождения не может быть в будущем");
        }
        user.setId(generateIdUser());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                log.warn("логин не может быть пустым и содержать пробелы");
                throw new ValidationException("Ошибка обновления - логин не может быть пустым и содержать пробелы");
            }
            if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                log.warn("электронная почта не может быть пустой и должна содержать символ @");
                throw new ValidationException("Ошибка обновления - электронная почта не может быть пустой и должна содержать символ @");
            }
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.warn("дата рождения не может быть в будущем");
                throw new ValidationException("Ошибка обновления - дата рождения не может быть в будущем");
            }
            users.put(user.getId(), user);
            log.info("Изменен пользователь: {}", user);
            return user;
        } else {
            log.warn("невозможно обновить несуществующего пользователя");
            throw new NotFoundException("Ошибка обновления - невозможно обновить несуществующего пользователя");
        }
    }
}
