package com.spkd.tinycrm.tinyos.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inbox_messages")
public class InboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender; // Can be null for system messages
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    private MessageType type = MessageType.NOTIFICATION;
    
    @Column(name = "is_read")
    private boolean isRead = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    // Reference to related entities
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private SupportTicket relatedTicket;
    
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private TicketComment relatedComment;

    public enum MessageType {
        NOTIFICATION, COMMENT, TICKET_UPDATE, SYSTEM, ADMIN_MESSAGE
    }

    // Constructors
    public InboxMessage() {}

    public InboxMessage(User recipient, String title, String message, MessageType type) {
        this.recipient = recipient;
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public SupportTicket getRelatedTicket() {
        return relatedTicket;
    }

    public void setRelatedTicket(SupportTicket relatedTicket) {
        this.relatedTicket = relatedTicket;
    }

    public TicketComment getRelatedComment() {
        return relatedComment;
    }

    public void setRelatedComment(TicketComment relatedComment) {
        this.relatedComment = relatedComment;
    }
}