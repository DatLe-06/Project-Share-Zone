package org.example.share_zone.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User commentedBy;

    @ManyToOne
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime commentedAt;

    @Override
    public int hashCode() {
        return Objects.hash(id); // Chỉ dùng id
    }

    @Override
    public String toString() {
        return "Comment{id=" + id + ", content='" + content + "}";
    }
}