package com.spkd.tinycrm.tinyos.service;

import com.spkd.tinycrm.tinyos.entity.User;
import com.spkd.tinycrm.tinyos.entity.UserMessage;
import com.spkd.tinycrm.tinyos.repository.UserMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserMessageService {

    @Autowired
    private UserMessageRepository userMessageRepository;

    public List<UserMessage> getMessagesForUser(User user) {
        return userMessageRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public List<UserMessage> getSentMessagesByUser(User user) {
        return userMessageRepository.findBySenderOrderByCreatedAtDesc(user);
    }

    public List<UserMessage> getAllMessagesByUser(User user) {
        return userMessageRepository.findMessagesByUser(user);
    }

    public List<UserMessage> getConversationBetweenUsers(User user1, User user2) {
        return userMessageRepository.findConversationBetweenUsers(user1, user2);
    }

    public List<UserMessage> getUnreadMessagesForUser(User user) {
        return userMessageRepository.findByRecipientAndIsReadOrderByCreatedAtDesc(user, false);
    }

    public long getUnreadCountForUser(User user) {
        return userMessageRepository.countByRecipientAndIsRead(user, false);
    }

    public Optional<UserMessage> getMessageById(Long messageId) {
        return userMessageRepository.findById(messageId);
    }

    public UserMessage sendMessage(User sender, User recipient, String subject, String message, UserMessage.MessagePriority priority) {
        UserMessage userMessage = new UserMessage();
        userMessage.setSender(sender);
        userMessage.setRecipient(recipient);
        userMessage.setSubject(subject);
        userMessage.setMessage(message);
        userMessage.setPriority(priority != null ? priority : UserMessage.MessagePriority.NORMAL);
        userMessage.setCreatedAt(LocalDateTime.now());
        
        return userMessageRepository.save(userMessage);
    }

    public UserMessage markAsRead(Long messageId) {
        Optional<UserMessage> messageOpt = userMessageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            UserMessage message = messageOpt.get();
            message.setRead(true);
            message.setReadAt(LocalDateTime.now());
            return userMessageRepository.save(message);
        }
        return null;
    }

    @Transactional
    public void markConversationAsRead(User recipient, User sender) {
        userMessageRepository.markConversationAsRead(recipient, sender);
    }

    public void deleteMessage(Long messageId) {
        userMessageRepository.deleteById(messageId);
    }

    @Transactional
    public void deleteAllMessagesForUser(User user) {
        userMessageRepository.deleteByRecipient(user);
        userMessageRepository.deleteBySender(user);
    }
}