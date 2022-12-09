package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage, UserStorage inMemoryUserStorage){
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void likeAdd(Integer filmId, Integer userId){
        inMemoryUserStorage.getUser(userId);
        Film film = inMemoryFilmStorage.getFilm(filmId);
        film.addLIke(userId);
    }

    public void likeRemove(Integer filmId, Integer userId){
        inMemoryUserStorage.getUser(userId);
        Film film = inMemoryFilmStorage.getFilm(filmId);
        film.removeLike(userId);

    }

    public List<Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public Film getFilm(Integer id) {
        return inMemoryFilmStorage.getFilm(id);
    }

    public List<Film> getPopularFilms(Integer count){
        return inMemoryFilmStorage.getPopularFilms(count);
    }
}
