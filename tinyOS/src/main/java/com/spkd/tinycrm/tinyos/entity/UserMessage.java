package com.spkd.tinycrm.tinyos.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_messages")
public class UserMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    private MessagePriority priority = MessagePriority.NORMAL;
    
    @Column(name = "is_read")
    private boolean isRead = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @ManyToOne
    @JoinColumn(name = "related_ticket_id")
    private SupportTicket relatedTicket;

    public enum MessagePriority {
        LOW, NORMAL, HIGH, URGENT
    }

    // Constructors
    public UserMessage() {}

    public UserMessage(User sender, User recipient, String subject, String message) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessagePriority getPriority() {
        return priority;
    }

    public void setPriority(MessagePriority priority) {
        this.priority = priority;
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
}