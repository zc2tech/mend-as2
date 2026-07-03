-- ============================================================
-- PostgreSQL Migration Script - CONFIG DATABASE
-- Add User-Specific API Authentication Tables and Columns
-- ============================================================

-- Step 1: Add columns to webui_users table
ALTER TABLE webui_users
ADD COLUMN api_auth_basic_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN api_auth_cert_enabled BOOLEAN DEFAULT FALSE;

-- Step 2: Create user_api_auth_credentials table
CREATE TABLE user_api_auth_credentials (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    auth_type INTEGER NOT NULL,      -- 1=basic, 2=certificate
    username VARCHAR(256),            -- For basic auth (null for cert)
    password VARCHAR(256),            -- For basic auth (null for cert)
    cert_fingerprint VARCHAR(255),    -- For cert auth (null for basic), SHA-1 format
    cert_alias VARCHAR(255),          -- Certificate alias/name for display
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES webui_users(id) ON DELETE CASCADE
);

-- Step 3: Create indexes on user_api_auth_credentials
CREATE INDEX idx_user_api_auth_user ON user_api_auth_credentials(user_id);
CREATE INDEX idx_user_api_auth_type ON user_api_auth_credentials(auth_type);

-- Step 4: Verify the changes
SELECT
    'webui_users columns added' as status,
    COUNT(*) as column_count
FROM information_schema.columns
WHERE table_name = 'webui_users'
AND column_name IN ('api_auth_basic_enabled', 'api_auth_cert_enabled');

SELECT
    'user_api_auth_credentials table created' as status,
    COUNT(*) as table_exists
FROM information_schema.tables
WHERE table_name = 'user_api_auth_credentials';

-- Done!
