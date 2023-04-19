package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final LocalDate DATE_OF_FIRST_FILM = LocalDate.of(1895, 12, 28);
    private static final int LENGTH_OF_DESCRIPTION = 200;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable(value = "id") int id) {
        log.info("Просмотр фильма по идентификатору");
        return filmService.getFilmStorage().findFilmById(id);
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Просмотр всех фильмов");
        return filmService.getFilmStorage().findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validationFilm(film);
        log.info("Создан новый фильм");
        return filmService.getFilmStorage().create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validationFilm(film);
        log.info("Обновление фильма");
        return filmService.getFilmStorage().update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLike(@PathVariable(value = "id") int id,
                           @PathVariable(value = "userId") int userId) {
        log.info("Пользователь поставил фильму лайк");
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable(value = "id") int id,
                              @PathVariable(value = "userId") int userId) {
        log.info("Пользователь удалил лайк у фильма");
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Просмотр популярных фильмов");
        return filmService.getTopFilms(count);
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
