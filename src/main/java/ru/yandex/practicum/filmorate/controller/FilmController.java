package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();
    private static final LocalDate FILMSTARTDATE = LocalDate.of(1895, 12, 28);

    private static int id = 0;

    private Integer generateId(){
        ++id;
        return id;
    }

    @GetMapping
    public ArrayList<Film> getFilms() {
        log.info("Получен запрос на предоставление списка всех имеющихся фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Получен запрос на добавление фильма {}, валидирую...", film);
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
            log.info("Не пройдна валидация продолжительности фильма. " +
                    "Продолжительность фильма должна быть положительной!");
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен в список!");
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Получен запрос на обновление фильма с ID = {}", film.getId());
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильма с таким ID нет в списке");
        } else {
            log.info("Фильм с ID {}, успешно обновлён!", film.getId());
            films.put(film.getId(), film);
        }
        return film;
    }


}
