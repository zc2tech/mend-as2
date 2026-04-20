-- PostgreSQL Migration Script: Add User-Scoped Keystore Support
-- This script upgrades existing installations to support user-specific keystores
-- Run this on existing databases that were created before user-scoping was added

-- Step 1: Check if migration has already been applied
DO $$
BEGIN
    -- Check if user_id column exists
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'keydata'
        AND column_name = 'user_id'
    ) THEN
        -- Add id column as primary key
        ALTER TABLE keydata ADD COLUMN id SERIAL;

        -- Add user_id column
        ALTER TABLE keydata ADD COLUMN user_id INTEGER NOT NULL DEFAULT 0;

        -- Drop old primary key constraint on purpose
        ALTER TABLE keydata DROP CONSTRAINT IF EXISTS keydata_pkey;

        -- Set id as new primary key
        ALTER TABLE keydata ADD PRIMARY KEY (id);

        -- Add unique constraint on (user_id, purpose)
        ALTER TABLE keydata ADD CONSTRAINT unique_user_purpose UNIQUE (user_id, purpose);

        -- Migrate existing keystores to admin user (user_id=0)
        UPDATE keydata SET user_id = 0;

        RAISE NOTICE 'Migration completed: keydata table updated for user-scoped keystores';
    ELSE
        RAISE NOTICE 'Migration skipped: keydata table already has user_id column';
    END IF;
END $$;

-- Migration complete
-- Existing keystores are now assigned to admin (user_id=0)
-- New users can have their own keystores created with user_id > 0
