package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void create() {
        User user1 = new User("user1@mail.ru", "user1_login", "user1_name", LocalDate.of(2012, 03, 15));
        userStorage.create(user1);
        User userExpected = userStorage.findUserById(1);

        assertThat(user1).isEqualTo(userExpected);
    }

    @Test
    public void testFindUserById() {
        User user1 = new User("user1@mail.ru", "user1_login", "user1_name", LocalDate.of(2012, 03, 15));
        userStorage.create(user1);

        User userExpected = userStorage.findUserById(1);
        assertThat(userExpected.getId()).isEqualTo(1);
        assertThat(userExpected.getName()).isEqualTo("user1_name");
    }

    @Test
    void findAll() {
        User user1 = new User("user1@mail.ru", "user1_login", "user1_name", LocalDate.of(2012, 03, 15));
        userStorage.create(user1);
        User user2 = new User("user2@mail.ru", "user2_login", "user2_name", LocalDate.of(2012, 03, 20));
        userStorage.create(user2);
        List<User> allUsers = userStorage.findAll();
        assertThat(allUsers).containsOnly(new User(1, "user1@mail.ru", "user1_login", "user1_name", LocalDate.of(2012, 03, 15)), new User(2, "user2@mail.ru", "user2_login", "user2_name", LocalDate.of(2012, 03, 20)));
    }

    @Test
    void put() {
        User user1 = new User("user1@mail.ru", "user1_login", "user1_name", LocalDate.of(2012, 03, 15));
        userStorage.create(user1);
        User user2 = new User(1, "user2@mail.ru", "user2_login", "user2_name", LocalDate.of(2012, 03, 20));
        userStorage.put(user2);
        User userExpected = userStorage.findUserById(1);
        assertThat(userExpected.getId()).isEqualTo(1);
        assertThat(userExpected.getName()).isEqualTo("user2_name");
        assertThat(userExpected.getBirthday()).isEqualTo(LocalDate.of(2012, 03, 20));
    }

    @Test
    void addToFriends() {
        User user1 = new User("user1@mail.ru", "user1_login", "user1_name", LocalDate.of(2012, 03, 15));
        userStorage.create(user1);
        User user2 = new User("user2@mail.ru", "user2_login", "user2_name", LocalDate.of(2012, 03, 20));
        userStorage.create(user2);
        userStorage.addToFriends(1, 2);
        assertThat(userStorage.getFriends(1).size()).isEqualTo(1);
        userStorage.deleteFromFriends(1, 2);
        assertThat(userStorage.getFriends(1).size()).isEqualTo(0);
    }
}