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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;


import java.sql.Date;
import java.sql.PreparedStatement;
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
        String sqlQuery = "SELECT * FROM films f JOIN FILMGENRE fg ON f.ID_FILM = fg.ID_FILM JOIN GENRES g ON fg.id_genre=g.ID_GENRE";
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
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select f.id_film, f.name_film, f.description," +
                " f.release_date, f.duration, f.id_mpa, mpa.name_mpa  from films f" +
                " LEFT JOIN MPA mpa ON f.ID_mpa = mpa.ID_MPA where id_film = ?", id);
        if (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getInt("id_film"));
            film.setName(filmRows.getString("name_film"));
            film.setDescription(filmRows.getString("description"));
            film.setReleaseDate(filmRows.getDate("release_date").toLocalDate());
            film.setDuration(filmRows.getInt("duration"));
            film.setMpa(new MPA(filmRows.getInt("id_mpa"), filmRows.getString("name_mpa")));
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Фильм с идентификатором не найден.");
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("id_film"))
                .name(resultSet.getString("name_film"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new MPA(resultSet.getInt("id_mpa"), resultSet.getString("name_mpa")))
                .genres(Set.of(new Genre(resultSet.getInt("id_genre"), resultSet.getString("name_genre"))))
                .build();
        return film;
    }

    @Override
    public Film create(Film film) { //add
        String insertQuery = "INSERT INTO public.films (name_film, description, release_date, duration, id_mpa) VALUES (?, ?, ?, ?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(insertQuery, new String[]{"id_film"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        if (film.getGenres() != null) {
            film.setGenres(film.getGenres());
        } else {
            film.setGenres(Collections.emptySet());
        }

        film.setMpa(getMPA(film.getMpa().getId()));

        String genresQuery = "INSERT INTO FILMGENRE(id_film, id_genre) VALUES (?, ?) ";
        if (film.getGenres() != null) {
            for( Genre genre: film.getGenres()){
                jdbcTemplate.update(genresQuery, film.getId(), genre.getId());
            }
        }
        return findFilmById(keyHolder.getKey().intValue());
    }

    private MPA getMPA(int id_mpa) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("Select name_mpa from mpa where id_mpa = ?", id_mpa);
        if (mpaRows.next()) {
            MPA mpa = new MPA();
            mpa.setId(id_mpa);
            mpa.setNameMPA(mpaRows.getString("name_mpa"));
            return mpa;
        } else {
            throw new NotFoundException("Не найдено");
        }
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
