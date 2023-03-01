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
    private Map<Integer, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private int idUser = 1;

    protected int generateIdUser() {
        return idUser++;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setUsers(HashMap<Integer, User> hashMap) {
        this.users = hashMap;
    }

    @GetMapping
    public List<User> findALl() {
        List<User> userList = new ArrayList<>(users.values());
        log.info("Количество всех пользователей: {}", userList.size());
        return userList;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (validationUser(user)) {
            if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            user.setId(generateIdUser());
            users.put(user.getId(), user);
            log.info("Добавлен пользователь: {}", user);
            return user;
        }
        return user;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (validationUser(user)) {
                if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
                    user.setName(user.getLogin());
                }
                users.put(user.getId(), user);
                log.info("Изменен пользователь: {}", user);
                return user;
            }
        } else {
            log.warn("невозможно обновить несуществующего пользователя");
            throw new NotFoundException("Ошибка обновления - невозможно обновить несуществующего пользователя");
        }
        return user;
    }

    private boolean validationUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Ошибка валидации - электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Ошибка валидации - логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("дата рождения не может быть в будущем");
            throw new ValidationException("Ошибка валидации - дата рождения не может быть в будущем");
        }
        return true;
    }
}
