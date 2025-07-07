package org.example.share_zone.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String avatar;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "follower_id", foreignKey = @ForeignKey(name = "FK_USER_FOLLOWERS")),
            inverseJoinColumns = @JoinColumn(name = "followed_id", foreignKey = @ForeignKey(name = "FK_USER_FOLLOWED"))
    )
    private Set<User> following = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_favorite_media",
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER_FAVORITE_MEDIA")),
            inverseJoinColumns = @JoinColumn(name = "media_id", foreignKey = @ForeignKey(name = "FK_MEDIA_FAVORITE_USER"))
    )
    private Set<Media> favoriteMedia = new HashSet<>();

    public enum Role {
        USER, ADMIN
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Chỉ dùng id
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}