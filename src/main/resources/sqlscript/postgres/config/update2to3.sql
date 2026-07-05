-- PostgreSQL Migration script from version 2 to version 3
-- Adds user API response rules table for dynamic REST API response configuration

-- Create user API response rules table
-- This table stores custom response rules per user for the REST API endpoint
CREATE TABLE IF NOT EXISTS user_api_response_rules (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    priority INTEGER NOT NULL DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    http_method VARCHAR(10) NOT NULL,
    path_pattern VARCHAR(500) NOT NULL,
    path_match_type VARCHAR(20) NOT NULL DEFAULT 'exact',
    status_code INTEGER NOT NULL DEFAULT 200,
    content_type VARCHAR(100) NOT NULL DEFAULT 'application/json',
    response_body TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES webui_users(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX IF NOT EXISTS idx_user_priority ON user_api_response_rules(user_id, priority);
CREATE INDEX IF NOT EXISTS idx_user_enabled ON user_api_response_rules(user_id, enabled);

-- Create trigger for updated_at timestamp (PostgreSQL doesn't have ON UPDATE CURRENT_TIMESTAMP)
CREATE OR REPLACE FUNCTION update_user_api_response_rules_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_user_api_response_rules_updated_at ON user_api_response_rules;
CREATE TRIGGER trigger_update_user_api_response_rules_updated_at
    BEFORE UPDATE ON user_api_response_rules
    FOR EACH ROW
    EXECUTE FUNCTION update_user_api_response_rules_updated_at();

-- Update database version
UPDATE version
SET actualversion = 3,
    updatedate = NOW(),
    updatecomment = 'Added user_api_response_rules table for dynamic REST API responses'
WHERE id = (SELECT MAX(id) FROM version);
