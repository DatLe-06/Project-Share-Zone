package org.example.share_zone.service.user;

import org.example.share_zone.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByName(String name);
    void saveUser(User user);
    User getUserByEmail(String email);
    void handleMediaFavorite(long user, Long media);
}
