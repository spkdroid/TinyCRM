package com.spkd.tinycrm.tinyos.service;

import com.spkd.tinycrm.tinyos.entity.InboxMessage;
import com.spkd.tinycrm.tinyos.entity.SupportTicket;
import com.spkd.tinycrm.tinyos.entity.TicketComment;
import com.spkd.tinycrm.tinyos.entity.User;
import com.spkd.tinycrm.tinyos.repository.InboxMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InboxService {

    @Autowired
    private InboxMessageRepository inboxMessageRepository;

    public List<InboxMessage> getMessagesForUser(User user) {
        return inboxMessageRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public List<InboxMessage> getUnreadMessagesForUser(User user) {
        return inboxMessageRepository.findByRecipientAndIsReadOrderByCreatedAtDesc(user, false);
    }

    public long getUnreadCountForUser(User user) {
        return inboxMessageRepository.countByRecipientAndIsRead(user, false);
    }

    public InboxMessage createMessage(User recipient, String title, String message, InboxMessage.MessageType type) {
        InboxMessage inboxMessage = new InboxMessage(recipient, title, message, type);
        return inboxMessageRepository.save(inboxMessage);
    }

    public InboxMessage createMessage(User recipient, User sender, String title, String message, InboxMessage.MessageType type) {
        InboxMessage inboxMessage = new InboxMessage(recipient, title, message, type);
        inboxMessage.setSender(sender);
        return inboxMessageRepository.save(inboxMessage);
    }

    public InboxMessage createTicketNotification(User recipient, SupportTicket ticket, String title, String message) {
        InboxMessage inboxMessage = new InboxMessage(recipient, title, message, InboxMessage.MessageType.TICKET_UPDATE);
        inboxMessage.setRelatedTicket(ticket);
        return inboxMessageRepository.save(inboxMessage);
    }

    public InboxMessage createCommentNotification(User recipient, User sender, TicketComment comment, String title, String message) {
        InboxMessage inboxMessage = new InboxMessage(recipient, title, message, InboxMessage.MessageType.COMMENT);
        inboxMessage.setSender(sender);
        inboxMessage.setRelatedComment(comment);
        inboxMessage.setRelatedTicket(comment.getSupportTicket());
        return inboxMessageRepository.save(inboxMessage);
    }

    public Optional<InboxMessage> getMessageById(Long id) {
        return inboxMessageRepository.findById(id);
    }

    public InboxMessage markAsRead(Long messageId) {
        Optional<InboxMessage> messageOpt = inboxMessageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            InboxMessage message = messageOpt.get();
            message.setRead(true);
            message.setReadAt(LocalDateTime.now());
            return inboxMessageRepository.save(message);
        }
        return null;
    }

    @Transactional
    public void markAllAsReadForUser(User user) {
        inboxMessageRepository.markAllAsReadForUser(user);
    }

    public void deleteMessage(Long messageId) {
        inboxMessageRepository.deleteById(messageId);
    }

    public void deleteAllMessagesForUser(User user) {
        inboxMessageRepository.deleteByRecipient(user);
    }
}