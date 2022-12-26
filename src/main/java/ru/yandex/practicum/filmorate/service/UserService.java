package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage){
        this.inMemoryUserStorage = inMemoryUserStorage;
    }


    public void addFriend(Integer userId1, Integer userId2){
        User user1 = inMemoryUserStorage.getUser(userId1);
        User user2 = inMemoryUserStorage.getUser(userId2);
        user1.addFriend(userId2);
        user2.addFriend(userId1);
    }

    public void removeFriend(Integer userId1, Integer userId2){
        User user1 = inMemoryUserStorage.getUser(userId1);
        User user2 = inMemoryUserStorage.getUser(userId2);
        user1.removeFriend(userId2);
        user2.removeFriend(userId1);
    }

    public List<User> getCommonFriend(Integer userId1, Integer userId2){
        User user1 = inMemoryUserStorage.getUser(userId1);
        User user2 = inMemoryUserStorage.getUser(userId2);
        Set<Integer> user1Friends = user1.getFriends();
        Set<Integer> user2Friends = user2.getFriends();
        List<User> result = new ArrayList<>();
        for (Integer i : user1Friends){
            if (user2Friends.contains(i)){
                result.add(inMemoryUserStorage.getUser(i));
            }
        }
        return result;
    }

    public User getUser(Integer id){
        return inMemoryUserStorage.getUser(id);
    }
    public List<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    public User addUser(User user) {
        return inMemoryUserStorage.addUser(user);
    }

    public User updateUser(User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    public List<User> getUserFriends(Integer id) {
        User user = inMemoryUserStorage.getUser(id);
        List<User> userFriends = new ArrayList<>();
        for (Integer userId : user.getFriends()){
            userFriends.add(inMemoryUserStorage.getUser(userId));
        }
        return userFriends;
    }
}
