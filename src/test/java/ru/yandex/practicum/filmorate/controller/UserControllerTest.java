package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    public void setUp() {
        UserStorage inMemoryUserStorage = new InMemoryUserStorage();
        UserService userService = new UserService(inMemoryUserStorage);
        this.controller = new UserController(userService);
    }

    @Test
    @Description("Добавление и получение списка пользователей")
    @Tag("addUser")
    @Tag("getUsers")
    public void getUsersTest() {
        User user1 = new User("email@email.com", "login1", "name1",
                LocalDate.of(1980, 1, 1));
        User user2 = new User("emailemail@email1.com", "login2", "name2",
                LocalDate.of(1985, 1, 1));
        controller.addUser(user1);
        controller.addUser(user2);
        List<User> savedUsers = controller.getUsers();
        assertNotNull(savedUsers, "Вместо списка пользователей вернулся null");
        assertEquals(2, savedUsers.size(), "Вернулся неверный размер списка пользователей");
        assertEquals(savedUsers.get(0), user1, "Вернулся не первый пользователь");
        assertEquals(savedUsers.get(1), user2, "Вернулся не второй пользователь");
    }

    @Test
    @Description("Пользователь с пустым email")
    public void addUserWithEmptyEmailTest() {
        User user = new User("", "login1", "name1",
                LocalDate.of(1980, 1, 1));
        assertThrows(ValidationException.class, () -> controller.addUser(user),
                "Не сработала валидация email");
    }

    @Test
    @Description("Пользователь с неверным email, без @")
    public void addUserWithWrongEmailTest() {
        User user = new User("email.com", "login1", "name1",
                LocalDate.of(1980, 1, 1));
        assertThrows(ValidationException.class, () -> controller.addUser(user),
                "Не сработала валидация email");
    }

    @Test
    @Description("Пользователь с пустым логином")
    public void addUserWithEmptyLoginTest() {
        User user = new User("email@email.com", "", "name1",
                LocalDate.of(1980, 1, 1));
        assertThrows(ValidationException.class, () -> controller.addUser(user),
                "Не сработала валидация логина");
    }

    @Test
    @Description("Пользователь с логином содержащим пробел")
    public void addUserWithWrongLoginTest() {
        User user = new User("email@email.com", "log in1", "name1",
                LocalDate.of(1980, 1, 1));
        assertThrows(ValidationException.class, () -> controller.addUser(user),
                "Не сработала валидация логина");
    }

    @Test
    @Description("Пользователь с пустым именем")
    public void addUserWithEmptyNameTest() {
        User user = new User("email@email.com", "login1", "",
                LocalDate.of(1980, 1, 1));
        controller.addUser(user);
        List<User> savedUsers = controller.getUsers();
        assertNotNull(savedUsers, "Вместо списка пользователей вернулся null");
        assertEquals(savedUsers.get(0).getName(), savedUsers.get(0).getLogin(),
                "Не сработал механизм подстановки логина вместо пустого имени");
    }

    @Test
    @Description("Пользователь с датой рождения в будущем")
    public void addUserWithWrongBirthDateTest() {
        User user = new User("email@email.com", "login1", "name1",
                LocalDate.of(2280, 1, 1));
        assertThrows(ValidationException.class, () -> controller.addUser(user),
                "Не сработала валидация даты рождения");
    }

    @Test
    @Description("Пользователь с текущей датой рождения")
    public void addUserWithNowBirthDateTest() {
        User user = new User("email@email.com", "login1", "name1",
                LocalDate.now());
        controller.addUser(user);
        List<User> savedUsers = controller.getUsers();
        assertNotNull(savedUsers, "Вместо списка пользователей вернулся null");
        assertEquals(savedUsers.get(0), user, "Вернулся не тот пользователь");
    }
}