-- ============================================================
-- MySQL Migration Script - CONFIG DATABASE
-- Add User-Specific API Authentication Tables and Columns
-- ============================================================

-- Step 1: Add columns to webui_users table (only if they don't exist)
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
                   WHERE table_schema = DATABASE()
                   AND table_name = 'webui_users'
                   AND column_name = 'api_auth_basic_enabled');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE webui_users ADD COLUMN api_auth_basic_enabled BOOLEAN DEFAULT FALSE',
    'SELECT "Column api_auth_basic_enabled already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
                   WHERE table_schema = DATABASE()
                   AND table_name = 'webui_users'
                   AND column_name = 'api_auth_cert_enabled');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE webui_users ADD COLUMN api_auth_cert_enabled BOOLEAN DEFAULT FALSE',
    'SELECT "Column api_auth_cert_enabled already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Step 2: Create user_api_auth_credentials table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS user_api_auth_credentials (
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

-- Step 3: Create indexes on user_api_auth_credentials (only if they don't exist)
SET @index_exists = (SELECT COUNT(*) FROM information_schema.statistics
                     WHERE table_schema = DATABASE()
                     AND table_name = 'user_api_auth_credentials'
                     AND index_name = 'idx_user_api_auth_user');

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_user_api_auth_user ON user_api_auth_credentials(user_id)',
    'SELECT "Index idx_user_api_auth_user already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM information_schema.statistics
                     WHERE table_schema = DATABASE()
                     AND table_name = 'user_api_auth_credentials'
                     AND index_name = 'idx_user_api_auth_type');

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_user_api_auth_type ON user_api_auth_credentials(auth_type)',
    'SELECT "Index idx_user_api_auth_type already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Done!
