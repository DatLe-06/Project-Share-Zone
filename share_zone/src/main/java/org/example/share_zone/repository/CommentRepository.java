package org.example.share_zone.repository;

import org.example.share_zone.model.Comment;
import org.example.share_zone.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMedia(Media media);

    List<Comment> findByMedia_Id(Long mediaId);
}
