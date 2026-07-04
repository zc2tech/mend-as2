-- Migration script from version 1 to version 2
-- Adds IP whitelist tables and splits TARGET_API into TARGET_SYS_API and TARGET_USER_API

-- Create IP whitelist tables if they don't exist
-- This handles upgrades from versions that didn't have IP whitelist feature

-- Global IP whitelist for system-wide access control
-- target_type values: AS2, TRACKER, WEBUI, SYS_API, USER_API, ALL
-- SYS_API: REST API for controlling server (/sysapi/v1/*)
-- USER_API: User customized endpoint for receiving API requests (/userapi/*)
CREATE TABLE IF NOT EXISTS ip_whitelist_global (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ip_pattern VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  target_type VARCHAR(50) NOT NULL,
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(255),
  UNIQUE KEY unique_ip_target (ip_pattern, target_type),
  INDEX idx_ip_whitelist_target (target_type, enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Partner-specific IP whitelist
CREATE TABLE IF NOT EXISTS ip_whitelist_partner (
  id INT AUTO_INCREMENT PRIMARY KEY,
  partner_id INT NOT NULL,
  ip_pattern VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_partner_ip (partner_id, ip_pattern),
  INDEX idx_ip_whitelist_partner_id (partner_id, enabled),
  FOREIGN KEY (partner_id) REFERENCES partner(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User-specific IP whitelist for WebUI/API access
CREATE TABLE IF NOT EXISTS ip_whitelist_user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  ip_pattern VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_user_ip (user_id, ip_pattern),
  INDEX idx_ip_whitelist_user_id (user_id, enabled),
  FOREIGN KEY (user_id) REFERENCES webui_users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- IP whitelist block log for audit trail
CREATE TABLE IF NOT EXISTS ip_whitelist_block_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  blocked_ip VARCHAR(255) NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  attempted_user VARCHAR(255),
  attempted_partner VARCHAR(255),
  block_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  user_agent VARCHAR(512),
  request_path VARCHAR(512),
  INDEX idx_ip_block_log_time (block_time),
  INDEX idx_ip_block_log_ip (blocked_ip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Migration for existing API entries (only if they exist)
-- Step 1: Update existing 'API' target_type to 'SYS_API' in ip_whitelist_global
UPDATE ip_whitelist_global
SET target_type = 'SYS_API',
    description = CONCAT('[MIGRATED] ', COALESCE(description, 'Migrated from API to SYS_API'))
WHERE target_type = 'API';

-- Step 2: Create duplicate entries for USER_API based on SYS_API entries
-- This ensures existing API whitelist rules apply to both sys and user APIs
INSERT IGNORE INTO ip_whitelist_global (ip_pattern, description, target_type, enabled, created_at, created_by)
SELECT
    ip_pattern,
    CONCAT('[AUTO-CREATED] ', COALESCE(description, 'Auto-created from SYS_API migration')),
    'USER_API' as target_type,
    enabled,
    created_at,
    created_by
FROM ip_whitelist_global
WHERE target_type = 'SYS_API';

-- Step 3: Add IP whitelist preference keys
-- Insert new preference keys with default values (will be skipped if they already exist)
INSERT IGNORE INTO serversettings (vkey, vvalue) VALUES ('ip.whitelist.enabled.as2', 'false');
INSERT IGNORE INTO serversettings (vkey, vvalue) VALUES ('ip.whitelist.enabled.tracker', 'false');
INSERT IGNORE INTO serversettings (vkey, vvalue) VALUES ('ip.whitelist.enabled.webui', 'true');
INSERT IGNORE INTO serversettings (vkey, vvalue) VALUES ('ip.whitelist.enabled.sysapi', 'true');
INSERT IGNORE INTO serversettings (vkey, vvalue) VALUES ('ip.whitelist.enabled.userapi', 'false');
INSERT IGNORE INTO serversettings (vkey, vvalue) VALUES ('ip.whitelist.mode', 'GLOBAL_AND_SPECIFIC');
INSERT IGNORE INTO serversettings (vkey, vvalue) VALUES ('ip.whitelist.log.retention.days', '30');
