package com.spkd.tinycrm.tinyos.repository;

import com.spkd.tinycrm.tinyos.entity.User;
import com.spkd.tinycrm.tinyos.entity.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {
    
    List<UserMessage> findByRecipientOrderByCreatedAtDesc(User recipient);
    
    List<UserMessage> findBySenderOrderByCreatedAtDesc(User sender);
    
    List<UserMessage> findByRecipientAndIsReadOrderByCreatedAtDesc(User recipient, boolean isRead);
    
    long countByRecipientAndIsRead(User recipient, boolean isRead);
    
    @Query("SELECT m FROM UserMessage m WHERE (m.sender = :user OR m.recipient = :user) ORDER BY m.createdAt DESC")
    List<UserMessage> findMessagesByUser(@Param("user") User user);
    
    @Query("SELECT m FROM UserMessage m WHERE (m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1) ORDER BY m.createdAt ASC")
    List<UserMessage> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    @Modifying
    @Query("UPDATE UserMessage m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP WHERE m.recipient = :recipient AND m.sender = :sender AND m.isRead = false")
    void markConversationAsRead(@Param("recipient") User recipient, @Param("sender") User sender);
    
    void deleteByRecipient(User recipient);
    void deleteBySender(User sender);
}