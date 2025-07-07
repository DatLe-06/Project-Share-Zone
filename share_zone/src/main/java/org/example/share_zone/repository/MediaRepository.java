package org.example.share_zone.repository;

import org.example.share_zone.model.Media;
import org.example.share_zone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.favoriteMedia WHERE u.id = :userId")
    User findByIdWithFavoriteMedia(@Param("userId") Long userId);
    @Query("SELECT m FROM Media m LEFT JOIN FETCH m.favoriteByUsers WHERE m.id = :mediaId")
    Media findByIdWithFavoriteByUsers(@Param("mediaId") Long mediaId);
    @Query("SELECT m FROM Media m LEFT JOIN FETCH m.comments WHERE m.id = :mediaId")
    Media findByIdWithComments(@Param("mediaId") Long mediaId);
    Set<Media> findAllByUploadedBy(User uploadedBy);
}
