package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;

@RestController
@RequestMapping("/films")
public class FilmController {
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable(value = "id") int id) {
        return filmService.getFilmStorage().findFilmById(id);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.getFilmStorage().findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.getFilmStorage().create(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        return filmService.getFilmStorage().put(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film putLike(@PathVariable(value = "id") int id,
                        @PathVariable(value = "userId") int userId) {
        return filmService.putLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable(value = "id") int id,
                           @PathVariable(value = "userId") int userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTopFilms(count);
    }
}
