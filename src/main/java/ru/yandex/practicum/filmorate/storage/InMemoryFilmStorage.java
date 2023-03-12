package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage{

    @Override
    public List<Film> findAll() {
        return null;
    }

    @Override
    public Film create(Film film) {
        return null;
    }

    @Override
    public Film put(Film film) {
        return null;
    }

    @Override
    public Film delete(Film film) {
        return null;
    }
}
