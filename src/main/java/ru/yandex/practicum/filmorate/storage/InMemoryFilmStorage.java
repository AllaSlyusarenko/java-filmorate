package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private Map<Integer, Film> films = new HashMap<>();
    private int idFilm = 1;
    private static final LocalDate DATE_OF_FIRST_FILM = LocalDate.of(1895, 12, 28);
    public static final int LENGTH_OF_DESCRIPTION = 200;

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
        if (films.get(id) != null) {
            log.info("Найден фильм с id: {}", films.get(id));
            return films.get(id);
        } else {
            log.warn("невозможно найти несуществующий фильм");
            throw new NotFoundException("Нет фильма с указанным id");
        }
    }

    @Override
    public Film create(Film film) {
        if (validationFilm(film)) {
            film.setId(generateIdFilm());
            films.put(film.getId(), film);
            log.info("Добавлен фильм: {}", film);
            return film;
        }
        return film;
    }

    @Override
    public Film put(Film film) {
        if (films.containsKey(film.getId())) {
            if (validationFilm(film)) {
                films.put(film.getId(), film);
                log.info("Изменен фильм: {}", film);
                return film;
            }
        } else {
            log.warn("невозможно обновить несуществующий фильм");
            throw new NotFoundException("Ошибка обновления - невозможно обновить несуществующий фильм");
        }
        return film;
    }

    @Override
    public Film putLike(int id, int userId) {
        if (films.containsKey(id) && !findFilmById(id).getIdLikeUsers().contains(userId)) {
            findFilmById(id).getIdLikeUsers().add(userId);
            log.info("Фильм {} получил Like  от пользователя с id {}", findFilmById(id), userId);
            return findFilmById(id);
        } else {
            log.warn("невозможно найти несуществующий фильм");
            throw new NotFoundException(String.format("Нет фильма с данным id %d", id));
        }
    }

    @Override
    public Film deleteLike(int idFilm, int userId) {
        if (films.containsKey(idFilm) && findFilmById(idFilm).getIdLikeUsers().contains(userId)) {
            findFilmById(idFilm).getIdLikeUsers().remove(userId);
            log.info("Пользователь с id {} удалил Like  у фильма {}", userId, findFilmById(idFilm));
            return findFilmById(idFilm);
        } else {
            log.warn("невозможно найти несуществующий фильм");
            throw new NotFoundException(String.format("Нет фильма с данным id %d", idFilm));
        }
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> sort(f1, f2))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compareTo(Film film1, Film film2) {
        int result = Integer.compare(film1.getIdLikeUsers().size(), film2.getIdLikeUsers().size());
        return result;
    }

    private int sort(Film f1, Film f2) {
        String sort = "desc";
        int result = compareTo(f1, f2);
        if (sort.equals("desc")) {
            result = -1 * result;
        }
        return result;
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
