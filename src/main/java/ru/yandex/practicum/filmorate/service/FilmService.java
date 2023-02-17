package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private static final LocalDate FILMSTARTDATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(@Qualifier("filmDBStorage") FilmStorage filmStorage,
                       @Qualifier("userDBStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void likeAdd(Integer filmId, Integer userId) {
        filmStorage.checkFilmInDb(filmId);
        userStorage.checkUserInDb(userId);
        filmStorage.likeAdd(filmId, userId);
    }

    public void likeRemove(Integer filmId, Integer userId) {
        filmStorage.checkFilmInDb(filmId);
        userStorage.checkUserInDb(userId);
        filmStorage.likeRemove(filmId, userId);

    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(FILMSTARTDATE)) {
            log.info("Не пройдена валидация даты выпуска фильма. Так рано фильмы не снимали!");
            throw new ValidationException("Так рано фильмы не снимали!");
        } else {
            return filmStorage.addFilm(film);
        }

    }

    public Film updateFilm(Film film) {
        filmStorage.checkFilmInDb(film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Integer id) throws SQLException {
        filmStorage.checkFilmInDb(id);
        return filmStorage.getFilm(id);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }


}
