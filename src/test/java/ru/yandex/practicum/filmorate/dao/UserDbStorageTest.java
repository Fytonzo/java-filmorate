package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@PropertySource("classpath:application_test.properties")
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userDbStorage;

    @BeforeEach
    public void setUp(){
        this.userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Запрос всех пользователей")
    @Tag("insert")
    @Tag("select")
    @Tag("adduser")
    public void getUsers() throws SQLException {
        User user1 = new User("email@email.com", "login1", "name1",
                LocalDate.of(1980, 1, 1));
        /*User user2 = new User("emailemail@email1.com", "login2", "name2",
                LocalDate.of(1985, 1, 1));*/
        userDbStorage.addUser(user1);
        /*userDbStorage.addUser(user2);*/
        List<User> users = userDbStorage.getUsers();
        assertNotNull(users, "Вместо списка пользователей вернулся null");
    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Обновление пользователя")
    @Tag("insert")
    @Tag("select")
    @Tag("adduser")
    @Tag("updateuser")
    public void updateUser() throws SQLException {
        User user1 = new User("email@email.com", "login1", "name1",
                LocalDate.of(1980, 1, 1));
        userDbStorage.addUser(user1);
        user1.setId(jdbcTemplate.queryForObject("SELECT id FROM users WHERE name='"+user1.getName()+"'",
                Integer.class));
        user1.setLogin("loginbobin");
        userDbStorage.updateUser(user1);
        User savedUser = userDbStorage.getUser(user1.getId());
        assertNotNull(savedUser, "Вместо пользователя вернулся null");
        assertEquals(user1.getLogin(), savedUser.getLogin(), "Пользователь не обновился");
    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Добавление пользователя")
    @Tag("insert")
    @Tag("select")
    @Tag("adduser")
    public void getUser() throws SQLException {
        User user1 = new User("email@email.com", "login1", "name1",
                LocalDate.of(1980, 1, 1));
        userDbStorage.addUser(user1);
        user1.setId(jdbcTemplate.queryForObject("SELECT id FROM users WHERE name='"+user1.getName()+"'",
                Integer.class));
        User savedUser = userDbStorage.getUser(user1.getId());
        assertNotNull(savedUser, "вместо пользователя вернулся null");
        assertEquals(user1.getName(), savedUser.getName(), "Пользователи не совпали");

    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Удалить друга")
    @Tag("insert")
    @Tag("select")
    @Tag("adduser")
    @Tag("addfriend")
    @Tag("removefriend")
    public void removeFriend() throws SQLException {
        User user1 = new User("email@email.com", "login1", "name1",
                LocalDate.of(1980, 1, 1));
        User user2 = new User("emailemail@email1.com", "login2", "name2",
                LocalDate.of(1985, 1, 1));
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        user1.setId(jdbcTemplate.queryForObject("SELECT id FROM users WHERE name='"+user1.getName()+"'",
                Integer.class));
        user2.setId(jdbcTemplate.queryForObject("SELECT id FROM users WHERE name='"+user2.getName()+"'",
                Integer.class));
        userDbStorage.addFriend(user1.getId(), user2.getId());
        List<User> userFriends = userDbStorage.getUserFriends(user1.getId());
        assertNotNull(userFriends, "вместо списка друзей вернулся null");
        assertEquals(1, userFriends.size(), "Неверный размер списка друзей");
        userDbStorage.removeFriend(user1.getId(), user2.getId());
        userFriends = userDbStorage.getUserFriends(user1.getId());
        assertNotNull(userFriends, "вместо списка друзей вернулся null");
        assertEquals(0, userFriends.size(), "Список друзей должен быть пустым");

    }

    @Test
    @Sql({"classpath:db/schema.sql", "classpath:db/data.sql"})
    @Description("Добавить друга")
    @Tag("insert")
    @Tag("select")
    @Tag("adduser")
    @Tag("addfriend")
    public void getUserFriends() throws SQLException {
        User user1 = new User("email@email.com", "login1", "name1",
                LocalDate.of(1980, 1, 1));
        User user2 = new User("emailemail@email1.com", "login2", "name2",
                LocalDate.of(1985, 1, 1));
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        user1.setId(jdbcTemplate.queryForObject("SELECT id FROM users WHERE name='"+user1.getName()+"'",
                Integer.class));
        user2.setId(jdbcTemplate.queryForObject("SELECT id FROM users WHERE name='"+user2.getName()+"'",
                Integer.class));
        userDbStorage.addFriend(user1.getId(), user2.getId());
        List<User> userFriends = userDbStorage.getUserFriends(user1.getId());
        assertNotNull(userFriends, "вместо списка друзей вернулся null");
        assertEquals(1, userFriends.size(), "Неверный размер списка друзей");
    }
}