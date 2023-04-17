package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Test
    void create() {
        Film film1 = new Film("Film1", "DescriptionFilm1", LocalDate.of(2015, 07, 06), 120,
                new MPA(1, "G"), Collections.emptyList());
        filmStorage.create(film1);
        Film filmExpected = filmStorage.findFilmById(1);
        assertThat(film1).isEqualTo(filmExpected);
    }

    @Test
    void findFilmById() {
        Film film1 = new Film("Film1", "DescriptionFilm1", LocalDate.of(2015, 07, 06), 120,
                new MPA(1, "G"), Collections.emptyList());
        filmStorage.create(film1);
        Film filmExpected = filmStorage.findFilmById(1);
        assertThat(filmExpected.getId()).isEqualTo(1);
        assertThat(filmExpected.getName()).isEqualTo("Film1");
    }


    @Test
    void update() {
        Film film1 = new Film("Film1", "DescriptionFilm1", LocalDate.of(2015, 07, 06), 120,
                new MPA(1, "G"), Collections.emptyList());
        filmStorage.create(film1);
        Film film2 = new Film(1, "Film2", "DescriptionFilm2", LocalDate.of(2015, 07, 05), 120,
                new MPA(1, "G"), Collections.emptyList());
        filmStorage.update(film2);
        Film filmExpected = filmStorage.findFilmById(1);
        assertThat(filmExpected.getId()).isEqualTo(1);
        assertThat(filmExpected.getName()).isEqualTo("Film2");
    }

    @Test
    void findAll() {
        Film film1 = new Film("Film1", "DescriptionFilm1", LocalDate.of(2015, 07, 06), 120,
                new MPA(1, "G"), Collections.emptyList());
        filmStorage.create(film1);
        Film film2 = new Film("Film2", "DescriptionFilm2", LocalDate.of(2015, 07, 05), 120,
                new MPA(1, "G"), Collections.emptyList());
        filmStorage.create(film2);
        List<Film> allFilms = filmStorage.findAll();
        assertThat(allFilms).containsOnly(new Film(1, "Film1", "DescriptionFilm1", LocalDate.of(2015, 07, 06), 120,
                new MPA(1, "G"), Collections.emptyList()), new Film(2, "Film2", "DescriptionFilm2", LocalDate.of(2015, 07, 05), 120,
                new MPA(1, "G"), Collections.emptyList()));
    }

    @Test
    void findGenreById() {
        Genre genre = filmStorage.findGenreById(1);
        assertThat(genre).isEqualTo(new Genre(1, "Комедия"));
    }

    @Test
    void findAllGenres() {
        List<Genre> allGenres = filmStorage.findAllGenres();
        assertThat(allGenres.size()).isEqualTo(6);
        assertThat(allGenres.get(0).getName()).isEqualTo("Комедия");
    }

    @Test
    void findMpaById() {
        MPA mpa = filmStorage.findMpaById(1);
        assertThat(mpa).isEqualTo(new MPA(1, "G"));
    }

    @Test
    void findAllMpa() {
        List<MPA> allMpa = filmStorage.findAllMpa();
        assertThat(allMpa.size()).isEqualTo(5);
        assertThat(allMpa.get(0).getName()).isEqualTo("G");
    }
}