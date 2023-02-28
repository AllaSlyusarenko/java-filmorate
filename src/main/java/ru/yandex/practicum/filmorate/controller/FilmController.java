package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/films")
public class FilmController {
    static Map<Integer, Film> films = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    static int idFilm = 1;

    protected static int  generateIdFilm() {
        return idFilm++;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Список всех фильмов");
        return new ArrayList<>(films.values());
    }

    @ResponseBody
    @PostMapping
    public static Film create(@RequestBody Film film) {
        if (film.getName().isBlank()) {
            log.warn("название фильма не может быть пустым");
            throw new ValidationException("Ошибка валидации - название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("максимальная длина описания — 200 символов");
            throw new ValidationException("Ошибка валидации - максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("дата релиза — должна быть не раньше 28 декабря 1895 года");
            throw new ValidationException("Ошибка валидации - дата релиза — должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("продолжительность фильма должна быть положительной");
            throw new ValidationException("Ошибка валидации - продолжительность фильма должна быть положительной");
        }
        film.setId(generateIdFilm());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public static Film put(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            if (film.getName().isBlank()) {
                log.warn("название не может быть пустым");
                throw new ValidationException("Ошибка обновления - название не может быть пустым");
            }
            if (film.getDescription().length() > 200) {
                log.warn("максимальная длина описания — 200 символов");
                throw new ValidationException("Ошибка обновления - максимальная длина описания — 200 символов");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.warn("дата релиза — должна быть не раньше 28 декабря 1895 года");
                throw new ValidationException("Ошибка обновления - дата релиза — не раньше 28 декабря 1895 года");
            }
            if (film.getDuration() <= 0) {
                log.warn("продолжительность фильма должна быть положительной");
                throw new ValidationException("Ошибка валидации - продолжительность фильма должна быть положительной");
            }
            films.put(film.getId(), film);
            log.info("Изменен фильм: {}", film);
            return film;
        } else {
            log.warn("невозможно обновить несуществующий фильм");
            throw new NotFoundException("Ошибка обновления - невозможно обновить несуществующий фильм");
        }
    }
}
