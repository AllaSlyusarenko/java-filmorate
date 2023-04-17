package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;
    private final Logger log = LoggerFactory.getLogger(GenreController.class);

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable(value = "id") int id) {
        log.info("Просмотр жанра по идентификатору");
        return filmService.getFilmStorage().findGenreById(id);
    }

    @GetMapping
    public List<Genre> findAll() {
        log.info("Получение всех жанров");
        return filmService.getFilmStorage().findAllGenres();
    }
}
