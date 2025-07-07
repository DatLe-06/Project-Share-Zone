package org.example.share_zone.model;

import java.util.HashSet;
import java.util.Set;

@lombok.Data
public class Data {
    private long id;
    private String name;
    private String email;
    private Set<Media> mediaUploaded = new HashSet<>();
    private Set<Media> mediaFavourite = new HashSet<>();
    private Set<User> followers = new HashSet<>();
    private Set<User> following = new HashSet<>();
}
