package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    @NotNull
    private int id;
    @NotBlank
    private String name;
    @Size(min = 1, max = InMemoryFilmStorage.LENGTH_OF_DESCRIPTION)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private long duration;
    private Set<Integer> idLikeUsers = new HashSet<>();
}