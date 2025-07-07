package org.example.share_zone.service.user;

import org.example.share_zone.model.Media;
import org.example.share_zone.model.User;
import org.example.share_zone.repository.MediaRepository;
import org.example.share_zone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    @Autowired
    public UserServiceImplement(UserRepository userRepository, MediaRepository mediaRepository) {
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null) ;
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.getUserByName(name);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getFirstByEmail(email);
    }

    @Override
    public void handleMediaFavorite(long userId, Long mediaId) {
        User user = mediaRepository.findByIdWithFavoriteMedia(userId);
        Media media = mediaRepository.findByIdWithFavoriteByUsers(mediaId);

        Set<Media> favourite = user.getFavoriteMedia();
        Set<User> favoriteByUsers = media.getFavoriteByUsers();

        if (favourite.contains(media)) {
            favourite.remove(media);
            favoriteByUsers.remove(user);
        } else {
            favourite.add(media);
            favoriteByUsers.add(user);
        }

        user.setFavoriteMedia(favourite);
        media.setFavoriteByUsers(favoriteByUsers);

        mediaRepository.save(media);
        userRepository.save(user);
    }
}