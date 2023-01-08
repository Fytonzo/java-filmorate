package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    Integer generateId();

    List<User> getUsers();

    User addUser(User user);

    User updateUser(User user);

    User getUser(Integer id);

    List<User> getUserFriends(Integer id);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);
}
