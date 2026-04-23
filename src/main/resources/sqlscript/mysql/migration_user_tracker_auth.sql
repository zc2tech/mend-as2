-- ============================================================
-- MySQL Migration Script
-- Add User-Specific Tracker Authentication Tables and Columns
-- ============================================================

-- Step 1: Add columns to webui_users table
ALTER TABLE webui_users
ADD COLUMN tracker_auth_basic_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN tracker_auth_cert_enabled BOOLEAN DEFAULT FALSE;

-- Step 2: Create user_tracker_auth_credentials table
CREATE TABLE user_tracker_auth_credentials (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    auth_type INT NOT NULL COMMENT '1=basic, 2=certificate',
    username VARCHAR(256) COMMENT 'For basic auth (null for cert)',
    password VARCHAR(256) COMMENT 'For basic auth (null for cert)',
    cert_fingerprint VARCHAR(255) COMMENT 'For cert auth (null for basic), SHA-1 format',
    cert_alias VARCHAR(255) COMMENT 'Certificate alias/name for display',
    enabled TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES webui_users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step 3: Create indexes
CREATE INDEX idx_user_tracker_auth_user ON user_tracker_auth_credentials(user_id);
CREATE INDEX idx_user_tracker_auth_type ON user_tracker_auth_credentials(auth_type);

-- Step 4: Verify the changes
SELECT
    'webui_users columns added' as status,
    COUNT(*) as column_count
FROM information_schema.columns
WHERE table_schema = DATABASE()
AND table_name = 'webui_users'
AND column_name IN ('tracker_auth_basic_enabled', 'tracker_auth_cert_enabled');

SELECT
    'user_tracker_auth_credentials table created' as status,
    COUNT(*) as table_exists
FROM information_schema.tables
WHERE table_schema = DATABASE()
AND table_name = 'user_tracker_auth_credentials';

-- Done!
