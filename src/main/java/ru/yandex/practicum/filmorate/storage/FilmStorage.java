package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface FilmStorage {

    public List<Film> findAll();

    public Film findFilmById(int id);

    public Film create(Film film);

    public Film update(Film film);

    public boolean addLike(int id, int userId);

    public boolean deleteLike(int idFilm, int userId);

    List<Film> getTopFilms(int count);

    Genre findGenreById(int id);

    List<Genre> findAllGenres();

    MPA findMpaById(int id);

    List<MPA> findAllMpa();
}
