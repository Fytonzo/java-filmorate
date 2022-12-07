package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage){
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public void likeAdd(Integer filmId, Integer userId){
        Set<Long> filmLikes = getFilm(filmId).getLikes();
        filmLikes.add((long) userId);
        getFilm(filmId).setLikes((HashSet<Long>) filmLikes);
    }

    public void likeRemove(Integer filmId, Integer userId){
        Set<Long> filmLikes = getFilm(filmId).getLikes();
        if (filmLikes.contains((long) userId)){
            filmLikes.remove((long) userId);
            getFilm(filmId).setLikes((HashSet<Long>) filmLikes);
        } else {
            throw new UserNotFoundException("Пользователь с таким id лайк не ставил!");
        }

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
