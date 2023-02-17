package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserStorage {

    Integer generateId();

    List<User> getUsers() throws SQLException;

    User addUser(User user);

    User updateUser(User user);

    User getUser(Integer id) throws SQLException;

    List<User> getUserFriends(Integer id) throws SQLException;

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    boolean checkUserInDb(Integer userId);

    List<User> getCommonFriends(Integer userId1, Integer userId2) throws SQLException;
}
