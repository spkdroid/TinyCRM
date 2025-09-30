-- V5__Add_user_messaging_and_ticket_assignment.sql
-- Migration to add user messaging system and enhance ticket assignment

-- Add new columns to support_ticket for proper user relationships
ALTER TABLE support_ticket 
ADD COLUMN creator_id BIGINT NULL,
ADD COLUMN assignee_id BIGINT NULL;

-- Add foreign key constraints for support_ticket
ALTER TABLE support_ticket
ADD CONSTRAINT fk_support_ticket_creator FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE SET NULL,
ADD CONSTRAINT fk_support_ticket_assignee FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL;

-- Create indexes for better performance
CREATE INDEX idx_support_ticket_creator ON support_ticket(creator_id);
CREATE INDEX idx_support_ticket_assignee ON support_ticket(assignee_id);

-- Create user_messages table for messaging system
CREATE TABLE IF NOT EXISTS user_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT,
    priority ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    related_ticket_id BIGINT NULL,
    
    INDEX idx_recipient_created (recipient_id, created_at DESC),
    INDEX idx_sender_created (sender_id, created_at DESC),
    INDEX idx_recipient_read (recipient_id, is_read),
    INDEX idx_conversation (sender_id, recipient_id, created_at ASC),
    INDEX idx_related_ticket (related_ticket_id),
    
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (related_ticket_id) REFERENCES support_ticket(id) ON DELETE SET NULL
);

-- Add additional indexes for better performance
CREATE INDEX idx_user_messages_priority ON user_messages(priority, created_at DESC);
CREATE INDEX idx_user_messages_read_created ON user_messages(is_read, created_at DESC);