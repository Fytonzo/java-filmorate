package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Integer generateId();

    List<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Integer id);

    List<Film> getPopularFilms(Integer count);

    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    List<Mpa> getAllMpa();

    Mpa getMpaById(int id);

    void likeAdd(Integer filmId, Integer userId);

    void likeRemove(Integer filmId, Integer userId);
}
