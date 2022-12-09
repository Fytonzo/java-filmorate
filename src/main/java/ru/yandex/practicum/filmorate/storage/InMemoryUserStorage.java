package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage{
    private final Map<Integer, User> users = new HashMap<>();

    private static int id = 0;

    @Override
    public Integer generateId() {
        ++id;
        return id;
    }

    @Override
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        if ((user.getName()==null)||(user.getName().isBlank())) {
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
            throw new UserNotFoundException("Пользователя с таким ID нет в списке");
        } else {
            log.info("Пользователь с ID={}", user.getId());
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User getUser(Integer id){
        if (users.containsKey(id)){
            return users.get(id);
        }else{
            throw new UserNotFoundException("Пользователь с id "+ id + " не найден!");
        }
    }
}
