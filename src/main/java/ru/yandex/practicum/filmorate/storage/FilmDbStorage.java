package ru.yandex.practicum.filmorate.storage;

import org.hibernate.boot.model.source.spi.SecondaryTableSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() { //  find all films
        String sqlQuery = "SELECT * FROM films f LEFT JOIN FILMGENRE fg ON f.ID_FILM = fg.ID_FILM LEFT JOIN GENRES g ON fg.id_genre=g.ID_GENRE";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

        //stream() - .toMap (id, Film), reduce - при одинак id - правило объединения
//        persons.stream()
//                .collect(Collectors
//                        .toMap(Person::getNationality, Person::getName, (name1, name2) -> name1);
        BinaryOperator<Film> mergeFunction = (film1, film2) -> {
            Set<Genre> genreHashSet = new HashSet<>(film1.getGenres());
            genreHashSet.addAll(film2.getGenres());
            return film1.toBuilder()
                    .genres(genreHashSet)
                    .build();
        };

        Map<Integer, Film> filmMap = filmList.stream()
                .collect(Collectors.toMap(Film::getId, film -> film, mergeFunction));
        List<Film> films = new ArrayList<>(filmMap.values());
        return films;
    }

    @Override
    public Film findFilmById(int id) {

        return null;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("id_film"))
                .name(resultSet.getString("name_film"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(resultSet.getInt("id_mpa"))
                .genres(Set.of(new Genre(resultSet.getInt("id_genre"),resultSet.getString("name_genre"))))
                .build();
        return film;
    }

    @Override
    public Film create(Film film) { //add
        return null;
    }

    @Override
    public Film put(Film film) { //update
        return null;
    }


    @Override
    public Film putLike(int id, int userId) {
        return null;
    }

    @Override
    public Film deleteLike(int idFilm, int userId) {
        return null;
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return null;
    }
    // получить лайки фильма по id фильма
    //получить все жанры фильма
    //make film
    //какая-то валидация
}
