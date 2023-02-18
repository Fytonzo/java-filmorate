package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    private FilmController controller;
    @Autowired
    private FilmService filmService;
    @Autowired
    @Qualifier("filmMemoryStorage")
    private FilmStorage filmStorage;
    @Autowired
    @Qualifier ("userMemoryStorage")
    private UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        this.userStorage = new InMemoryUserStorage();
        this.filmStorage = new InMemoryFilmStorage();
        this.filmService = new FilmService(filmStorage, userStorage);
        this.controller = new FilmController(filmService);
    }

    @Test
    @Description("Добавление и получение списка фильмов")
    @Tag("addFilm")
    @Tag("getFilms")
    public void getFilmsTest() {
        Film film1 = new Film("Name", "Description",
                LocalDate.of(2020, 1, 1), 120);
        Film film2 = new Film("NameName", "Description_Description",
                LocalDate.of(2020, 12, 15), 110);
        controller.addFilm(film1);
        controller.addFilm(film2);
        List<Film> savedFilms = controller.getFilms();
        assertNotNull(savedFilms, "Вместо списка фильмов вернулся null");
        assertEquals(2, savedFilms.size(), "Вернулся неверный размер списка фильмов");
        assertEquals(savedFilms.get(0), film1, "Вернулся не первый фильм");
        assertEquals(savedFilms.get(1), film2, "Вернулся не второй фильм");
    }

    @Test
    @Description("Фильм с описанием 200 символов")
    @Tag("addFilm")
    @Tag("getFilms")
    public void addFilmWith200SymbolsLongDescriptionTest() {
        Film film = new Film("Name",
                "iptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                        "DescriptionDescriptionDescriptionDescriptionDescriptionDescriDescriptionDescriptionD" +
                        "escriDescriptionDescriptionDescri",
                LocalDate.of(2020, 1, 1), 120);
        controller.addFilm(film);
        List<Film> savedFilms = controller.getFilms();
        assertNotNull(savedFilms, "Вместо списка фильмов вернулся null");
        assertEquals(1, savedFilms.size(), "Вернулся неверный размер списка фильмов");
        assertEquals(savedFilms.get(0), film, "Фильмы не совпали");
    }

    @Test
    @Description("Фильм с описанием 199 символов")
    @Tag("addFilm")
    @Tag("getFilms")
    public void addFilmWith199SymbolsLongDescriptionTest() {
        Film film = new Film("Name",
                "DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                        "DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                        "DescriptionDescrDescriptionDescrDescriptionDe",
                LocalDate.of(2020, 1, 1), 120);
        controller.addFilm(film);
        List<Film> savedFilms = controller.getFilms();
        assertNotNull(savedFilms, "Вместо списка фильмов вернулся null");
        assertEquals(1, savedFilms.size(), "Вернулся неверный размер списка фильмов");
        assertEquals(savedFilms.get(0), film, "Фильмы не совпали");
    }

    @Test
    @Description("Фильм с неверной датой выпуска")
    @Tag("addFilm")
    @Tag("Exception")
    public void addFilmWithEarlyReleaseDateTest() {
        Film film = new Film("Name", "Description",
                LocalDate.of(1800, 1, 1), 120);
        assertThrows(ValidationException.class, () -> controller.addFilm(film),
                "Не сработала валидация даты фильма");
    }

    @Test
    @Description("Фильм с нулевой продолжительностью")
    @Tag("addFilm")
    @Tag("Exception")
    public void addFilmWithZeroDurationTest() {
        Film film = new Film("Name", "Description",
                LocalDate.of(2000, 1, 1), 0);
        controller.addFilm(film);
        List<Film> savedFilms = controller.getFilms();
        assertNotNull(savedFilms, "Вместо списка фильмов вернулся null");
        assertEquals(savedFilms.get(0), film, "Фильмы не совпали");
    }
}