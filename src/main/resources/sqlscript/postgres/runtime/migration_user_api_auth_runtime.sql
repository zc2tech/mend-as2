-- ============================================================
-- PostgreSQL Migration Script - RUNTIME DATABASE
-- Add API Request Log and Auth Failure Tables
-- ============================================================

-- Step 1: Create api_request_log table
CREATE TABLE api_request_log (
    id SERIAL PRIMARY KEY,
    request_id VARCHAR(255) UNIQUE NOT NULL,
    user_id INTEGER NOT NULL,
    remote_addr VARCHAR(255),
    user_agent VARCHAR(512),
    http_method VARCHAR(10) NOT NULL,
    request_path VARCHAR(512) NOT NULL,
    request_headers TEXT,
    request_body BYTEA,
    content_type VARCHAR(255),
    content_size INTEGER NOT NULL,
    response_status INTEGER NOT NULL,
    auth_status INTEGER DEFAULT 0,    -- 0=none, 1=success, 2=failed
    auth_user VARCHAR(255),
    request_time TIMESTAMP NOT NULL
);

-- Step 2: Create indexes on api_request_log
CREATE INDEX idx_api_request_user ON api_request_log(user_id);
CREATE INDEX idx_api_request_time ON api_request_log(request_time);
CREATE INDEX idx_api_request_id ON api_request_log(request_id);
CREATE INDEX idx_api_request_remote_addr ON api_request_log(remote_addr);

-- Step 3: Create api_auth_failure table
CREATE TABLE api_auth_failure (
    id SERIAL PRIMARY KEY,
    remote_addr VARCHAR(255) NOT NULL,
    failure_time TIMESTAMP NOT NULL,
    user_agent VARCHAR(512),
    attempted_user VARCHAR(255)
);

-- Step 4: Create indexes on api_auth_failure
CREATE INDEX idx_api_auth_failure_addr ON api_auth_failure(remote_addr);
CREATE INDEX idx_api_auth_failure_time ON api_auth_failure(failure_time);

-- Step 5: Verify the changes
SELECT
    'api_request_log table created' as status,
    COUNT(*) as table_exists
FROM information_schema.tables
WHERE table_name = 'api_request_log';

SELECT
    'api_auth_failure table created' as status,
    COUNT(*) as table_exists
FROM information_schema.tables
WHERE table_name = 'api_auth_failure';

-- Done!
