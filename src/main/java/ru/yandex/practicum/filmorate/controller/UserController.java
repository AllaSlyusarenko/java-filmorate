package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable int id) { // get user by id
        return userService.getUserStorage().findUserById(id);
    }

    @GetMapping
    public List<User> findAll() { // get all users
        return userService.getUserStorage().findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) { //add new user
        if (validationUser(user)) {
            return userService.getUserStorage().create(user);
        } else {
            throw new ValidationException("Валидация не пройдена");
        }
    }

    @PutMapping
    public User put(@RequestBody User user) { //update user
        if (validationUser(user)) {
            return userService.getUserStorage().put(user);
        } else {
            throw new ValidationException("Валидация не пройдена");
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public boolean addToFriends(@PathVariable(value = "id") int id,
                                @PathVariable(value = "friendId") int friendId) {
        return userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public boolean deleteFromFriends(@PathVariable(value = "id") int id,
                                     @PathVariable(value = "friendId") int friendId) {
        return userService.deleteFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable(value = "id") int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable(value = "id") int id,
                                        @PathVariable(value = "otherId") int otherId) {
        return userService.getCommonFriends(id, otherId);
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
