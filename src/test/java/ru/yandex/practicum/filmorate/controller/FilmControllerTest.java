package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mvc;
    ObjectMapper om = new ObjectMapper();
    FilmController filmController;

    @BeforeEach
    void setup() {
        filmController = new FilmController();
        mvc = MockMvcBuilders.standaloneSetup(filmController).build();
        om.registerModule(new JavaTimeModule());
    }

    @SneakyThrows
    @Test
    void findAll() {
        Film film1_1 = new Film();
        film1_1.setName("Film 1");
        film1_1.setDescription("Description Film1");
        film1_1.setReleaseDate(LocalDate.of(2015, 3, 15));
        film1_1.setDuration(6315);
        Film film1 = filmController.create(film1_1);
        String jsonRequest = om.writeValueAsString(film1);
        mvc.perform(get("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Film 1"));

        Film film2_1 = new Film();
        film2_1.setName("Film 2");
        film2_1.setDescription("Description Film2");
        film2_1.setReleaseDate(LocalDate.of(2015, 3, 15));
        film2_1.setDuration(6315);
        Film film2 = filmController.create(film2_1);
        String jsonRequest2 = om.writeValueAsString(film2);
        mvc.perform(get("/films")
                        .contentType("application/json")
                        .content(jsonRequest2))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name").value("Film 2"));
    }

    @SneakyThrows
    @Test
    void createEmptyFilm() {
        String jsonRequest = om.writeValueAsString(" ");

        mvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createNormalFilm() {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Description Film1");
        film.setReleaseDate(LocalDate.of(2015, 3, 15));
        film.setDuration(6315);
        String jsonRequest = om.writeValueAsString(film);

        mvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name", Matchers.containsString("Film1")))
                .andExpect(jsonPath("$.description").value("Description Film1"));
    }

    @SneakyThrows
    @Test
    void createFilmWrongName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description Film1");
        film.setReleaseDate(LocalDate.of(2015, 3, 15));
        film.setDuration(6315);
        String jsonRequest = om.writeValueAsString(film);

        mvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createFilmWrongDescription() {
        Film film = new Film();
        film.setName("Film 1");
        film.setDescription("Description Film1 qwrtywretryterytqwe weyqtrwetyqrwetyr ywtreytqrweytqrweytrqw yrwerweytr" +
                "wrqeqrwetyqrwe gwehjwgehjg qjwhgejhqwgejhqgw wjejhdtgwetqwyet gweqwgteyqwt wetqywetqyuwte wqtwueytqywuet" +
                "gehqjwgehjgw gwejhgehjqwgej wjegqjhwegjqhwge qgejqgwejhqgwejhg wjhgejhqgwejhqgw very long description");
        film.setReleaseDate(LocalDate.of(2015, 3, 15));
        film.setDuration(6315);
        String jsonRequest = om.writeValueAsString(film);

        mvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createFilmWrongReleaseDate() {
        Film film = new Film();
        film.setName("Film 1");
        film.setDescription("Description Film1");
        film.setReleaseDate(LocalDate.of(1894, 3, 15));
        film.setDuration(6315);
        String jsonRequest = om.writeValueAsString(film);

        mvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createFilmWrongDuration() {
        Film film = new Film();
        film.setName("Film 1");
        film.setDescription("Description Film1");
        film.setReleaseDate(LocalDate.of(2015, 3, 15));
        film.setDuration(-1);
        String jsonRequest = om.writeValueAsString(film);

        mvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void putNormalFilm() {
        Film film1_1 = new Film();
        film1_1.setName("Film 1");
        film1_1.setDescription("Description Film1");
        film1_1.setReleaseDate(LocalDate.of(2015, 3, 15));
        film1_1.setDuration(6315);
        Film film1 = filmController.create(film1_1);
        Film film2_1 = new Film(1, "Film 2", "Description Film1", LocalDate.of(2015, 3, 15), 6315);

        String jsonRequest = om.writeValueAsString(film2_1);

        mvc.perform(put("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name").value("Film 2"))
                .andExpect(jsonPath("$.description").value("Description Film1"));
    }

    @SneakyThrows
    @Test
    void putFilmWrongId() {
        Film film1_1 = new Film();
        film1_1.setName("Film 1");
        film1_1.setDescription("Description Film1");
        film1_1.setReleaseDate(LocalDate.of(2015, 3, 15));
        film1_1.setDuration(6315);
        Film film1 = filmController.create(film1_1);
        Film film2_1 = new Film(999, "Film 2", "Description Film1", LocalDate.of(2015, 3, 15), 6315);

        String jsonRequest = om.writeValueAsString(film2_1);

        mvc.perform(put("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void putFilmWrongName() {
        Film film1_1 = new Film();
        film1_1.setName("Film 1");
        film1_1.setDescription("Description Film1");
        film1_1.setReleaseDate(LocalDate.of(2015, 3, 15));
        film1_1.setDuration(6315);
        Film film1 = filmController.create(film1_1);
        Film film2_1 = new Film(1, "", "Description Film1", LocalDate.of(2015, 3, 15), 6315);

        String jsonRequest = om.writeValueAsString(film2_1);

        mvc.perform(put("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void putFilmWrongDescription() {
        Film film1_1 = new Film();
        film1_1.setName("Film 1");
        film1_1.setDescription("Description Film1");
        film1_1.setReleaseDate(LocalDate.of(2015, 3, 15));
        film1_1.setDuration(6315);
        Film film1 = filmController.create(film1_1);
        Film film2_1 = new Film(1, "Film 2", "Description Film1 qwrtywretryterytqwe weyqtrwetyqrwetyr ywtreytqrweytqrweytrqw yrwerweytr" +
                "wrqeqrwetyqrwe gwehjwgehjg qjwhgejhqwgejhqgw wjejhdtgwetqwyet gweqwgteyqwt wetqywetqyuwte wqtwueytqywuet" +
                "gehqjwgehjgw gwejhgehjqwgej wjegqjhwegjqhwge qgejqgwejhqgwejhg wjhgejhqgwejhqgw very long description", LocalDate.of(2015, 3, 15), 6315);

        String jsonRequest = om.writeValueAsString(film2_1);

        mvc.perform(put("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void putFilmWrongReleaseDate() {
        Film film1_1 = new Film();
        film1_1.setName("Film 1");
        film1_1.setDescription("Description Film1");
        film1_1.setReleaseDate(LocalDate.of(2015, 3, 15));
        film1_1.setDuration(6315);
        Film film1 = filmController.create(film1_1);
        Film film2_1 = new Film(1, "Film 2", "Description Film1", LocalDate.of(1894, 3, 15), 6315);

        String jsonRequest = om.writeValueAsString(film2_1);

        mvc.perform(put("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void putFilmWrongDuration() {
        Film film1_1 = new Film();
        film1_1.setName("Film 1");
        film1_1.setDescription("Description Film1");
        film1_1.setReleaseDate(LocalDate.of(2015, 3, 15));
        film1_1.setDuration(6315);
        Film film1 = filmController.create(film1_1);
        Film film2_1 = new Film(1, "Film 2", "Description Film1", LocalDate.of(2015, 3, 15), -1);
        String jsonRequest = om.writeValueAsString(film2_1);

        mvc.perform(put("/films")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}