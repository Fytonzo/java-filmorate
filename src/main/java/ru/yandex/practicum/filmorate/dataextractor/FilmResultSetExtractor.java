package ru.yandex.practicum.filmorate.dataextractor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    public FilmResultSetExtractor(JdbcTemplate jdbcTemplate, @Qualifier("filmDBStorage") FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException{
        List<Film> films = new ArrayList<>();
        while(rs.next()){
            Film film = new Film(rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"));
            film.setId(rs.getInt("id"));
            film.setMpa(filmStorage.getMpaById(rs.getInt("rating_id")));
            SqlRowSet getFilmGenres = jdbcTemplate.queryForRowSet("SELECT genre_id FROM film_genre WHERE film_id=?", film.getId());
            while(getFilmGenres.next()){
                Genre genre = filmStorage.getGenreById(getFilmGenres.getInt("genre_id"));
                film.addGenre(genre);
            }
            SqlRowSet getFilmLikes = jdbcTemplate.queryForRowSet("SELECT user_id FROM film_likes WHERE film_id = ?",
                    film.getId());
            while(getFilmLikes.next()){
                film.addLIke(getFilmLikes.getInt("user_id"));
            }
            films.add(film);
        }
        return films;
    }

}
