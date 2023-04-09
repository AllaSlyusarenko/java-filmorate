package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
@Component
@Primary
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
    public Film create(Film film) { //add
        return null;
    }

    @Override
    public Film put(Film film) { //update
        return null;
    }
    //удалить фильм

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
    // получить лайки фильма по id фильма
    //получить все жанры фильма
    //make film
    //какая-то валидация
}
