//package ru.yandex.practicum.filmorate.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import lombok.SneakyThrows;
//import org.hamcrest.Matchers;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class UserControllerTest {
//    @Autowired
//    private MockMvc mvc;
//    ObjectMapper om = new ObjectMapper();
//    UserController userController;
//
//    @BeforeEach
//    void setup() {
//        userController = new UserController();
//        mvc = MockMvcBuilders.standaloneSetup(userController).build();
//        om.registerModule(new JavaTimeModule());
//    }
//
//    @SneakyThrows
//    @Test
//    void findAll() {
//        User user1_1 = new User();
//        user1_1.setEmail("email1@yandex.ru");
//        user1_1.setLogin("login1");
//        user1_1.setName("Name1");
//        user1_1.setBirthday(LocalDate.of(2015, 3, 15));
//        User user1 = userController.create(user1_1);
//        String jsonRequest = om.writeValueAsString(user1);
//        mvc.perform(get("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().is(200))
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].name").value("Name1"));
//
//        User user2_1 = new User();
//        user2_1.setEmail("email2@yandex.ru");
//        user2_1.setLogin("login2");
//        user2_1.setName("Name2");
//        user2_1.setBirthday(LocalDate.of(2015, 3, 15));
//        User user2 = userController.create(user2_1);
//        String jsonRequest2 = om.writeValueAsString(user2);
//        mvc.perform(get("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest2))
//                .andDo(print())
//                .andExpect(status().is(200))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[1].name").value("Name2"));
//    }
//
//    @SneakyThrows
//    @Test
//    void createEmptyUser() {
//        String jsonRequest = om.writeValueAsString(" ");
//
//        mvc.perform(post("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void createNormalUser() {
//        User user1 = new User();
//        user1.setEmail("email1@yandex.ru");
//        user1.setLogin("login1");
//        user1.setName("Name1");
//        user1.setBirthday(LocalDate.of(2015, 3, 15));
//        String jsonRequest = om.writeValueAsString(user1);
//
//        mvc.perform(post("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().is(200))
//                .andExpect(jsonPath("$.name", Matchers.containsString("Name1")))
//                .andExpect(jsonPath("$.email").value("email1@yandex.ru"));
//    }
//
//    @SneakyThrows
//    @Test
//    void createUserWrongEmail() {
//        User user1 = new User();
//        user1.setEmail("email1yandex.ru");
//        user1.setLogin("login1");
//        user1.setName("Name1");
//        user1.setBirthday(LocalDate.of(2015, 3, 15));
//        String jsonRequest = om.writeValueAsString(user1);
//
//        mvc.perform(post("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void createUserWrongLogin() {
//        User user1 = new User();
//        user1.setEmail("email1@yandex.ru");
//        user1.setLogin("lo gi n1");
//        user1.setName("Name1");
//        user1.setBirthday(LocalDate.of(2015, 3, 15));
//        String jsonRequest = om.writeValueAsString(user1);
//
//        mvc.perform(post("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void createUserWrongBirthday() {
//        User user1 = new User();
//        user1.setEmail("email1@yandex.ru");
//        user1.setLogin("login1");
//        user1.setName("Name1");
//        user1.setBirthday(LocalDate.of(2025, 3, 15));
//        String jsonRequest = om.writeValueAsString(user1);
//
//        mvc.perform(post("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void putNormalUser() {
//        User user1_1 = new User();
//        user1_1.setEmail("email1@yandex.ru");
//        user1_1.setLogin("login1");
//        user1_1.setName("Name1");
//        user1_1.setBirthday(LocalDate.of(2015, 3, 15));
//        User user1 = userController.create(user1_1);
//        User user2_1 = new User(1, "email2@yandex.ru", "login2", "Name2", LocalDate.of(2015, 3, 15));
//        String jsonRequest = om.writeValueAsString(user2_1);
//
//        mvc.perform(put("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().is(200))
//                .andExpect(jsonPath("$.name").value("Name2"))
//                .andExpect(jsonPath("$.email").value("email2@yandex.ru"));
//    }
//
//    @SneakyThrows
//    @Test
//    void putUserWrongId() {
//        User user1_1 = new User();
//        user1_1.setEmail("email1@yandex.ru");
//        user1_1.setLogin("login1");
//        user1_1.setName("Name1");
//        user1_1.setBirthday(LocalDate.of(2015, 3, 15));
//        User user1 = userController.create(user1_1);
//        User user2_1 = new User(999, "email2@yandex.ru", "login2", "Name2", LocalDate.of(2015, 3, 15));
//
//        String jsonRequest = om.writeValueAsString(user2_1);
//
//        mvc.perform(put("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().isNotFound());
//    }
//
//    @SneakyThrows
//    @Test
//    void putUserWrongEmail() {
//        User user1_1 = new User();
//        user1_1.setEmail("email1@yandex.ru");
//        user1_1.setLogin("login1");
//        user1_1.setName("Name1");
//        user1_1.setBirthday(LocalDate.of(2015, 3, 15));
//        User user1 = userController.create(user1_1);
//        User user2_1 = new User(1, "email1 yandex.ru", "login2", "Name2", LocalDate.of(2015, 3, 15));
//
//        String jsonRequest = om.writeValueAsString(user2_1);
//
//        mvc.perform(put("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void putUserWrongLogin() {
//        User user1_1 = new User();
//        user1_1.setEmail("email1@yandex.ru");
//        user1_1.setLogin("login1");
//        user1_1.setName("Name1");
//        user1_1.setBirthday(LocalDate.of(2015, 3, 15));
//        User user1 = userController.create(user1_1);
//        User user2_1 = new User(1, "email1@yandex.ru", "log in2", "Name2", LocalDate.of(2015, 3, 15));
//
//        String jsonRequest = om.writeValueAsString(user2_1);
//
//        mvc.perform(put("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @SneakyThrows
//    @Test
//    void putUserWrongBirthday() {
//        User user1_1 = new User();
//        user1_1.setEmail("email1@yandex.ru");
//        user1_1.setLogin("login1");
//        user1_1.setName("Name1");
//        user1_1.setBirthday(LocalDate.of(2015, 3, 15));
//        User user1 = userController.create(user1_1);
//        User user2_1 = new User(1, "email1@yandex.ru", "login2", "Name2", LocalDate.of(2025, 3, 15));
//
//        String jsonRequest = om.writeValueAsString(user2_1);
//
//        mvc.perform(put("/users")
//                        .contentType("application/json")
//                        .content(jsonRequest))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//}