package com.spkd.tinycrm.tinyos.repository;

import com.spkd.tinycrm.tinyos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE (u.username = :login OR u.email = :login) AND u.active = true")
    Optional<User> findByUsernameOrEmailAndActive(@Param("login") String login);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}