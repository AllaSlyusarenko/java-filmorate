package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {

    //методы добавления, удаления и модификации объектов
    //CRUD
    public List<Film> findAll();

    public Film create(Film film);

    public Film put(Film film);

    public Film delete(Film film);

}
