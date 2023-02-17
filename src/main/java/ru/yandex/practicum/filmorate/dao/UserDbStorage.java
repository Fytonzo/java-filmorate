package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("userDBStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer generateId() {
        return null;
    }

    @Override
    public List<User> getUsers() throws SQLException {
        String sql = "SELECT u.*, f.friend_id FROM users AS u LEFT JOIN friendship AS f on u.id = f.user_id";
        List<User> result = new ArrayList<>();
        SqlRowSet users = jdbcTemplate.queryForRowSet(sql);
        while (users.next()){
            result.add(makeUser(users));
        }
        users.beforeFirst();
        for (User user: result) {
            while (users.next()){
                if (user.getId() == users.getInt("id")){
                    user.addFriend(users.getInt("friend_id"));
                }
            }
        }
        return result;
    }

    @Override
    public User addUser(User user) {
        jdbcTemplate.update("INSERT INTO users(email, login, name, birth_date) VALUES (?,?,?,?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        String sql = "SELECT id FROM users WHERE name='" + user.getName() + "' AND login='" + user.getLogin() +
                "' AND email='" + user.getEmail() + "'";
        SqlRowSet getId = jdbcTemplate.queryForRowSet(sql);
        int id = user.getId();
        while (getId.next()) {
            id = getId.getInt("id");
        }
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update("UPDATE users SET email=?, login=?, name=?, birth_date=? WHERE id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;

    }

    @Override
    public User getUser(Integer id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=" + id;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql);
        if (userRows.next()) {
            User user = makeUser(userRows);
            return user;
        } else {
            return null;
        }
    }

    public void addFriend(Integer userId, Integer friendId) {
        List<Integer> friends = new ArrayList<>();
        SqlRowSet checkFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friendship " +
                "WHERE user_id=?", friendId);
        while (checkFriends.next()) {
            friends.add(checkFriends.getInt("friend_id"));
        }
        if (friends.contains(userId)) {
            jdbcTemplate.update("INSERT INTO friendship VALUES (?,?,?)", userId, friendId, "confirmed");
            jdbcTemplate.update("UPDATE friendship SET status=? WHERE user_id=? AND friend_id=?",
                    "confirmed", friendId, userId);
        } else {
            jdbcTemplate.update("INSERT INTO friendship VALUES (?,?,?)", userId, friendId, "not confirmed");
        }

    }

    public void removeFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id=? AND friend_id=?", userId, friendId);
        List<Integer> friends = new ArrayList<>();
        SqlRowSet checkFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friendship " +
                "WHERE user_id=?", userId);
        while (checkFriends.next()) {
            friends.add(checkFriends.getInt("friend_id"));
        }
        if (friends.contains(userId)) {
            jdbcTemplate.update("UPDATE friendship SET status=? WHERE user_id=? AND friend_id=?",
                    "not confirmed", friendId, userId);
        }
    }

    public List<User> getUserFriends(Integer userId) throws SQLException {
        List<User> result = new ArrayList<>();
        SqlRowSet listOfFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friendship WHERE user_id=?",
                userId);
        while (listOfFriends.next()) {
            result.add(getUser(listOfFriends.getInt("friend_id")));
        }
        return result;

    }

    private User makeUser(SqlRowSet rs) throws SQLException {
        User user = new User(rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birth_date").toLocalDate());
        user.setId(rs.getInt("id"));
        return user;
    }

    public boolean checkUserInDb(Integer id) {
        String sql = "SELECT id FROM users";
        SqlRowSet getUsersFromDb = jdbcTemplate.queryForRowSet(sql);
        List<Integer> ids = new ArrayList<>();
        while (getUsersFromDb.next()) {
            ids.add(getUsersFromDb.getInt("id"));
        }
        if (ids.contains(id)) {
            return true;
        } else {
            throw new UserNotFoundException("Пользователя с таким id нет в базе!");
        }

    }

    public List<User> getCommonFriends(Integer user1Id, Integer user2Id) throws SQLException {
        String sql = "SELECT fr1.friend_id FROM friendship fr1 " +
                "INNER JOIN friendship fr2 ON fr1.friend_id = fr2.friend_id " +
                "WHERE fr1.user_id = ? AND fr2.user_id = ?";
        List<User> result = new ArrayList<>();
        SqlRowSet commonFriends = jdbcTemplate.queryForRowSet(sql, user1Id, user2Id);
        while (commonFriends.next()){
            result.add(getUser(commonFriends.getInt("friend_id")));
        }
        return result;
    }


}
