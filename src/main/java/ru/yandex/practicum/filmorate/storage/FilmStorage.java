package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.SQLException;
import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Integer id) throws SQLException;

    List<Film> getPopularFilms(Integer count);

    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    List<Mpa> getAllMpa();

    Mpa getMpaById(int id);

    void likeAdd(Integer filmId, Integer userId);

    void likeRemove(Integer filmId, Integer userId);

    boolean checkFilmInDb(Integer filmId);

    boolean checkMpaInDb(Integer mpaId);

    boolean checkGenreInDb(Integer genreId);
}
