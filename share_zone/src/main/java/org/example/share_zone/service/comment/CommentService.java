package org.example.share_zone.service.comment;

import org.example.share_zone.model.Comment;
import org.example.share_zone.model.Media;
import org.example.share_zone.model.User;
import org.example.share_zone.repository.CommentRepository;
import org.example.share_zone.repository.MediaRepository;
import org.example.share_zone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;


    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, MediaRepository mediaRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
    }

    public void addComment(Long userId, Long mediaId, String content) {
        // Tìm user và media
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new IllegalArgumentException("Media not found"));

        // Tạo comment mới
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCommentedBy(user);
        comment.setMedia(media);

        // Lưu comment vào database
        commentRepository.save(comment);
    }

    public List<Comment> findByMediaId(long mediaId) {
        return commentRepository.findByMedia_Id(mediaId);
    }
}
