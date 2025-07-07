package org.example.share_zone.service.media;

import org.example.share_zone.model.Media;
import org.example.share_zone.model.MediaDisplay;
import org.example.share_zone.model.User;
import org.example.share_zone.repository.MediaRepository;
import org.example.share_zone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MediaServiceImplement implements MediaService {
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;

    @Autowired
    public MediaServiceImplement(MediaRepository mediaRepository, UserRepository userRepository) {
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<MediaDisplay> getAllMedias(User user) {
        List<Media> allMedia = mediaRepository.findAll();
        Set<Media> createSet = new HashSet<>(allMedia);
        return setMediaDisplay(createSet, user);
    }

    @Override
    public Media getMediaById(Long id) {
        return mediaRepository.findById(id).orElse(null) ;
    }

    @Override
    public void saveMedia(Media media) {
        mediaRepository.save(media);
    }

    @Override
    public List<MediaDisplay> getMediaUploadedByUser(User user) {
        return setMediaDisplay(mediaRepository.findAllByUploadedBy(user),user);
    }

    @Override
    public List<MediaDisplay> getFavouriteMediaByUser(long userId) {
        return setMediaDisplay(mediaRepository.findByIdWithFavoriteMedia(userId).getFavoriteMedia(),
                userRepository.findById(userId).orElse(null));
    }

    @Override
    public User getUserUploadedByMedia(long mediaId) {
        return Objects.requireNonNull(mediaRepository.findById(mediaId).orElse(null)).getUploadedBy();
    }

    @Override
    public Media getMediaWithComments(Media media) {
        return mediaRepository.findByIdWithComments(media.getId());
    }

    public MediaDisplay getMediaDisplay(Media media, User user) {
        MediaDisplay mediaDisplay = new MediaDisplay();
        mediaDisplay.setMedia(media);
        boolean setFavourite = false;
        if (user != null) {
            setFavourite = mediaRepository.findByIdWithFavoriteMedia(user.getId())
                    .getFavoriteMedia().contains(media);
        }
        mediaDisplay.setFavourite(setFavourite);
        boolean upLoadByThisUser = Objects.equals(media.getUploadedBy().getId(), user != null ? user.getId() : null);
        mediaDisplay.setUpLoadedByThisUser(upLoadByThisUser);
        return mediaDisplay;
    }

    private List<MediaDisplay> setMediaDisplay(Set<Media> allMedia, User user) {
        return allMedia.stream().map(media ->
            getMediaDisplay(media, user)
        ).collect(Collectors.toList());
    }
}