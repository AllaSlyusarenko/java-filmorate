package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable int id) {
        return userService.getUserStorage().findUserById(id);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.getUserStorage().findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.getUserStorage().create(user);
    }

    @PutMapping
    public User put(@RequestBody User user) {
        return userService.getUserStorage().put(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addToFriends(@PathVariable(value = "id") int id,
                             @PathVariable(value = "friendId") int friendId) {
        return userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFromFriends(@PathVariable(value = "id") int id,
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
}
