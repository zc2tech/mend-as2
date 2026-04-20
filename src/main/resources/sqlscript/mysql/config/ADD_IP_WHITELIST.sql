-- Migration: Add IP whitelist tables to config database
-- Run this on existing databases to add IP whitelist functionality

-- Global IP whitelist for system-wide access control
CREATE TABLE IF NOT EXISTS ip_whitelist_global (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ip_pattern VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  target_type VARCHAR(50) NOT NULL,
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(255),
  UNIQUE KEY unique_ip_target (ip_pattern, target_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IF NOT EXISTS idx_ip_whitelist_target ON ip_whitelist_global(target_type, enabled);

-- Partner-specific IP whitelist
CREATE TABLE IF NOT EXISTS ip_whitelist_partner (
  id INT AUTO_INCREMENT PRIMARY KEY,
  partner_id INT NOT NULL,
  ip_pattern VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_partner_ip (partner_id, ip_pattern),
  FOREIGN KEY (partner_id) REFERENCES partner(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IF NOT EXISTS idx_ip_whitelist_partner_id ON ip_whitelist_partner(partner_id, enabled);

-- User-specific IP whitelist for WebUI/API access
CREATE TABLE IF NOT EXISTS ip_whitelist_user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  ip_pattern VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_user_ip (user_id, ip_pattern),
  FOREIGN KEY (user_id) REFERENCES webui_users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IF NOT EXISTS idx_ip_whitelist_user_id ON ip_whitelist_user(user_id, enabled);

-- IP whitelist block log for audit trail
CREATE TABLE IF NOT EXISTS ip_whitelist_block_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  blocked_ip VARCHAR(255) NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  attempted_user VARCHAR(255),
  attempted_partner VARCHAR(255),
  block_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  user_agent VARCHAR(512),
  request_path VARCHAR(512)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX IF NOT EXISTS idx_ip_block_log_time ON ip_whitelist_block_log(block_time);
CREATE INDEX IF NOT EXISTS idx_ip_block_log_ip ON ip_whitelist_block_log(blocked_ip);

-- Add default settings for IP whitelist (WebUI and API enabled by default)
INSERT INTO serversettings (vkey, vvalue)
VALUES ('ip.whitelist.enabled.webui', 'true')
ON DUPLICATE KEY UPDATE vvalue = 'true';

INSERT INTO serversettings (vkey, vvalue)
VALUES ('ip.whitelist.enabled.api', 'true')
ON DUPLICATE KEY UPDATE vvalue = 'true';

-- Other IP whitelist settings use code defaults if not already set
-- (ip.whitelist.enabled.as2 = false, ip.whitelist.enabled.tracker = false,
--  ip.whitelist.mode = GLOBAL_AND_SPECIFIC, ip.whitelist.log.retention.days = 30)

