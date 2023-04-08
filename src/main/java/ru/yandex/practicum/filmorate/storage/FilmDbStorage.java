package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public class FilmDbStorage implements FilmStorage{
    @Override
    public List<Film> findAll() {
        return null;
    }

    @Override
    public Film findFilmById(int id) {
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
    public Film putLike(int id, int userId) {
        return null;
    }

    @Override
    public Film deleteLike(int idFilm, int userId) {
        return null;
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return null;
    }
}
