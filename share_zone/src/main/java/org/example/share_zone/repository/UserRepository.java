package org.example.share_zone.repository;

import org.example.share_zone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    List<User> getUsersByEmail(String email);
    User getFirstByEmail(String email);
    User getUserByName(String name);
    User getUserById(Long id);

    @Query("SELECT u.following FROM User u WHERE u.id = :userId")
    Set<User> findFollowingByUserId(@Param("userId") Long userId);
    @Query("SELECT u.followers FROM User u WHERE u.id = :userId")
    Set<User> findFollowersByUserId(@Param("userId") Long userId);
}