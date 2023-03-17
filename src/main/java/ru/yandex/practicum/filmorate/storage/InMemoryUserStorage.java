package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private int idUser = 1;

    protected int generateIdUser() {
        return idUser++;
    }

    private Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>(users.values());
        log.info("Количество всех пользователей: {}", userList.size());
        return userList;
    }

    @Override
    public User findUserById(int id) {
        if (users.get(id) == null) {
            log.warn("невозможно найти несуществующего пользователя");
            throw new NotFoundException("Нет пользователя с данными Id");
        }
        log.info("Пользователь с id: {}", id);
        return users.get(id);
    }

    @Override
    public User create(User user) {
        if (!validationUser(user)) {
            log.warn("данные пользователя не прошли валидацию");
            throw new ValidationException("Ошибка валидации");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(generateIdUser());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User put(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("невозможно обновить несуществующего пользователя");
            throw new NotFoundException("Ошибка обновления - невозможно обновить несуществующего пользователя");
        }
        if (validationUser(user)) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Изменен пользователь: {}", user);
        }
        return user;
    }

    @Override
    public User addToFriends(int id, int friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        user.getIdFriends().add(friendId);
        friend.getIdFriends().add(id);
        log.info("Пользователи: {},{} стали друзьями", user, friend);
        return getUsers().get(friendId);
    }

    @Override
    public User deleteFromFriends(int id, int friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        if (!user.getIdFriends().contains(friendId)) {
            log.warn("пользователи не являются друзьями друг друга");
            throw new NotFoundException("пользователи не являются друзьями друг друга");
        }
        user.getIdFriends().remove(friendId);
        friend.getIdFriends().remove(id);
        log.info("Пользователи: {},{} больше не друзья", user, friend);
        return users.get(friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        List<User> listFriends = new ArrayList<>();
        User user = findUserById(id);
        for (int idFriends : user.getIdFriends()) {
            listFriends.add(findUserById(idFriends));
        }
        log.info("Количество друзей пользователя: {}", listFriends.size());
        return listFriends;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        User user = findUserById(id);
        User other = findUserById(otherId);
        List<User> listFriends = new ArrayList<>();
        for (int idFriend : user.getIdFriends()) {
            if (other.getIdFriends().contains(idFriend)) {
                listFriends.add(findUserById(idFriend));
            }
        }
        log.info("Количество общих друзей: {}", listFriends.size());
        return listFriends;
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
