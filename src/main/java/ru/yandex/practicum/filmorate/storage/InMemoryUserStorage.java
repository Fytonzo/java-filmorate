package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component("userMemoryStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    private static int id = 0;

    private Integer generateId() {
        ++id;
        return id;
    }

    @Override
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        if ((user.getName() == null) || (user.getName().isBlank())) {
            log.info("Поле \"Имя\" пустое, ему будет присвоено значение поля \"Логин\"");
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Пользователя с таким ID нет в списке");
            throw new EntityNotFoundException("Пользователя с таким ID нет в списке");
        } else {
            log.info("Пользователь с ID={}", user.getId());
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User getUser(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new EntityNotFoundException("Пользователь с id " + id + " не найден!");
        }
    }

    @Override
    public List<User> getUserFriends(Integer id) {
        List<User> result = new ArrayList<>();
        Set<Integer> userFriends = getUser(id).getFriends();
        for (Integer friendId : userFriends) {
            result.add(getUser(friendId));
        }
        return result;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {

    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {

    }

    @Override
    public boolean checkUserInDb(Integer userId) {
        if (users.containsKey(userId)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<User> getCommonFriends(Integer userId1, Integer userId2) {
        List<User> commonFriends = new ArrayList<>();
        List<User> user1Friends = this.getUserFriends(userId1);
        List<User> user2Friends = this.getUserFriends(userId2);
        for (User user : user1Friends) {
            if (user2Friends.contains(user)) {
                commonFriends.add(user);
            }
        }
        return commonFriends;
    }
}
