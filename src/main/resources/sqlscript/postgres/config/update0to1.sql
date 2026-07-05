-- ============================================================
-- PostgreSQL Migration Script - CONFIG DATABASE
-- Add User-Specific API Authentication Tables and Columns
-- ============================================================

-- Step 1: Add columns to webui_users table (only if they don't exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'webui_users'
        AND column_name = 'api_auth_basic_enabled'
    ) THEN
        ALTER TABLE webui_users ADD COLUMN api_auth_basic_enabled BOOLEAN DEFAULT FALSE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'webui_users'
        AND column_name = 'api_auth_cert_enabled'
    ) THEN
        ALTER TABLE webui_users ADD COLUMN api_auth_cert_enabled BOOLEAN DEFAULT FALSE;
    END IF;
END $$;

-- Step 2: Create user_api_auth_credentials table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS user_api_auth_credentials (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    auth_type INTEGER NOT NULL,
    username VARCHAR(256),
    password VARCHAR(256),
    cert_fingerprint VARCHAR(255),
    cert_alias VARCHAR(255),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES webui_users(id) ON DELETE CASCADE
);

-- Step 3: Create indexes on user_api_auth_credentials (only if they don't exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE tablename = 'user_api_auth_credentials'
        AND indexname = 'idx_user_api_auth_user'
    ) THEN
        CREATE INDEX idx_user_api_auth_user ON user_api_auth_credentials(user_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE tablename = 'user_api_auth_credentials'
        AND indexname = 'idx_user_api_auth_type'
    ) THEN
        CREATE INDEX idx_user_api_auth_type ON user_api_auth_credentials(auth_type);
    END IF;
END $$;

-- Done!
