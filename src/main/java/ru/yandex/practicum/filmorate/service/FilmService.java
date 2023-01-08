package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDBStorage") FilmStorage filmStorage, @Qualifier("userDBStorage") UserStorage userStorage){
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void likeAdd(Integer filmId, Integer userId){
        filmStorage.likeAdd(filmId, userId);
    }

    public void likeRemove(Integer filmId, Integer userId){
        filmStorage.likeRemove(filmId, userId);

    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id);
    }

    public List<Film> getPopularFilms(Integer count){
        return filmStorage.getPopularFilms(count);
    }

    public List<Genre> getAllGenres(){
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id){
        return filmStorage.getGenreById(id);
    }

    public List<Mpa> getAllMpa(){
        return filmStorage.getAllMpa();
    }

    public Mpa getMpaById(int id){
        return filmStorage.getMpaById(id);
    }
}
