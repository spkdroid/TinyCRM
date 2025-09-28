-- V3__Add_user_profile_fields.sql
-- Migration script to add profile fields to users table

ALTER TABLE users ADD COLUMN phone_number VARCHAR(20) NULL;
ALTER TABLE users ADD COLUMN address VARCHAR(255) NULL;
ALTER TABLE users ADD COLUMN city VARCHAR(100) NULL;
ALTER TABLE users ADD COLUMN state VARCHAR(100) NULL;
ALTER TABLE users ADD COLUMN zip_code VARCHAR(20) NULL;
ALTER TABLE users ADD COLUMN country VARCHAR(100) NULL;
ALTER TABLE users ADD COLUMN bio TEXT NULL;
ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500) NULL;
ALTER TABLE users ADD COLUMN job_title VARCHAR(100) NULL;
ALTER TABLE users ADD COLUMN department VARCHAR(100) NULL;
ALTER TABLE users ADD COLUMN company VARCHAR(100) NULL;
ALTER TABLE users ADD COLUMN date_of_birth DATETIME NULL;
ALTER TABLE users ADD COLUMN timezone VARCHAR(50) DEFAULT 'UTC';
ALTER TABLE users ADD COLUMN language VARCHAR(10) DEFAULT 'en';
ALTER TABLE users ADD COLUMN email_notifications BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN sms_notifications BOOLEAN DEFAULT FALSE;

-- Add indexes for better performance
CREATE INDEX idx_users_phone_number ON users(phone_number);
CREATE INDEX idx_users_city ON users(city);
CREATE INDEX idx_users_country ON users(country);
CREATE INDEX idx_users_company ON users(company);
CREATE INDEX idx_users_department ON users(department);

-- Add updated_at column if it doesn't exist
ALTER TABLE users ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;