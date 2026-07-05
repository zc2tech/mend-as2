-- ============================================================
-- PostgreSQL Migration Script - RUNTIME DATABASE
-- Add API Request Log and Auth Failure Tables
-- ============================================================

-- Step 1: Create api_request_log table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS api_request_log (
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
    auth_status INTEGER DEFAULT 0,
    auth_user VARCHAR(255),
    request_time TIMESTAMP NOT NULL
);

-- Step 2: Create indexes on api_request_log (only if they don't exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE tablename = 'api_request_log'
        AND indexname = 'idx_api_request_user'
    ) THEN
        CREATE INDEX idx_api_request_user ON api_request_log(user_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE tablename = 'api_request_log'
        AND indexname = 'idx_api_request_time'
    ) THEN
        CREATE INDEX idx_api_request_time ON api_request_log(request_time);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE tablename = 'api_request_log'
        AND indexname = 'idx_api_request_id'
    ) THEN
        CREATE INDEX idx_api_request_id ON api_request_log(request_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE tablename = 'api_request_log'
        AND indexname = 'idx_api_request_remote_addr'
    ) THEN
        CREATE INDEX idx_api_request_remote_addr ON api_request_log(remote_addr);
    END IF;
END $$;

-- Step 3: Create api_auth_failure table (only if it doesn't exist)
CREATE TABLE IF NOT EXISTS api_auth_failure (
    id SERIAL PRIMARY KEY,
    remote_addr VARCHAR(255) NOT NULL,
    failure_time TIMESTAMP NOT NULL,
    user_agent VARCHAR(512),
    attempted_user VARCHAR(255)
);

-- Step 4: Create indexes on api_auth_failure (only if they don't exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE tablename = 'api_auth_failure'
        AND indexname = 'idx_api_auth_failure_addr'
    ) THEN
        CREATE INDEX idx_api_auth_failure_addr ON api_auth_failure(remote_addr);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE tablename = 'api_auth_failure'
        AND indexname = 'idx_api_auth_failure_time'
    ) THEN
        CREATE INDEX idx_api_auth_failure_time ON api_auth_failure(failure_time);
    END IF;
END $$;

-- Done!
