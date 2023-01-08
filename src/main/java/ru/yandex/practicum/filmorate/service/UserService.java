package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDBStorage") UserStorage userStorage){
        this.userStorage = userStorage;
    }


    public void addFriend(Integer userId1, Integer userId2){
        userStorage.addFriend(userId1, userId2);
    }

    public void removeFriend(Integer userId1, Integer userId2){
        userStorage.removeFriend(userId1, userId2);
    }

    public List<User> getCommonFriend(Integer userId1, Integer userId2){
        List<User> commonFriends = new ArrayList<>();
        List<User> user1Friends = userStorage.getUserFriends(userId1);
        List<User> user2Friends = userStorage.getUserFriends(userId2);
        for(User user : user1Friends){
            if(user2Friends.contains(user)){
                commonFriends.add(user);
            }
        }
        return commonFriends;
    }

    public User getUser(Integer id){
        return userStorage.getUser(id);
    }
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUserFriends(Integer id) {
        return userStorage.getUserFriends(id);
    }
}
