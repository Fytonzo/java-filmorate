package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class GenreService {
    private final FilmStorage filmStorage;

    public GenreService(@Qualifier("filmDBStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        filmStorage.checkGenreInDb(id);
        return filmStorage.getGenreById(id);
    }
}
