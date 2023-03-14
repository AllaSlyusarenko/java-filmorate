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

    @Override
    public Map<Integer, User> getUsers() {
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
        if (users.get(id) != null) {
            log.info("Пользователь с id: {}", id);
            return users.get(id);
        } else {
            log.warn("невозможно найти несуществующего пользователя");
            throw new NotFoundException("Нет пользователя с данными Id");
        }
    }

    @Override
    public User create(User user) {
        if (validationUser(user)) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            user.setId(generateIdUser());
            users.put(user.getId(), user);
            log.info("Добавлен пользователь: {}", user);
            return user;
        }
        return user;
    }

    @Override
    public User put(User user) {
        if (users.containsKey(user.getId())) {
            if (validationUser(user)) {
                if (user.getName() == null || user.getName().isBlank()) {
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

    @Override
    public User addToFriends(int id, int friendId) {
        if (findUserById(id) != null && findUserById(friendId) != null) {
            findUserById(id).getIdFriends().add(friendId);
            getUsers().get(friendId).getIdFriends().add(id);
            log.info("Пользователи: {},{} стали друзьями", findUserById(id), findUserById(friendId));
            return getUsers().get(friendId);
        } else {
            log.warn("невозможно найти несуществующего пользователя");
            throw new NotFoundException("Нет пользователей с данными Id");
        }
    }
    @Override
    public User deleteFromFriends(int id, int friendId) {
        if (findUserById(id) != null && findUserById(friendId) != null && findUserById(id).getIdFriends().contains(friendId)) {
            findUserById(id).getIdFriends().remove(friendId);
            findUserById(friendId).getIdFriends().remove(id);
            log.info("Пользователи: {},{} больше не друзья", findUserById(id), findUserById(friendId));
            return getUsers().get(friendId);
        } else {
            log.warn("невозможно найти несуществующего пользователя");
            throw new NotFoundException("Нет пользователей с данными Id");
        }
    }

    @Override
    public List<User> getFriends(int id) {
        List<User> listFriends = new ArrayList<>();
        if (findUserById(id) != null) {
            for (int idFriends : findUserById(id).getIdFriends()) {
                listFriends.add(findUserById(idFriends));
            }
            log.info("Количество друзей пользователя: {}", listFriends.size());
            return listFriends;
        } else {
            log.warn("невозможно найти несуществующего пользователя");
            throw new NotFoundException("Нет пользователя с данным Id");
        }
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        List<User> listFriends = new ArrayList<>();
        if (findUserById(id) != null && findUserById(otherId) != null) {
            for (int idFriend : findUserById(id).getIdFriends()) {
                if (findUserById(otherId).getIdFriends().contains(idFriend)) {
                    listFriends.add(findUserById(idFriend));
                }
            }
            log.info("Количество общих друзей: {}", listFriends.size());
            return listFriends;
        } else {
            log.warn("невозможно найти несуществующего пользователя");
            throw new NotFoundException("Нет пользователей с данными Id");
        }
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
