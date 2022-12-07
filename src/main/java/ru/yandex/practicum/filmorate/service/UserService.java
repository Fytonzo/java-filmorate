package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage){
        this.inMemoryUserStorage = inMemoryUserStorage;
    }


    public void addFriend(Integer idUser1, Integer idUser2){
        Set<Integer> user1Friends = inMemoryUserStorage.getUser(idUser1).getFriends();
        Set<Integer> user2Friends = inMemoryUserStorage.getUser(idUser2).getFriends();
        user1Friends.add(idUser2);
        user2Friends.add(idUser1);
        inMemoryUserStorage.getUser(idUser1).setFriends((HashSet<Integer>) user1Friends);
        inMemoryUserStorage.getUser(idUser2).setFriends((HashSet<Integer>) user2Friends);
    }

    public void removeFriend(Integer idUser1, Integer idUser2){
        Set<Integer> user1Friends = inMemoryUserStorage.getUser(idUser1).getFriends();
        Set<Integer> user2Friends = inMemoryUserStorage.getUser(idUser2).getFriends();
        user1Friends.remove(idUser2);
        user2Friends.remove(idUser1);
        inMemoryUserStorage.getUser(idUser1).setFriends((HashSet<Integer>) user1Friends);
        inMemoryUserStorage.getUser(idUser2).setFriends((HashSet<Integer>) user2Friends);
    }

    public List<User> getCommonFriend(Integer idUser1, Integer idUser2){
        Set<Integer> user1Friends = inMemoryUserStorage.getUser(idUser1).getFriends();
        Set<Integer> user2Friends = inMemoryUserStorage.getUser(idUser2).getFriends();
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
        List<User> userFriends = new ArrayList<>();
        for (Integer userId : inMemoryUserStorage.getUser(id).getFriends()){
            userFriends.add(inMemoryUserStorage.getUser(userId));
        }
        return userFriends;
    }
}
