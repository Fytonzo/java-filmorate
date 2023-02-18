package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("filmMemoryStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Mpa> mpas = new HashMap<>();
    private final Map<Integer, Genre> genres = new HashMap<>();
    private static final LocalDate FILM_STARTDATE = LocalDate.of(1895, 12, 28);

    private static int id = 0;

    private Integer generateId() {
        ++id;
        return id;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(FILM_STARTDATE)) {
            log.info("Не пройдна валидация даты выпуска фильма. Так рано фильмы не снимали!");
            throw new ValidationException("Так рано фильмы не снимали!");
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен в список!");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new EntityNotFoundException("Фильма с таким ID нет в списке");
        } else {
            log.info("Фильм с ID {}, успешно обновлён!", film.getId());
            films.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new EntityNotFoundException("Фильма с таким id нет!");
        }
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        Collection<Film> filmCollection = films.values();
        return filmCollection
                .stream()
                .sorted(Comparator.comparing(Film::popularity).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Genre> getAllGenres() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public Genre getGenreById(int id) {
        return genres.get(id);
    }

    @Override
    public List<Mpa> getAllMpa() {
        return new ArrayList<>(mpas.values());
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpas.get(id);
    }

    @Override
    public void likeAdd(Integer filmId, Integer userId) {
        films.get(filmId).addLIke(userId);
    }

    @Override
    public void likeRemove(Integer filmId, Integer userId) {
        films.get(filmId).removeLike(userId);
    }

    @Override
    public boolean checkFilmInDb(Integer filmId) {
        if (films.containsKey(filmId)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean checkMpaInDb(Integer mpaId) {
        if (mpas.containsKey(mpaId)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean checkGenreInDb(Integer genreId) {
        if (genres.containsKey(genreId)) {
            return true;
        } else {
            return false;
        }
    }
}
