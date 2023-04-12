package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id_user"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name_user"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public User findUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from public.users where id_user = ?", id);
        if (userRows.next()) {
            User user = new User(
                    userRows.getInt("id_user"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name_user"),
                    userRows.getDate("birthday").toLocalDate());
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь с идентификатором не найден.");
        }
    }

    @Override
    public User create(User user) { //add new user
        String insertQuery = "INSERT INTO public.users (email, login, name_user, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(insertQuery, new String[]{"id_user"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return findUserById(keyHolder.getKey().intValue());
    }

    @Override
    public User put(User user) {
        if (findUserById(user.getId())== null){
            throw new ValidationException("Невозможно обновить несуществующего пользователя");
        }
        String sqlQuery = "update users set name_user = ?, login  = ?, birthday = ?, email = ? where id_user = ?";
        jdbcTemplate.update(sqlQuery
                , user.getName()
                , user.getLogin()
                , user.getBirthday()
                , user.getEmail()
                ,user.getId()
                );
        return user;
    }

    @Override
    public User addToFriends(int id, int friendId) {
        return null;
    }

    @Override
    public User deleteFromFriends(int id, int friendId) {
        return null;
    }

    @Override
    public List<User> getFriends(int id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        return null;
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
