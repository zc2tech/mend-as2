-- PostgreSQL Migration Script: Add Message Ownership Support
-- This script upgrades existing installations to support user-specific message ownership
-- Run this on existing databases that were created before message ownership was added

-- Step 1: Check if migration has already been applied
DO $$
BEGIN
    -- Check if owner_user_id column exists
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'messages'
        AND column_name = 'owner_user_id'
    ) THEN
        -- Add owner_user_id column to messages table
        ALTER TABLE messages ADD COLUMN owner_user_id INTEGER DEFAULT 0 NOT NULL;

        -- Create index for faster user-based message queries
        CREATE INDEX idx_messages_owner_user ON messages(owner_user_id);

        -- Set existing messages to admin owner
        UPDATE messages SET owner_user_id = 0;

        RAISE NOTICE 'Migration completed: messages table updated for message ownership';
    ELSE
        RAISE NOTICE 'Migration skipped: messages table already has owner_user_id column';
    END IF;
END $$;

-- Migration complete
-- Existing messages are now assigned to admin (owner_user_id=0)
-- New messages will be assigned to the receiving user based on HttpReceiver endpoint
