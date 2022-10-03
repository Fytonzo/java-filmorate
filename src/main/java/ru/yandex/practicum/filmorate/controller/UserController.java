package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();

    private static int id = 0;

    private Integer generateId(){
        ++id;
        return id;
    }

    @GetMapping
    public ArrayList<User> getUsers() {
        log.info("Получен запрос на предоставление списка всех имеющихся пользователей");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("Получен запрос на добавление пользователя {}, валидирую...", user);
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

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Получен запрос на обновление пользователя с ID = {}", user.getId());
        if (!users.containsKey(user.getId())) {
            log.info("Пользователя с таким ID нет в списке");
            throw new ValidationException("Пользователя с таким ID нет в списке");
        } else {
            log.info("Пользователь с ID={}", user.getId());
            users.put(user.getId(), user);
        }
        return user;
    }
}
