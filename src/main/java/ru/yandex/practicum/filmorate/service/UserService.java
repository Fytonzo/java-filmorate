package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.SQLException;
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

    public List<User> getCommonFriend(Integer userId1, Integer userId2) {
        try {
            return userStorage.getCommonFriends(userId1, userId2);
        } catch (SQLException e) {
            log.error("Ошибка в сервисном методе получения общих друзей: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public User getUser(Integer id) {
        userStorage.checkUserInDb(id);
        try {
            return userStorage.getUser(id);
        } catch (SQLException e) {
            log.error("Ошибка в сервисном методе получения пользователя по id: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<User> getUsers() {
        try {
            return userStorage.getUsers();
        } catch (SQLException e) {
            log.error("Ошибка в сервисном методе получения списка всех пользователей: " + e.getMessage());
            throw new RuntimeException(e);
        }
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

    public List<User> getUserFriends(Integer id) {
        userStorage.checkUserInDb(id);
        try {
            return userStorage.getUserFriends(id);
        } catch (SQLException e) {
            log.error("Ошибка в сервисном методе получения друзей пользователя по id: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
