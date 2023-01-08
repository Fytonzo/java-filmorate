package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@PropertySource("classpath:application_test.properties")
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    private UserDbStorage userDbStorage;

    @BeforeEach
    public void setUp(){
        this.userDbStorage = new UserDbStorage(jdbcTemplate);
        this.filmDbStorage = new FilmDbStorage(jdbcTemplate);
    }
    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Добавление/получение фильмов")
    @Tag("insert")
    @Tag("select")
    public void getFilms() {
        Film film1 = new Film("firstFilm", "Description",
                LocalDate.of(2020, 1, 1), 120);
        Film film2 = new Film("secondFilm", "Description_Description",
                LocalDate.of(2020, 12, 15), 110);
        film1.addGenre(filmDbStorage.getGenreById(1));
        film2.addGenre(filmDbStorage.getGenreById(2));
        film1.setMpa(filmDbStorage.getMpaById(2));
        film2.setMpa(filmDbStorage.getMpaById(3));
        filmDbStorage.addFilm(film1);
        filmDbStorage.addFilm(film2);
        List<Film> films = filmDbStorage.getFilms();
        assertNotNull(films, "Вместо списка фильмов вернулся null");
        assertEquals(2, films.size(), "Вернулся неверный размер списка фильмов");
        assertEquals(films.get(0).getMpa(), film1.getMpa(), "Вернулся не первый фильм");
        assertEquals(films.get(1).getDuration(), film2.getDuration(), "Вернулся не второй фильм");
    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Добавление/получение фильмов")
    @Tag("insert")
    @Tag("select")
    public void updateFilmTest() {
        Film film1 = new Film("firstFilm", "Description",
                LocalDate.of(2020, 1, 1), 120);
        film1.addGenre(filmDbStorage.getGenreById(1));
        film1.setMpa(filmDbStorage.getMpaById(2));
        filmDbStorage.addFilm(film1);
        film1.setDuration(150);
        filmDbStorage.updateFilm(film1);
        Film savedFilm = filmDbStorage.getFilm(film1.getId());
        assertNotNull(savedFilm, "Вместо обновленного фильма вернулся null");
        assertEquals(film1.getDuration(), savedFilm.getDuration(), "Фильм не обновился в базе");

    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Добавление/получение фильма")
    @Tag("insert")
    @Tag("select")
    public void getFilmTest() {
        Film film1 = new Film("firstFilm", "Description",
                LocalDate.of(2020, 1, 1), 120);
        film1.addGenre(filmDbStorage.getGenreById(1));
        film1.setMpa(filmDbStorage.getMpaById(2));
        filmDbStorage.addFilm(film1);
        Film newFilm = filmDbStorage.getFilm(film1.getId());
        assertNotNull(newFilm, "Вместо фильма вернулся null");
        assertEquals(newFilm.getName(), film1.getName(), "Имена фильмов не совпадают");
    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Запрос самых популярных фильмов")
    @Tag("insert")
    @Tag("select")
    @Tag("likeadd")
    @Tag("likeremove")
    public void getPopularFilmsTest() {
        Film film1 = new Film("firstFilm", "Description",
                LocalDate.of(2020, 1, 1), 120);
        Film film2 = new Film("secondFilm", "Description_Description",
                LocalDate.of(2020, 12, 15), 110);
        User user1 = new User("email@email.com", "login1", "name1",
                LocalDate.of(1980, 1, 1));
        User user2 = new User("emailemail@email1.com", "login2", "name2",
                LocalDate.of(1985, 1, 2));
        User user3 = new User("sdfhemailemail@email1.com", "sdflogin2", "afhname2",
                LocalDate.of(1989, 4, 5));
        film1.addGenre(filmDbStorage.getGenreById(1));
        film1.addGenre(filmDbStorage.getGenreById(5));
        film2.addGenre(filmDbStorage.getGenreById(2));
        film1.setMpa(filmDbStorage.getMpaById(2));
        film2.setMpa(filmDbStorage.getMpaById(3));
        filmDbStorage.addFilm(film1);
        filmDbStorage.addFilm(film2);
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);
        filmDbStorage.likeAdd(film1.getId(), user1.getId());
        filmDbStorage.likeAdd(film1.getId(), user3.getId());
        filmDbStorage.likeAdd(film1.getId(), user2.getId());
        List<Film> popularFilms = filmDbStorage.getPopularFilms(2);
        assertNotNull(popularFilms, "Вместо списка популярных фильмов вернулся null");
        assertEquals(2, popularFilms.size(), "Неверный размер списка популярных фильмов");
        assertEquals(3, popularFilms.get(0).getLikes().size(),
                "Неверное количество лайков у самого популярного фильма");
        filmDbStorage.likeRemove(film1.getId(), user3.getId());
        popularFilms = filmDbStorage.getPopularFilms(2);
        assertNotNull(popularFilms, "Вместо списка популярных фильмов вернулся null");
        assertEquals(2, popularFilms.size(), "Неверный размер списка популярных фильмов");
        assertEquals(2, popularFilms.get(0).getLikes().size(),
                "Количество лайков должно было уменьшится на 1");
    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Получение списка жанров")
    @Tag("select")
    public void getAllGenresTest() {
        List<Genre> genres = filmDbStorage.getAllGenres();
        assertNotNull(genres, "Вместо списка жанров вернулся null");
        assertEquals(6, genres.size(), "Неверный размер списка жанров");
    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Получение жанра по ID")
    @Tag("select")
    public void getGenreByIdTest() {
        Genre genre = filmDbStorage.getGenreById(1);
        assertNotNull(genre, "Вместо жанра вернулся null");
        assertEquals(genre.getName(), "Комедия", "Вернулся неверный жанр");
    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Получение списка возрастных рейтингов")
    @Tag("select")
    public void getAllMpaTest() {
        List<Mpa> mpas = filmDbStorage.getAllMpa();
        assertNotNull(mpas, "Вместо списка рейтингов вернулся null");
        assertEquals(5, mpas.size(), "Неверный размер списка возрастных рейтингов");
    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Получение возрастного рейтинга по ID")
    @Tag("select")
    public void getMpaByIdTest() {
        Mpa mpa = filmDbStorage.getMpaById(2);
        assertNotNull(mpa, "вместо возрастного рейтинга вернулся null");
        assertEquals(mpa.getName(), "PG", "Вернулся неверный возрастной рейтинг");

    }
}