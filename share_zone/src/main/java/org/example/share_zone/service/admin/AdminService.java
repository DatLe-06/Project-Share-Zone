package org.example.share_zone.service.admin;

import org.example.share_zone.model.Data;
import org.example.share_zone.model.Media;
import org.example.share_zone.repository.MediaRepository;
import org.example.share_zone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    @Autowired
    public AdminService(UserRepository userRepository, MediaRepository mediaRepository) {
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }

    public List<Data> getALlData() {
        return userRepository.findAll().stream().map(user -> {
            Data data = new Data();
            data.setId(user.getId());
            data.setName(user.getName());
            data.setEmail(user.getEmail());
            Set<Media> mediaFavourite = mediaRepository.findByIdWithFavoriteMedia(user.getId()).getFavoriteMedia();
            data.setMediaFavourite(mediaFavourite);
            Set<Media> mediaUploaded = mediaRepository.findAllByUploadedBy(user);
            data.setMediaUploaded(mediaUploaded);
            data.setFollowers(userRepository.findFollowersByUserId(user.getId()));
            data.setFollowing(userRepository.findFollowingByUserId(user.getId()));
            return data;
        }).collect(Collectors.toList());
    }
}
