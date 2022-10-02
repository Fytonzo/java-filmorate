package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController controller;

    public FilmControllerTest() {
    }

    @BeforeEach
    public void setUp() {
        this.controller = new FilmController();
    }

    @Test
    @Description("Добавление и получение списка фильмов")
    @Tag("addFilm")
    @Tag("getFilms")
    public void getFilmsTest() {
        Film film1 = new Film(1, "Name", "Description",
                LocalDate.of(2020, 1, 1), 120);
        Film film2 = new Film(2, "NameName", "Description_Description",
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
    @Description("Фильм с пустым названием")
    @Tag("addFilm")
    @Tag("getFilms")
    public void addFilmWithEmptyNameTest() {
        Film film = new Film(1, "", "Description",
                LocalDate.of(2020, 1, 1), 120);
        assertThrows(ValidationException.class, () -> controller.addFilm(film),
                "Не сработала валидация названия фильма");
    }

    @Test
    @Description("Фильм с ооочень длинным описание")
    @Tag("addFilm")
    @Tag("getFilms")
    public void addFilmWithVeryLongDescriptionTest() {
        Film film = new Film(1, "Name",
                "DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                        "DescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                        "DescriptionDescriptionDescription",
                LocalDate.of(2020, 1, 1), 120);
        assertThrows(ValidationException.class, () -> controller.addFilm(film),
                "Не сработала валидация длины описания фильма");
    }

    @Test
    @Description("Фильм с описанием 200 символов")
    @Tag("addFilm")
    @Tag("getFilms")
    public void addFilmWith200SymbolsLongDescriptionTest() {
        Film film = new Film(1, "Name",
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
        Film film = new Film(1, "Name",
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
    @Description("Фильм с описанием 201 символов")
    @Tag("addFilm")
    @Tag("Exception")
    public void addFilmWith201SymbolsLongDescriptionTest() {
        Film film = new Film(1, "Name",
                "iptionDescriptionDescriptionDescriptionDescriptionDescriptionDescriptionDescription" +
                        "DescriptionDescriptionDescriptionDescriptionDescriptionDescriDescriptionDescriptionD" +
                        "escriDescriptionDescriptionDescrip",
                LocalDate.of(2020, 1, 1), 120);
        assertThrows(ValidationException.class, () -> controller.addFilm(film),
                "Не сработала валидация длины описания фильма");
    }

    @Test
    @Description("Фильм с неверной датой выпуска")
    @Tag("addFilm")
    @Tag("Exception")
    public void addFilmWithEarlyReleaseDateTest() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(1800, 1, 1), 120);
        assertThrows(ValidationException.class, () -> controller.addFilm(film),
                "Не сработала валидация даты фильма");
    }

    @Test
    @Description("Фильм с отрицательной продолжительностью")
    @Tag("addFilm")
    @Tag("Exception")
    public void addFilmWithNegativeDurationTest() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(2000, 1, 1), -120);
        assertThrows(ValidationException.class, () -> controller.addFilm(film),
                "Не сработала валидация продолжительности фильма");
    }

    @Test
    @Description("Фильм с нулевой продолжительностью")
    @Tag("addFilm")
    @Tag("Exception")
    public void addFilmWithZeroDurationTest() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(2000, 1, 1), 0);
        controller.addFilm(film);
        List<Film> savedFilms = controller.getFilms();
        assertNotNull(savedFilms, "Вместо списка фильмов вернулся null");
        assertEquals(savedFilms.get(0), film, "Фильмы не совпали");
    }
}