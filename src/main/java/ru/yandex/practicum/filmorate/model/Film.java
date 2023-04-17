package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.controller.FilmController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"id", "name", "description", "releaseDate", "duration", "mpa"})
public class Film {
    @NotNull
    private int id;
    @NotBlank
    private String name;
    @Size(min = 1, max = FilmController.LENGTH_OF_DESCRIPTION)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private int duration;
    @NotNull
    private MPA mpa;
    private List<Genre> genres;

    public Film(String name, String description, LocalDate releaseDate, int duration, MPA mpa, List<Genre> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }
}