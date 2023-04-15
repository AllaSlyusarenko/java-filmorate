package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
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
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
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
        if (findUserById(user.getId()) == null) {
            throw new ValidationException("Невозможно обновить несуществующего пользователя");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        String sqlQuery = "update users set name_user = ?, login  = ?, birthday = ?, email = ? where id_user = ?";
        jdbcTemplate.update(sqlQuery
                , user.getName()
                , user.getLogin()
                , user.getBirthday()
                , user.getEmail()
                , user.getId()
        );
        return user;
    }

    private void deleteUserById(int id) {
        String deleteQuery = "delete from users where id_user = ?";
        jdbcTemplate.update(deleteQuery, id);
        //удалить из связанных таблиц
    }

    @Override
    public boolean addToFriends(int id, int friendId) {
        if (id == friendId) {
            return false;
        }
        User user = findUserById(id);
        User friend = findUserById(friendId);
        String getFriendQuery = "Select * FROM friendship where id_user = ?";
        List<Integer> idsFriendFriend = jdbcTemplate.query(getFriendQuery,
                (rs, rowNum) -> rs.getInt("id_friend"),
                friendId);


        String insertFriendQuery = "INSERT INTO friendship(id_user, id_friend, status) Values(?,?,?)";
        if (idsFriendFriend.contains(id)) {
            jdbcTemplate.update(insertFriendQuery, id, friendId, "true");

            String deleteQuery = "delete from friendship where id_friend = ? and id_user = ?";
            jdbcTemplate.update(deleteQuery, friendId, id);
            String insertQuery = "INSERT INTO friendship(id_user, id_friend, status) Values(?,?,?)";
            jdbcTemplate.update(insertQuery, friendId, id, "true");
        } else {
            jdbcTemplate.update(insertFriendQuery, id, friendId, "false");
        }
        return true;
    }

    @Override
    public boolean deleteFromFriends(int id, int friendId) {
        if (id == friendId) {
            return false;
        }
        User user = findUserById(id);
        User friend = findUserById(friendId);
        String statusFriend = jdbcTemplate.queryForObject("Select status from friendship where id_user = ? and id_friend = ?",
                (rs, rowNum) -> rs.getString("status"), id, friendId);

        String deleteQueryFromFriends = "delete from friendship where id_user = ? and id_friend = ?";
        jdbcTemplate.update(deleteQueryFromFriends, id, friendId);

        if (statusFriend.equals("true")) {
            jdbcTemplate.update("update friendship set status = ? where id_user = ? and id_friend = ?", "false", friendId, id);
        }
        return true;
    }

    @Override
    public List<User> getFriends(int id) {
        String getFriendQuery = "Select *  FROM USERS u WHERE ID_USER IN " +
                " (SELECT id_friend FROM FRIENDSHIP f WHERE id_user= ?) ";
        List<User> friendsUser = jdbcTemplate.query(getFriendQuery, userRowMapper(), id);
        return friendsUser;
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(rs.getInt("id_user"), rs.getString("email"), rs.getString("login"),
                rs.getString("name_user"), rs.getDate("birthday").toLocalDate());
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        String getCommonFriendQuery1 = "SELECT * FROM (Select * FROM friendship where id_user = ?) AS f1" +
                " join (select * from friendship where id_user = ?) AS f2 on f1.id_friend = f2.id_friend";

        String getCommonFriendQuery = "Select * FROM USERS u WHERE ID_USER IN(" +
                " SELECT  f1.id_friend FROM (Select * FROM friendship where id_user = ?) f1 join" +
                " (select * from friendship WHERE id_user=?  ) f2 on f1.id_friend = f2.id_friend)";

        List<User> commonUsers = jdbcTemplate.query(getCommonFriendQuery, userRowMapper(), id, otherId);
        return commonUsers;
    }
}
