package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDBStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public void addFriend(Integer userId1, Integer userId2) {
        userStorage.checkUserInDb(userId1);
        userStorage.checkUserInDb(userId2);
        userStorage.addFriend(userId1, userId2);
    }

    public void removeFriend(Integer userId1, Integer userId2) {
        userStorage.removeFriend(userId1, userId2);
    }

    public List<User> getCommonFriend(Integer userId1, Integer userId2) throws SQLException {
        return userStorage.getCommonFriends(userId1, userId2);
    }

    public User getUser(Integer id) throws SQLException {
        userStorage.checkUserInDb(id);
        return userStorage.getUser(id);
    }

    public List<User> getUsers() throws SQLException {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        if ((user.getName() == null) || (user.getName().isBlank())) {
            log.info("Поле \"Имя\" пустое, ему будет присвоено значение поля \"Логин\"");
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        userStorage.checkUserInDb(user.getId());
        return userStorage.updateUser(user);
    }

    public List<User> getUserFriends(Integer id) throws SQLException {
        userStorage.checkUserInDb(id);
        return userStorage.getUserFriends(id);
    }
}
