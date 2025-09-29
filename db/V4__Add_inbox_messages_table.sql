-- V4__Add_inbox_messages_table.sql
-- Migration to add inbox messages for notifications system

CREATE TABLE IF NOT EXISTS inbox_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    sender_id BIGINT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type ENUM('NOTIFICATION', 'COMMENT', 'TICKET_UPDATE', 'SYSTEM', 'ADMIN_MESSAGE') DEFAULT 'NOTIFICATION',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    ticket_id BIGINT NULL,
    comment_id BIGINT NULL,
    
    INDEX idx_recipient_created (recipient_id, created_at DESC),
    INDEX idx_recipient_read (recipient_id, is_read),
    INDEX idx_ticket (ticket_id),
    INDEX idx_comment (comment_id),
    
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (ticket_id) REFERENCES support_ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES ticket_comment(id) ON DELETE CASCADE
);

-- Add indexes for better performance
CREATE INDEX idx_inbox_type_created ON inbox_messages(type, created_at DESC);
CREATE INDEX idx_inbox_read_created ON inbox_messages(is_read, created_at DESC);