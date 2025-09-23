package com.spkd.tinycrm.tinyos.repository;

import com.spkd.tinycrm.tinyos.entity.UserSession;
import com.spkd.tinycrm.tinyos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    Optional<UserSession> findBySessionTokenAndActiveTrue(String sessionToken);
    
    List<UserSession> findByUserAndActiveTrue(User user);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.user = :user")
    void deactivateAllUserSessions(@Param("user") User user);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.expiresAt < :now OR s.lastAccessedAt < :inactiveThreshold")
    void deactivateExpiredSessions(@Param("now") LocalDateTime now, @Param("inactiveThreshold") LocalDateTime inactiveThreshold);
    
    @Query("SELECT COUNT(s) FROM UserSession s WHERE s.user = :user AND s.active = true")
    long countActiveSessionsByUser(@Param("user") User user);
}
