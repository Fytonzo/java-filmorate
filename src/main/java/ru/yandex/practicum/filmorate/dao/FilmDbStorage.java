package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dataextractor.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("filmDBStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate FILMSTARTDATE = LocalDate.of(1895, 12, 28);
    private static int id = 0;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer generateId() {
        ++id;
        return id;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, new FilmResultSetExtractor(jdbcTemplate, this));
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(FILMSTARTDATE)) {
            log.info("Не пройдена валидация даты выпуска фильма. Так рано фильмы не снимали!");
            throw new ValidationException("Так рано фильмы не снимали!");
        }else{
            film.setId(generateId());
            jdbcTemplate.update("INSERT INTO films(id, name, description, release_date, duration, rating_id) " +
                            "VALUES(?,?,?,?,?,?)",
                    film.getId(),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());
            if(film.getGenres().size()>0){
                for (Genre genre : film.getGenres()){
                    jdbcTemplate.update("INSERT INTO film_genre(film_id, genre_id) VALUES(?,?)",
                            film.getId(),
                            genre.getId());
                }
            }

        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if(checkFilmInDb(film.getId())){
            jdbcTemplate.update("UPDATE films SET name=?, description=?, release_date=?, duration=?, " +
                            "rating_id=? WHERE id=?",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            jdbcTemplate.update("DELETE FROM film_likes WHERE film_id=?", film.getId());
            jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", film.getId());
            for(Long userId : film.getLikes()){
                jdbcTemplate.update("INSERT INTO film_likes VALUES(?,?)", film.getId(), userId);
            }
            for (Genre genre : film.getGenres()){
                jdbcTemplate.update("INSERT INTO film_genre(film_id, genre_id) " +
                        "VALUES(?,?)", film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        String sql = "SELECT * FROM films WHERE id="+id;
        if (checkFilmInDb(id)){
            return jdbcTemplate.query(sql, this::makeFilm);
        }else{
            return null;
        }
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "SELECT f.*, count(fl.user_id) AS likes FROM films AS f LEFT JOIN film_likes AS fl " +
                "ON f.id=fl.film_id GROUP BY f.id ORDER BY likes DESC LIMIT "+count;
        return jdbcTemplate.query(sql, new FilmResultSetExtractor(jdbcTemplate, this));
    }

    public List<Genre> getAllGenres(){
        List<Genre> genres = new ArrayList<>();
        SqlRowSet allGenres = jdbcTemplate.queryForRowSet("SELECT * FROM genre");
        while(allGenres.next()){
            Genre genre = new Genre(allGenres.getInt("id"), allGenres.getString("genre_name"));
            genres.add(genre);
        }
        return genres;
    }

    public Genre getGenreById(int id){
        checkGenreInDb(id);
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genre WHERE id = ?", id);
        if(genreRows.next()){
            Genre genre = new Genre(genreRows.getInt("id"),
                    genreRows.getString("genre_name"));
            log.info("Жанр с id={}, это {}.", genre.getId(), genre.getName());
            return genre;
        }else{
            log.info("Жанра с таким id нет!");
            return null;
        }
    }

    public List<Mpa> getAllMpa(){
        List<Mpa> mpas = new ArrayList<>();
        SqlRowSet allMpas = jdbcTemplate.queryForRowSet("SELECT * FROM rating");
        while(allMpas.next()){
            Mpa mpa = new Mpa(allMpas.getInt("id"), allMpas.getString("name"));
            mpas.add(mpa);
        }
        return mpas;
    }

    public Mpa getMpaById(int id){
        checkMpaInDb(id);
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM rating WHERE id = ?", id);
        if(mpaRows.next()){
            Mpa mpa = new Mpa(mpaRows.getInt("id"), mpaRows.getString("name"));
            log.info("Рейтинг с id={}, это {}.", mpa.getId(), mpa.getName());
            return mpa;
        }else{
            log.info("Рейтинга с таким id нет!");
            return null;
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        if (rs.next()){
            Film film = new Film(rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"));
            film.setId(rs.getInt("id"));
            film.setMpa(getMpaById(rs.getInt("rating_id")));
            SqlRowSet getFilmGenres = jdbcTemplate.queryForRowSet("SELECT genre_id FROM film_genre WHERE film_id=?", film.getId());
            while(getFilmGenres.next()){
                Genre genre = getGenreById(getFilmGenres.getInt("genre_id"));
                film.addGenre(genre);
            }
            SqlRowSet getFilmLikes = jdbcTemplate.queryForRowSet("SELECT user_id FROM film_likes WHERE film_id = ?",
                    film.getId());
            while(getFilmLikes.next()){
                film.addLIke(getFilmLikes.getInt("user_id"));
            }
            return film;
        }else{
            return null;
        }

    }

    private boolean checkFilmInDb(Integer id){
        String sql = "SELECT id FROM films";
        SqlRowSet getFilmFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getFilmFromDb.next()){
            ids.add(getFilmFromDb.getInt("id"));
        }
        if(ids.contains(id)){
            return true;
        }else{
            throw new FilmNotFoundException("Фильма с таким id нет в базе!");
        }
    }

    private boolean checkUserInDb(Integer id){
        String sql = "SELECT id FROM users";
        SqlRowSet getUsersFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getUsersFromDb.next()){
            ids.add(getUsersFromDb.getInt("id"));
        }
        if(ids.contains(id)){
            return true;
        }else{
            throw new UserNotFoundException("Пользователя с таким id нет в базе!");
        }

    }

    private boolean checkMpaInDb(Integer id){
        String sql = "SELECT id FROM rating";
        SqlRowSet getMpaFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getMpaFromDb.next()){
            ids.add(getMpaFromDb.getInt("id"));
        }
        if(ids.contains(id)){
            return true;
        }else{
            throw new MpaNotFoundException("Рейтинга с таким id нет в базе!");
        }
    }

    private boolean checkGenreInDb(Integer id){
        String sql = "SELECT id FROM genre";
        SqlRowSet getGenreFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getGenreFromDb.next()){
            ids.add(getGenreFromDb.getInt("id"));
        }
        if(ids.contains(id)){
            return true;
        }else{
            throw new GenreNotFoundException("Жанра с таким id нет в базе!");
        }
    }

    public void likeAdd(Integer filmId, Integer userId){
        checkFilmInDb(filmId);
        checkUserInDb(userId);
        jdbcTemplate.update("INSERT INTO film_likes VALUES (?,?)", filmId, userId);
    }

    public void likeRemove(Integer filmId, Integer userId){
        checkFilmInDb(filmId);
        checkUserInDb(userId);
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id=? AND user_id=?", filmId, userId);
    }


}
