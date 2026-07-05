-- MySQL Migration script from version 2 to version 3
-- Adds user API response rules table for dynamic REST API response configuration

-- Create user API response rules table
-- This table stores custom response rules per user for the REST API endpoint
CREATE TABLE IF NOT EXISTS user_api_response_rules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    http_method VARCHAR(10) NOT NULL,
    path_pattern VARCHAR(500) NOT NULL,
    path_match_type VARCHAR(20) NOT NULL DEFAULT 'exact',
    status_code INT NOT NULL DEFAULT 200,
    content_type VARCHAR(100) NOT NULL DEFAULT 'application/json',
    response_body TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_api_response_user FOREIGN KEY (user_id)
        REFERENCES webui_users(id) ON DELETE CASCADE,

    INDEX idx_user_priority (user_id, priority),
    INDEX idx_user_enabled (user_id, enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
