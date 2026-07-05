-- PostgreSQL Migration script from version 1 to version 2
-- Adds IP whitelist tables and splits TARGET_API into TARGET_SYS_API and TARGET_USER_API

-- Create IP whitelist tables if they don't exist
-- This handles upgrades from versions that didn't have IP whitelist feature

-- Global IP whitelist for system-wide access control
-- target_type values: AS2, TRACKER, WEBUI, SYS_API, USER_API, ALL
-- SYS_API: REST API for controlling server (/sysapi/v1/*)
-- USER_API: User customized endpoint for receiving API requests (/userapi/*)
CREATE TABLE IF NOT EXISTS ip_whitelist_global (
  id SERIAL PRIMARY KEY,
  ip_pattern VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  target_type VARCHAR(50) NOT NULL,
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(255),
  UNIQUE (ip_pattern, target_type)
);

CREATE INDEX IF NOT EXISTS idx_ip_whitelist_target ON ip_whitelist_global(target_type, enabled);

-- Partner-specific IP whitelist
CREATE TABLE IF NOT EXISTS ip_whitelist_partner (
  id SERIAL PRIMARY KEY,
  partner_id INTEGER NOT NULL,
  ip_pattern VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (partner_id, ip_pattern),
  FOREIGN KEY (partner_id) REFERENCES partner(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ip_whitelist_partner_id ON ip_whitelist_partner(partner_id, enabled);

-- User-specific IP whitelist for WebUI/API access
CREATE TABLE IF NOT EXISTS ip_whitelist_user (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  ip_pattern VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (user_id, ip_pattern),
  FOREIGN KEY (user_id) REFERENCES webui_users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ip_whitelist_user_id ON ip_whitelist_user(user_id, enabled);

-- IP whitelist block log for audit trail
CREATE TABLE IF NOT EXISTS ip_whitelist_block_log (
  id SERIAL PRIMARY KEY,
  blocked_ip VARCHAR(255) NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  attempted_user VARCHAR(255),
  attempted_partner VARCHAR(255),
  block_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  user_agent VARCHAR(512),
  request_path VARCHAR(512)
);

CREATE INDEX IF NOT EXISTS idx_ip_block_log_time ON ip_whitelist_block_log(block_time);
CREATE INDEX IF NOT EXISTS idx_ip_block_log_ip ON ip_whitelist_block_log(blocked_ip);

-- Migration for existing API entries (only if they exist)
-- Step 1: Update existing 'API' target_type to 'SYS_API' in ip_whitelist_global
UPDATE ip_whitelist_global
SET target_type = 'SYS_API',
    description = '[MIGRATED] ' || COALESCE(description, 'Migrated from API to SYS_API')
WHERE target_type = 'API';

-- Step 2: Create duplicate entries for USER_API based on SYS_API entries
-- This ensures existing API whitelist rules apply to both sys and user APIs
INSERT INTO ip_whitelist_global (ip_pattern, description, target_type, enabled, created_at, created_by)
SELECT
    ip_pattern,
    '[AUTO-CREATED] ' || COALESCE(description, 'Auto-created from SYS_API migration'),
    'USER_API' as target_type,
    enabled,
    created_at,
    created_by
FROM ip_whitelist_global
WHERE target_type = 'SYS_API'
ON CONFLICT (ip_pattern, target_type) DO NOTHING;

-- Step 3: Add IP whitelist preference keys
-- Insert new preference keys with default values (will be skipped if they already exist)
INSERT INTO serversettings (vkey, vvalue)
VALUES ('ip.whitelist.enabled.as2', 'false')
ON CONFLICT (vkey) DO NOTHING;

INSERT INTO serversettings (vkey, vvalue)
VALUES ('ip.whitelist.enabled.tracker', 'false')
ON CONFLICT (vkey) DO NOTHING;

INSERT INTO serversettings (vkey, vvalue)
VALUES ('ip.whitelist.enabled.webui', 'true')
ON CONFLICT (vkey) DO NOTHING;

INSERT INTO serversettings (vkey, vvalue)
VALUES ('ip.whitelist.enabled.sysapi', 'true')
ON CONFLICT (vkey) DO NOTHING;

INSERT INTO serversettings (vkey, vvalue)
VALUES ('ip.whitelist.enabled.userapi', 'false')
ON CONFLICT (vkey) DO NOTHING;

INSERT INTO serversettings (vkey, vvalue)
VALUES ('ip.whitelist.mode', 'GLOBAL_AND_SPECIFIC')
ON CONFLICT (vkey) DO NOTHING;

INSERT INTO serversettings (vkey, vvalue)
VALUES ('ip.whitelist.log.retention.days', '30')
ON CONFLICT (vkey) DO NOTHING;

-- Update database version
UPDATE version
SET actualversion = 2,
    updatedate = NOW(),
    updatecomment = 'Added IP whitelist tables and split API into SYS_API/USER_API'
WHERE id = (SELECT MAX(id) FROM version);
