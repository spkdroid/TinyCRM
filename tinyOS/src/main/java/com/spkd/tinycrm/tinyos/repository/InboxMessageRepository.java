package com.spkd.tinycrm.tinyos.repository;

import com.spkd.tinycrm.tinyos.entity.InboxMessage;
import com.spkd.tinycrm.tinyos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboxMessageRepository extends JpaRepository<InboxMessage, Long> {
    
    List<InboxMessage> findByRecipientOrderByCreatedAtDesc(User recipient);
    
    List<InboxMessage> findByRecipientAndIsReadOrderByCreatedAtDesc(User recipient, boolean isRead);
    
    long countByRecipientAndIsRead(User recipient, boolean isRead);
    
    @Modifying
    @Query("UPDATE InboxMessage m SET m.isRead = true, m.readAt = CURRENT_TIMESTAMP WHERE m.recipient = :recipient AND m.isRead = false")
    void markAllAsReadForUser(@Param("recipient") User recipient);
    
    void deleteByRecipient(User recipient);
}