package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {

    public List<Film> findAll();

    public Film findFilmById(int id);

    public Film create(Film film);

    public Film put(Film film);

    public Film putLike(int id, int userId);

    public Film deleteLike(int idFilm, int userId);

    List<Film> getTopFilms(int count);
}
