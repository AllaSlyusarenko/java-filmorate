package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;
    private final Logger log = LoggerFactory.getLogger(MpaController.class);

    @Autowired
    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/{id}")
    public MPA findMpaById(@PathVariable(value = "id") int id) {
        log.info("Просмотр рейтинга MPA по идентификатору");
        return filmService.getFilmStorage().findMpaById(id);
    }

    @GetMapping
    public List<MPA> findAllMpa() {
        log.info("Просмотр всех рейтингов MPA");
        return filmService.getFilmStorage().findAllMpa();
    }
}
