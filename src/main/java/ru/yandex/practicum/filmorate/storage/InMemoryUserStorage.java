package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
        if (user.getEmail().isBlank() || (!user.getEmail().contains("@"))) {
            log.info("Не пройдна валидация email. Пустой или неверный адрес электронной почты!");
            throw new ValidationException("Пустой или неверный адрес электронной почты!");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("Не пройдна валидация имени. Пустое или содержит пробелы.");
            throw new ValidationException("Пустое или неверное имя");
        }
        if ((user.getName()==null)||(user.getName().isBlank())) {
            log.info("Поле \"Имя\" пустое, ему будет присвоено значение поля \"Логин\"");
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Дата рождения не может быть в будущем!");
            throw new ValidationException("Дата рождения не может быть в будущем!");
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
            throw new UserNotFoundException("Пользователь с id "+ id + "не найден!");
        }
    }
}
