package org.example.share_zone.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_MEDIA_USER"))
    private User uploadedBy;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "media_tags",
            joinColumns = @JoinColumn(name = "media_id", foreignKey = @ForeignKey(name = "FK_MEDIA_TAGS")),
            inverseJoinColumns = @JoinColumn(name = "tag_id", foreignKey = @ForeignKey(name = "FK_TAGS_MEDIA"))
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(mappedBy = "favoriteMedia")
    private Set<User> favoriteByUsers = new HashSet<>();

    public enum MediaType {
        IMAGE, VIDEO
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Chỉ dùng id
    }

    @Override
    public String toString() {
        return "Media{id=" + id + ", url='" + url + "'}";
    }
}