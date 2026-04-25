-- Add payload format and doctype columns to messages table
-- These columns store payload metadata for filtering and tracking

ALTER TABLE messages 
ADD COLUMN IF NOT EXISTS payloadformat VARCHAR(50),
ADD COLUMN IF NOT EXISTS payloaddoctype VARCHAR(100);
