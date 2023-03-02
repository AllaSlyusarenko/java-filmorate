package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;

@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int idFilm = 1;
    private static final LocalDate DATE_OF_FIRST_FILM = LocalDate.of(1895, 12, 28);
    public static final int LENGTH_OF_DESCRIPTION = 200;

    protected int generateIdFilm() {
        return idFilm++;
    }

    @GetMapping
    public List<Film> findAll() {
        List<Film> filmList = new ArrayList<>(films.values());
        log.info("Количество всех фильмов: {}", filmList.size());
        return filmList;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (validationFilm(film)) {
            film.setId(generateIdFilm());
            films.put(film.getId(), film);
            log.info("Добавлен фильм: {}", film);
            return film;
        }
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
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
