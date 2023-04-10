merge into GENRES (id_genre, name_genre)
values (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

merge into MPA (id_mpa, name_mpa)
values (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

INSERT INTO FILMS (name_film, description, release_date, duration, id_mpa) Values('привет', 'privet', '2023-04-9', 100, 1);
INSERT INTO FILMGENRE  (id_film, id_genre) Values(1,1);
INSERT INTO FILMGENRE  (id_film, id_genre) Values(1,2);
INSERT INTO FILMGENRE  (id_film, id_genre) Values(1,3);