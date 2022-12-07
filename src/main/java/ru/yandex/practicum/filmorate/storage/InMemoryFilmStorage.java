package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate FILMSTARTDATE = LocalDate.of(1895, 12, 28);

    private static int id = 0;

    @Override
    public Integer generateId() {
        ++id;
        return id;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getName().isBlank()) {
            log.info("Не пройдна валидация названия. Название не может быть пустым!");
            throw new ValidationException("Название не может быть пустым!");
        }
        if (film.getDescription().length() > 200) {
            log.info("Не пройдна валидация описания. Описание должно быть не больше 200 символов!");
            throw new ValidationException("Описание должно быть не больше 200 символов!");
        }
        if (film.getReleaseDate().isBefore(FILMSTARTDATE)) {
            log.info("Не пройдна валидация даты выпуска фильма. Так рано фильмы не снимали!");
            throw new ValidationException("Так рано фильмы не снимали!");
        }
        if (film.getDuration() < 0) {
            log.info("Не пройдна валидация продолжительности фильма. " + "Продолжительность фильма должна быть положительной!");
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен в список!");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильма с таким ID нет в списке");
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
            throw new FilmNotFoundException("Фильма с таким id нет!");
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
}
