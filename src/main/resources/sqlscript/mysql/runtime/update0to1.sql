-- ============================================================
-- MySQL Migration Script - RUNTIME DATABASE
-- Add API Request Log and Auth Failure Tables
-- ============================================================

-- Step 1: Create api_request_log table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS api_request_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(255) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    remote_addr VARCHAR(255),
    user_agent VARCHAR(512),
    http_method VARCHAR(10) NOT NULL,
    request_path VARCHAR(512) NOT NULL,
    request_headers TEXT,
    request_body LONGBLOB,
    content_type VARCHAR(255),
    content_size INT NOT NULL,
    response_status INT NOT NULL,
    auth_status INT DEFAULT 0 COMMENT '0=none, 1=success, 2=failed',
    auth_user VARCHAR(255),
    request_time TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step 2: Create indexes on api_request_log (only if they don't exist)
SET @index_exists = (SELECT COUNT(*) FROM information_schema.statistics
                     WHERE table_schema = DATABASE()
                     AND table_name = 'api_request_log'
                     AND index_name = 'idx_api_request_user');

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_api_request_user ON api_request_log(user_id)',
    'SELECT "Index idx_api_request_user already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM information_schema.statistics
                     WHERE table_schema = DATABASE()
                     AND table_name = 'api_request_log'
                     AND index_name = 'idx_api_request_time');

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_api_request_time ON api_request_log(request_time)',
    'SELECT "Index idx_api_request_time already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM information_schema.statistics
                     WHERE table_schema = DATABASE()
                     AND table_name = 'api_request_log'
                     AND index_name = 'idx_api_request_id');

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_api_request_id ON api_request_log(request_id)',
    'SELECT "Index idx_api_request_id already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM information_schema.statistics
                     WHERE table_schema = DATABASE()
                     AND table_name = 'api_request_log'
                     AND index_name = 'idx_api_request_remote_addr');

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_api_request_remote_addr ON api_request_log(remote_addr)',
    'SELECT "Index idx_api_request_remote_addr already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Step 3: Create api_auth_failure table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS api_auth_failure (
    id INT AUTO_INCREMENT PRIMARY KEY,
    remote_addr VARCHAR(255) NOT NULL,
    failure_time TIMESTAMP NOT NULL,
    user_agent VARCHAR(512),
    attempted_user VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step 4: Create indexes on api_auth_failure (only if they don't exist)
SET @index_exists = (SELECT COUNT(*) FROM information_schema.statistics
                     WHERE table_schema = DATABASE()
                     AND table_name = 'api_auth_failure'
                     AND index_name = 'idx_api_auth_failure_addr');

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_api_auth_failure_addr ON api_auth_failure(remote_addr)',
    'SELECT "Index idx_api_auth_failure_addr already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (SELECT COUNT(*) FROM information_schema.statistics
                     WHERE table_schema = DATABASE()
                     AND table_name = 'api_auth_failure'
                     AND index_name = 'idx_api_auth_failure_time');

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_api_auth_failure_time ON api_auth_failure(failure_time)',
    'SELECT "Index idx_api_auth_failure_time already exists" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Done!
