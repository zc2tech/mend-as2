-- MySQL Migration Script: Add Message Ownership Support
-- This script upgrades existing installations to support user-specific message ownership
-- Run this on existing databases that were created before message ownership was added

-- Step 1: Add owner_user_id column to messages table
ALTER TABLE messages
  ADD COLUMN IF NOT EXISTS owner_user_id INT DEFAULT 0 NOT NULL;

-- Step 2: Create index for faster user-based message queries
CREATE INDEX IF NOT EXISTS idx_messages_owner_user ON messages(owner_user_id);

-- Step 3: Set existing messages to admin owner
UPDATE messages SET owner_user_id = 0 WHERE owner_user_id IS NULL OR owner_user_id = 0;

-- Migration complete
-- Existing messages are now assigned to admin (owner_user_id=0)
-- New messages will be assigned to the receiving user based on HttpReceiver endpoint
