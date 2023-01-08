package ru.yandex.practicum.filmorate.dataextractor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserResultSetExtractor implements ResultSetExtractor<List<User>> {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public UserResultSetExtractor(JdbcTemplate jdbcTemplate, @Qualifier("userDBStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<User> users = new ArrayList<>();
        while(rs.next()){
            User user = new User(rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birth_date").toLocalDate());
            user.setId(rs.getInt("id"));
            users.add(user);
            SqlRowSet getUserFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friendship " +
                    "WHERE user_id=?", user.getId());
            while(getUserFriends.next()){
                user.addFriend(getUserFriends.getInt("friend_id"));
            }
        }
        return users;
    }
}
