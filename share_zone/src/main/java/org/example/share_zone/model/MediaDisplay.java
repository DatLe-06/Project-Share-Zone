package org.example.share_zone.model;

import lombok.Data;

@Data
public class MediaDisplay {
    private Media media;
    private boolean upLoadedByThisUser;
    private boolean isFavourite;

    public MediaDisplay() {
    }

    public MediaDisplay(Media media, boolean isFavourite, boolean upLoadedByThisUser) {
        this.media = media;
        this.isFavourite = isFavourite;
        this.upLoadedByThisUser = upLoadedByThisUser;
    }
}
