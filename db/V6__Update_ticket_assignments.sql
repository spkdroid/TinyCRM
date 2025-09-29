-- V6: Update existing ticket assignments to use user IDs
-- This migration updates any existing tickets that have string assignee values
-- to use proper user IDs from the users table

-- First, let's add any missing users that might be referenced in existing tickets
INSERT IGNORE INTO users (username, email, first_name, last_name, password, role, created_at, updated_at)
VALUES 
('john.smith', 'john.smith@company.com', 'John', 'Smith', '$2a$10$defaultpasswordhash', 'SUPPORT', NOW(), NOW()),
('sarah.wilson', 'sarah.wilson@company.com', 'Sarah', 'Wilson', '$2a$10$defaultpasswordhash', 'ADMIN', NOW(), NOW()),
('mike.johnson', 'mike.johnson@company.com', 'Mike', 'Johnson', '$2a$10$defaultpasswordhash', 'USER', NOW(), NOW());

-- Update existing tickets to use proper user IDs for assignees
-- Only update tickets where assignee_id is NULL but assigned_to has a value
UPDATE support_tickets st
JOIN users u ON st.assigned_to = u.username
SET st.assignee_id = u.id
WHERE st.assignee_id IS NULL 
  AND st.assigned_to IS NOT NULL 
  AND st.assigned_to != '';

-- Update existing tickets to use proper user IDs for creators
-- This assumes tickets were created by the first admin user if no creator is set
UPDATE support_tickets st
SET st.creator_id = (SELECT id FROM users WHERE role = 'ADMIN' LIMIT 1)
WHERE st.creator_id IS NULL;

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_support_tickets_assignee ON support_tickets(assignee_id);
CREATE INDEX IF NOT EXISTS idx_support_tickets_creator ON support_tickets(creator_id);
CREATE INDEX IF NOT EXISTS idx_support_tickets_status ON support_tickets(status);
CREATE INDEX IF NOT EXISTS idx_support_tickets_priority ON support_tickets(priority);
CREATE INDEX IF NOT EXISTS idx_support_tickets_created_date ON support_tickets(created_date);

-- Add indexes for user messages
CREATE INDEX IF NOT EXISTS idx_user_messages_sender ON user_messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_user_messages_recipient ON user_messages(recipient_id);
CREATE INDEX IF NOT EXISTS idx_user_messages_created_at ON user_messages(created_at);
CREATE INDEX IF NOT EXISTS idx_user_messages_read ON user_messages(is_read);

-- Add indexes for ticket comments
CREATE INDEX IF NOT EXISTS idx_ticket_comments_ticket ON ticket_comments(ticket_id);
CREATE INDEX IF NOT EXISTS idx_ticket_comments_user ON ticket_comments(user_id);
CREATE INDEX IF NOT EXISTS idx_ticket_comments_created_at ON ticket_comments(created_at);

-- Add indexes for inbox messages
CREATE INDEX IF NOT EXISTS idx_inbox_messages_user ON inbox_messages(user_id);
CREATE INDEX IF NOT EXISTS idx_inbox_messages_read ON inbox_messages(is_read);
CREATE INDEX IF NOT EXISTS idx_inbox_messages_created_at ON inbox_messages(created_at);