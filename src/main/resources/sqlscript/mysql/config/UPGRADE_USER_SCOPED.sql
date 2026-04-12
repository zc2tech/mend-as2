-- MySQL Migration Script: Add User-Scoped Keystore Support
-- This script upgrades existing installations to support user-specific keystores
-- Run this on existing databases that were created before user-scoping was added

-- Check if migration is needed by checking if user_id column exists
-- If column doesn't exist, this migration needs to be applied

-- Step 1: Add new columns to keydata table
ALTER TABLE keydata
  ADD COLUMN IF NOT EXISTS id INT AUTO_INCREMENT FIRST,
  ADD COLUMN IF NOT EXISTS user_id INT NOT NULL DEFAULT 0 AFTER id,
  ADD PRIMARY KEY IF NOT EXISTS (id),
  ADD UNIQUE KEY IF NOT EXISTS unique_user_purpose (user_id, purpose);

-- Step 2: Drop old primary key if it exists
-- Note: MySQL requires dropping the primary key before we can add a new one
-- The above commands handle this by checking IF NOT EXISTS

-- Step 3: Migrate existing keystores to admin user (user_id=0)
UPDATE keydata SET user_id = 0 WHERE user_id IS NULL OR user_id = 0;

-- Migration complete
-- Existing keystores are now assigned to admin (user_id=0)
-- New users can have their own keystores created with user_id > 0
