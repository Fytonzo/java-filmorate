package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dataextractor.UserResultSetExtractor;
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
    private static int id = 0;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer generateId() {
        ++id;
        return id;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserResultSetExtractor(jdbcTemplate, this));
    }

    @Override
    public User addUser(User user) {
        user.setId(generateId());
        if ((user.getName()==null)||(user.getName().isBlank())) {
            log.info("Поле \"Имя\" пустое, ему будет присвоено значение поля \"Логин\"");
            user.setName(user.getLogin());
        }
        jdbcTemplate.update("INSERT INTO users(id, email, login, name, birth_date) VALUES (?,?,?,?,?)",
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if(checkUserInDb(user.getId())){
            jdbcTemplate.update("UPDATE users SET email=?, login=?, name=?, birth_date=? WHERE id=?",
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
        }
        return user;

    }

    @Override
    public User getUser(Integer id) {
        if(checkUserInDb(id)){
            String sql = "SELECT * FROM users WHERE id="+id;
            return jdbcTemplate.query(sql, this::makeUser);
        }else{
            return null;
        }

    }

    public void addFriend(Integer userId, Integer friendId){
        if(checkUserInDb(userId)&&checkUserInDb(friendId)){
            List<Integer> friends = new ArrayList<>();
            SqlRowSet checkFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friendship " +
                    "WHERE user_id=?", friendId);
            while(checkFriends.next()){
                friends.add(checkFriends.getInt("friend_id"));
            }
            if (friends.contains(userId)){
                jdbcTemplate.update("INSERT INTO friendship VALUES (?,?,?)", userId, friendId,
                        jdbcTemplate.queryForObject("SELECT id FROM status WHERE description='confirmed'",
                                Integer.class));
                jdbcTemplate.update("UPDATE friendship SET status_id=? WHERE user_id=? AND friend_id=?",
                        jdbcTemplate.queryForObject("SELECT id FROM status WHERE description='confirmed'",
                                Integer.class), friendId, userId);
            }else{
                jdbcTemplate.update("INSERT INTO friendship VALUES (?,?,?)", userId, friendId,
                        jdbcTemplate.queryForObject("SELECT id FROM status WHERE description='not confirmed'",
                                Integer.class));
            }
        }

    }

    public void removeFriend(Integer userId, Integer friendId){
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id=? AND friend_id=?", userId, friendId);
        List<Integer> friends = new ArrayList<>();
        SqlRowSet checkFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friendship " +
                "WHERE user_id=?", userId);
        while(checkFriends.next()){
            friends.add(checkFriends.getInt("friend_id"));
        }
        if(friends.contains(userId)){
            jdbcTemplate.update("UPDATE friendship SET status_id=? WHERE user_id=? AND friend_id=?",
                    jdbcTemplate.queryForObject("SELECT id FROM status WHERE description='not confirmed'",
                    Integer.class), friendId, userId);
        }
    }

    public List<User> getUserFriends(Integer userId){
        if(checkUserInDb(userId)){
            List<User> userFriends = new ArrayList<>();
            SqlRowSet getFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friendship WHERE user_id=?",
                    userId);
            while (getFriends.next()){
                userFriends.add(getUser(getFriends.getInt("friend_id")));
            }
            return userFriends;
        }else{
            return null;
        }

    }

    private User makeUser(ResultSet rs) throws SQLException {
        if (rs.next()) {
            User user = new User(rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birth_date").toLocalDate());
            user.setId(rs.getInt("id"));
            SqlRowSet getUserFriends = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friendship " +
                    "WHERE user_id=?", user.getId());
            while (getUserFriends.next()) {
                user.addFriend(getUserFriends.getInt("friend_id"));
            }
            return user;
        } else {
            return null;
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


}
