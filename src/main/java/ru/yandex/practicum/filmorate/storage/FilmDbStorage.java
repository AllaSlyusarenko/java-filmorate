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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select f.id_film, f.name_film, f.description," + " f.release_date, f.duration, f.id_mpa, mpa.name_mpa, fg.id_genre, g.name_genre  from films f " + "LEFT JOIN MPA mpa ON f.ID_mpa = mpa.ID_MPA LEFT JOIN filmgenre fg ON f.ID_FILM = fg.ID_FILM  " + "LEFT JOIN genres g ON fg.id_genre = g.id_genre";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        return filmList;
    }

    @Override
    public Film findFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select f.id_film, f.name_film, f.description," + " f.release_date, f.duration, f.id_mpa, mpa.name_mpa, fg.id_genre, g.name_genre  from films f" + " LEFT JOIN MPA mpa ON f.ID_mpa = mpa.ID_MPA LEFT JOIN FILMGENRE fg ON f.ID_FILM = fg.ID_FILM  " + "LEFT JOIN genres g ON fg.id_genre = g.id_genre where f.id_film = ?", id);

        if (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getInt("id_film"));
            film.setName(filmRows.getString("name_film"));
            film.setDescription(filmRows.getString("description"));
            film.setReleaseDate(filmRows.getDate("release_date").toLocalDate());
            film.setDuration(filmRows.getInt("duration"));
            film.setMpa(new MPA(filmRows.getInt("id_mpa"), filmRows.getString("name_mpa")));
            film.setGenres(getAllGenresByIdFilm(id));
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Фильм с идентификатором не найден.");
        }
    }

    private List<Genre> getAllGenresByIdFilm(int id) {
        String genreQuery = "SELECT fg.ID_FILM , g.id_genre, g.name_genre From FILMGENRE fg" + " JOIN GENRES g ON fg.id_genre=g.ID_GENRE WHERE fg.ID_FILM = ?";
        List<Genre> genreList = jdbcTemplate.query(genreQuery, this::mapRowToGenre, id);
        List<Genre> genres = new ArrayList<>();
        for (Genre genre : genreList) {
            genres.add(genre);
        }
        return genres;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        MPA mpa = mapRowToMPA(resultSet, rowNum);
        Film film = Film.builder().id(resultSet.getInt("id_film")).name(resultSet.getString("name_film")).description(resultSet.getString("description")).releaseDate(resultSet.getDate("release_date").toLocalDate()).duration(resultSet.getInt("duration")).mpa(mpa).genres(getAllGenresByIdFilm(resultSet.getInt("id_film"))).build();
        return film;
    }

    private MPA mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        MPA mpa = new MPA();
        mpa.setId(resultSet.getInt("id_mpa"));
        mpa.setName(resultSet.getString("name_mpa"));
        return mpa;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("id_genre"));
        genre.setName(resultSet.getString("name_genre"));
        return genre;
    }

    @Override
    public Film create(Film film) {
        String insertQuery = "INSERT INTO films (name_film, description, release_date, duration, id_mpa) VALUES (?, ?, ?, ?,?)";
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
        film.setMpa(collectMPA(film.getMpa().getId()));

        String genresQuery = "INSERT INTO FILMGENRE(id_film, id_genre) VALUES (?, ?) ";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genresQuery, film.getId(), genre.getId());
            }
        } else {
            film.setGenres(Collections.emptyList());
        }
        film.setGenres(collectAllGenresIntoFilm(film));
        return film;
    }

    private List<Genre> collectAllGenresIntoFilm(Film film) {
        List fullGenres = new ArrayList();
        if (film.getGenres() != null) {
            List<Genre> uniqueGenre = film.getGenres().stream().distinct().collect(Collectors.toList());
            for (Genre genre : uniqueGenre) {
                fullGenres.add(collectGenre(genre.getId()));
            }
            film.setGenres(fullGenres);
        } else {
            film.setGenres(Collections.emptyList());
        }
        return fullGenres;
    }

    private MPA makeMPA(ResultSet rs, int rowNum) throws SQLException {
        return new MPA(rs.getInt("id_mpa"), rs.getString("name_mpa"));
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id_genre"), rs.getString("name_genre"));
    }

    private MPA collectMPA(int id_mpa) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("Select name_mpa from mpa where id_mpa = ?", id_mpa);
        if (mpaRows.next()) {
            MPA mpa = new MPA();
            mpa.setId(id_mpa);
            mpa.setName(mpaRows.getString("name_mpa"));
            return mpa;
        } else {
            throw new NotFoundException("Не найдено");
        }
    }

    private Genre collectGenre(int id_genre) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("Select name_genre from genres where id_genre = ?", id_genre);
        if (genreRows.next()) {
            Genre genre = new Genre();
            genre.setId(id_genre);
            genre.setName(genreRows.getString("name_genre"));
            return genre;
        } else {
            throw new NotFoundException("Не найдено");
        }
    }

    @Override
    public Film put(Film film) {
        if (findFilmById(film.getId()) == null) {
            throw new ValidationException("Невозможно обновить несуществующий фильм");
        }
        String sqlQuery = "update films set name_film= ?, description  = ?, release_date = ?, duration = ?," + " id_mpa = ? where id_film = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        film.setMpa(collectMPA(film.getMpa().getId()));
        film.setGenres(collectAllGenresIntoFilm(film));

        String deleteQuery = "delete from filmgenre where id_film = ?";
        jdbcTemplate.update(deleteQuery, film.getId());


        String genresQuery = "INSERT INTO FILMGENRE(id_film, id_genre) VALUES (?, ?) ";
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genresQuery, film.getId(), genre.getId());
            }
        } else {
            film.setGenres(Collections.emptyList());
        }
        return film;
    }

    private void deleteFilmById(int id) {
        String deleteQuery = "delete from films where id_film = ?";
        jdbcTemplate.update(deleteQuery, id);

        String deleteQueryFilmGenre = "delete from filmgenre where id_film = ?";
        jdbcTemplate.update(deleteQuery, id);

        String deleteQueryLikeUsers = "delete from likeusers where id_film = ?";
        jdbcTemplate.update(deleteQuery, id);
    }

    @Override
    public boolean putLike(int id, int userId) {
        Film film = findFilmById(id);
        User user = userStorage.findUserById(userId);
        String insertLikeQuery = "INSERT INTO LIKEUSERS  (id_film, id_user) Values(?,?)";
        jdbcTemplate.update(insertLikeQuery, id, userId);
        return true;
    }

    @Override
    public boolean deleteLike(int idFilm, int userId) {
        Film film = findFilmById(idFilm);
        User user = userStorage.findUserById(userId);
        String deleteQueryLikeUsers = "delete from likeusers where id_film = ? and id_user = ?";
        jdbcTemplate.update(deleteQueryLikeUsers, idFilm, userId);
        return true;
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String getQueryTopFilms1 = "SELECT f.*, flu.alllikes " +
                " FROM (SELECT films.*, mpa.name_mpa FROM films JOIN mpa ON films.id_mpa = mpa.id_mpa) AS f " +
                " LEFT JOIN" +
                " (SELECT id_film, COUNT(id_user) as alllikes FROM likeusers GROUP BY id_film ORDER BY COUNT(id_user))AS flu " +
                " ON f.id_film = flu.id_film ORDER BY flu.alllikes DESC LIMIT ?";
        List<Film> topFilms = jdbcTemplate.query(getQueryTopFilms1, this::mapRowToFilm, count);
        return topFilms;
    }

    @Override
    public Genre findGenreById(int idGenre) {
        if (!jdbcTemplate.query("Select * from genres where id_genre = ?", genreRowMapper(), idGenre).isEmpty()) {
            Genre genre = jdbcTemplate.queryForObject("Select * from genres where id_genre = ?", genreRowMapper(), idGenre);
            return genre;
        } else {
            throw new NotFoundException("Неверный идентификатор");
        }
    }

    @Override
    public List<Genre> findAllGenres() {
        return jdbcTemplate.query("select * from genres", genreRowMapper());
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(rs.getInt("id_genre"), rs.getString("name_genre"));
    }

    private RowMapper<MPA> mpaRowMapper() {
        return (rs, rowNum) -> new MPA(rs.getInt("id_mpa"), rs.getString("name_mpa"));
    }

    @Override
    public MPA findMpaById(int idMpa) {
        if (!jdbcTemplate.query("Select * from mpa where id_mpa = ?", mpaRowMapper(), idMpa).isEmpty()) {
            MPA mpa = jdbcTemplate.queryForObject("Select * from mpa where id_mpa = ?", mpaRowMapper(), idMpa);
            return mpa;
        } else {
            throw new NotFoundException("Неверный идентификатор");
        }
    }

    @Override
    public List<MPA> findAllMpa() {
        return jdbcTemplate.query("select * from mpa", mpaRowMapper());
    }
    //какая-то валидация
}
