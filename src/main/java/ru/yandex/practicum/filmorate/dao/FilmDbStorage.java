package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("filmDBStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GENRES = "SELECT fg.film_id, g.id, g.genre_name FROM film_genre AS fg " +
            "LEFT JOIN genre AS g ON fg.genre_id=g.id";
    private static final String SQL_LIKES = "SELECT * FROM film_likes";

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT f.*, r.rating_name FROM films AS f LEFT JOIN rating AS r ON f.rating_id=r.id";
        List<Film> result = new ArrayList<>();
        SqlRowSet films = jdbcTemplate.queryForRowSet(sql);
        while (films.next()) {
            result.add(makeFilm(films));
        }
        return addGenresAndLikes(result);
    }

    @Override
    public Film addFilm(Film film) {
        jdbcTemplate.update("INSERT INTO films(name, description, release_date, duration, rating_id) " +
                        "VALUES(?,?,?,?,?)",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        String sql = "SELECT id FROM films WHERE name='" + film.getName() + "' AND description='" +
                film.getDescription() + "' AND duration='" + film.getDuration() + "'";
        SqlRowSet getId = jdbcTemplate.queryForRowSet(sql);
        int id = film.getId();
        while (getId.next()) {
            id = getId.getInt("id");
        }
        film.setId(id);
        List<Genre> genres = new ArrayList<>(List.of(film.getGenres().toArray(new Genre[0])));
        jdbcTemplate.batchUpdate("INSERT INTO film_genre(film_id, genre_id) VALUES (?,?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {

                        ps.setInt(1, film.getId());
                        ps.setInt(2, genres.get(i).getId());

                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (checkFilmInDb(film.getId())) {
            jdbcTemplate.update("UPDATE films SET name=?, description=?, release_date=?, duration=?, " +
                            "rating_id=? WHERE id=?",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", film.getId());
            List<Genre> genres = new ArrayList<>(List.of(film.getGenres().toArray(new Genre[0])));
            jdbcTemplate.batchUpdate("INSERT INTO film_genre(film_id, genre_id) VALUES (?,?)",
                    new BatchPreparedStatementSetter() {

                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {

                            ps.setInt(1, film.getId());
                            ps.setInt(2, genres.get(i).getId());

                        }

                        @Override
                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
        }
        return film;
    }

    @Override
    public Film getFilm(Integer id) throws SQLException {
        String sql = "SELECT f.*, r.rating_name FROM films AS f LEFT JOIN rating AS r ON f.rating_id=r.id " +
                "WHERE f.id=?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (filmRows.next()) {
            Film film = makeFilm(filmRows);
            SqlRowSet getFilmGenres = jdbcTemplate.queryForRowSet("SELECT fg.genre_id, g.genre_name " +
                    "FROM film_genre AS fg LEFT JOIN genre AS g ON fg.genre_id=g.id WHERE fg.film_id=" + id);
            while (getFilmGenres.next()) {
                film.addGenre(new Genre(getFilmGenres.getInt("genre_id"),
                        getFilmGenres.getString("genre_name")));
            }
            SqlRowSet getFilmLikes = jdbcTemplate.queryForRowSet("SELECT user_id FROM film_likes " +
                    "WHERE film_id=" + id);
            while (getFilmLikes.next()) {
                film.addLIke(getFilmLikes.getInt("user_id"));
            }
            return film;
        } else {
            return null;
        }

    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "SELECT f.*, count(fl.user_id) AS likes, r.rating_name FROM films AS f LEFT JOIN film_likes " +
                "AS fl ON f.id=fl.film_id LEFT JOIN rating AS r ON f.rating_id=r.id GROUP BY f.id " +
                "ORDER BY likes DESC LIMIT " + count;
        List<Film> result = new ArrayList<>();
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sql);
        while (srs.next()) {
            result.add(makeFilm(srs));
        }
        return addGenresAndLikes(result);
    }

    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet allGenres = jdbcTemplate.queryForRowSet("SELECT * FROM genre");
        while (allGenres.next()) {
            Genre genre = new Genre(allGenres.getInt("id"), allGenres.getString("genre_name"));
            genres.add(genre);
        }
        return genres;
    }

    public Genre getGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genre WHERE id = ?", id);
        if (genreRows.next()) {
            Genre genre = new Genre(genreRows.getInt("id"),
                    genreRows.getString("genre_name"));
            log.info("Жанр с id={}, это {}.", genre.getId(), genre.getName());
            return genre;
        } else {
            log.info("Жанра с таким id нет!");
            return null;
        }
    }

    public List<Mpa> getAllMpa() {
        List<Mpa> mpas = new ArrayList<>();
        SqlRowSet allMpas = jdbcTemplate.queryForRowSet("SELECT * FROM rating");
        while (allMpas.next()) {
            Mpa mpa = new Mpa(allMpas.getInt("id"), allMpas.getString("rating_name"));
            mpas.add(mpa);
        }
        return mpas;
    }

    public Mpa getMpaById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM rating WHERE id = ?", id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(mpaRows.getInt("id"), mpaRows.getString("rating_name"));
            log.info("Рейтинг с id={}, это {}.", mpa.getId(), mpa.getName());
            return mpa;
        } else {
            log.info("Рейтинга с таким id нет!");
            return null;
        }
    }

    private Film makeFilm(SqlRowSet rs) {
        Film film = new Film(rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"));
        film.setId(rs.getInt("id"));
        Mpa mpa = new Mpa(rs.getInt("rating_id"), rs.getString("rating_name"));
        film.setMpa(mpa);
        return film;
    }

    public boolean checkFilmInDb(Integer id) {
        String sql = "SELECT id FROM films";
        SqlRowSet getFilmFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getFilmFromDb.next()) {
            ids.add(getFilmFromDb.getInt("id"));
        }
        if (ids.contains(id)) {
            return true;
        } else {
            throw new EntityNotFoundException("Фильма с таким id нет в базе!");
        }
    }

    public boolean checkMpaInDb(Integer id) {
        String sql = "SELECT id FROM rating";
        SqlRowSet getMpaFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getMpaFromDb.next()) {
            ids.add(getMpaFromDb.getInt("id"));
        }
        if (ids.contains(id)) {
            return true;
        } else {
            throw new EntityNotFoundException("Рейтинга с таким id нет в базе!");
        }
    }

    public boolean checkGenreInDb(Integer id) {
        String sql = "SELECT id FROM genre";
        SqlRowSet getGenreFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getGenreFromDb.next()) {
            ids.add(getGenreFromDb.getInt("id"));
        }
        if (ids.contains(id)) {
            return true;
        } else {
            throw new EntityNotFoundException("Жанра с таким id нет в базе!");
        }
    }

    public void likeAdd(Integer filmId, Integer userId) {
        jdbcTemplate.update("INSERT INTO film_likes VALUES (?,?)", filmId, userId);
    }

    public void likeRemove(Integer filmId, Integer userId) {
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id=? AND user_id=?", filmId, userId);
    }

    private List<Film> addGenresAndLikes(List<Film> films) {
        SqlRowSet genres = jdbcTemplate.queryForRowSet(SQL_GENRES);
        SqlRowSet likes = jdbcTemplate.queryForRowSet(SQL_LIKES);
        for (Film film : films) {
            while (genres.next()) {
                if (film.getId() == genres.getInt("film_id")) {
                    Genre genre = new Genre(genres.getInt("id"), genres.getString("genre_name"));
                    film.addGenre(genre);
                }
            }
            genres.beforeFirst();
            while (likes.next()) {
                if (film.getId() == likes.getInt("film_id")) {
                    film.addLIke(likes.getInt("user_id"));
                }
            }
            likes.beforeFirst();
        }
        return films;
    }


}
