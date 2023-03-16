package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final UserStorage inMemoryUserStorage;
    private Map<Integer, Film> films = new HashMap<>();
    private int idFilm = 1;
    private static final LocalDate DATE_OF_FIRST_FILM = LocalDate.of(1895, 12, 28);
    public static final int LENGTH_OF_DESCRIPTION = 200;

    @Autowired
    public InMemoryFilmStorage(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    protected int generateIdFilm() {
        return idFilm++;
    }

    @Override
    public List<Film> findAll() {
        List<Film> filmList = new ArrayList<>(films.values());
        log.info("Количество всех фильмов: {}", filmList.size());
        return filmList;
    }

    @Override
    public Film findFilmById(int id) {
        if (films.get(id) == null) {
            log.warn("невозможно найти несуществующий фильм");
            throw new NotFoundException("Нет фильма с указанным id");
        }
        log.info("Найден фильм с id: {}", films.get(id));
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        if (!validationFilm(film)) {
            log.warn("данные фильма не прошли валидацию");
            throw new ValidationException("Ошибка валидации");
        }
        film.setId(generateIdFilm());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film put(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("невозможно обновить несуществующий фильм");
            throw new NotFoundException("Ошибка обновления - невозможно обновить несуществующий фильм");
        }
        if (validationFilm(film)) {
            films.put(film.getId(), film);
            log.info("Изменен фильм: {}", film);
        }
        return film;
    }

    @Override
    public Film putLike(int idFilm, int userId) {
        Film film = findFilmById(idFilm);
        User user = inMemoryUserStorage.findUserById(userId);
        if (film.getIdLikeUsers().contains(userId)) {
            log.warn("этот пользователь уже поставил лайк данному фильму");
            throw new NotFoundException(String.format("этот пользователь уже поставил лайк фильму с id %d", idFilm));
        }
        film.getIdLikeUsers().add(userId);
        log.info("Фильм {} получил Like  от пользователя с id {}", film, userId);
        return film;
    }

    @Override
    public Film deleteLike(int idFilm, int userId) {
        Film film = findFilmById(idFilm);
        User user = inMemoryUserStorage.findUserById(userId);
        if (!film.getIdLikeUsers().contains(userId)) {
            log.warn("этот пользователь еще не поставил лайк данному фильму");
            throw new NotFoundException(String.format("этот пользователь еще не поставил лайк фильму с id %d", idFilm));
        }
        film.getIdLikeUsers().remove(userId);
        log.info("Пользователь с id {} удалил Like  у фильма {}", userId, film);
        return film;
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getSizeIdLikesUsers).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean validationFilm(Film film) {
        if (film.getName().isBlank()) {
            log.warn("название не может быть пустым");
            throw new ValidationException("Ошибка обновления - название не может быть пустым");
        }
        if (film.getDescription().length() > LENGTH_OF_DESCRIPTION) {
            log.warn("максимальная длина описания — 200 символов");
            throw new ValidationException("Ошибка обновления - максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(DATE_OF_FIRST_FILM)) {
            log.warn("дата релиза — должна быть не раньше 28 декабря 1895 года");
            throw new ValidationException("Ошибка обновления - дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("продолжительность фильма должна быть положительной");
            throw new ValidationException("Ошибка валидации - продолжительность фильма должна быть положительной");
        }
        return true;
    }
}
