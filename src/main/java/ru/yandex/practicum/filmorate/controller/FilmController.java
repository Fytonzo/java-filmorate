package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService){
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос на предоставление списка всех имеющихся фильмов");
        return filmService.getFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма {}, валидирую...", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма с ID = {}", film.getId());
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Integer id){
        log.info("Получен запрос на фильм с ID = {}", id);
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeAdd(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId){
        filmService.likeAdd(id, userId);
        log.info("Пользователь {} поставил лайк фильму с ID = {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void likeRemove(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId){
        filmService.likeRemove(id, userId);
        log.info("Пользователь {} убрал лайк с фильма с ID = {}", userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getFilmsByPopularity(@RequestParam(required = false) Integer count){
        if(count != null){
            log.info("Получен запрос на предоставление самых популярных фильмов в количестве {}", count);
            return filmService.getPopularFilms(count);
        }else{
            log.info("Получен запрос на предоставление 10 самых популярных фильмов");
            return filmService.getPopularFilms(10);
        }
    }

}
