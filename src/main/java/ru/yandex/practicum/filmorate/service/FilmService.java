package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public Film deleteLike(int idFilm, int userId) {
        return filmStorage.deleteLike(idFilm, userId); //удалить у фильма лайк
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count); // через стримы отсортировать по количеству лайков - сортид и компаратор, только по уменьшению лайков, лимит - 10
    }

    public Film putLike(int idFilm, int userId) {
        return filmStorage.putLike(idFilm, userId);
    }
}
