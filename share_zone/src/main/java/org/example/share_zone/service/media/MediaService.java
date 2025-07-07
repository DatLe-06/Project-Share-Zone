package org.example.share_zone.service.media;

import org.example.share_zone.model.Media;
import org.example.share_zone.model.MediaDisplay;
import org.example.share_zone.model.User;

import java.util.List;

public interface MediaService {
    List<MediaDisplay> getAllMedias(User user);
    Media getMediaById(Long id);
    void saveMedia(Media customer);
    List<MediaDisplay> getMediaUploadedByUser(User user);
    List<MediaDisplay> getFavouriteMediaByUser(long userId);
    User getUserUploadedByMedia(long mediaId);
    Media getMediaWithComments(Media media);
    MediaDisplay getMediaDisplay(Media media, User user);
}
