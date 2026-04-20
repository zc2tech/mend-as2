-- Migration: Add IP whitelist tables to config database
-- Run this on existing PostgreSQL databases to add IP whitelist functionality

DO $$
BEGIN
    -- Global IP whitelist for system-wide access control
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'ip_whitelist_global') THEN
        CREATE TABLE ip_whitelist_global (
          id SERIAL PRIMARY KEY,
          ip_pattern VARCHAR(255) NOT NULL,
          description VARCHAR(512),
          target_type VARCHAR(50) NOT NULL,
          enabled BOOLEAN DEFAULT TRUE,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          created_by VARCHAR(255),
          UNIQUE (ip_pattern, target_type)
        );
        CREATE INDEX idx_ip_whitelist_target ON ip_whitelist_global(target_type, enabled);
    END IF;

    -- Partner-specific IP whitelist
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'ip_whitelist_partner') THEN
        CREATE TABLE ip_whitelist_partner (
          id SERIAL PRIMARY KEY,
          partner_id INTEGER NOT NULL,
          ip_pattern VARCHAR(255) NOT NULL,
          description VARCHAR(512),
          enabled BOOLEAN DEFAULT TRUE,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          UNIQUE (partner_id, ip_pattern),
          FOREIGN KEY (partner_id) REFERENCES partner(id) ON DELETE CASCADE
        );
        CREATE INDEX idx_ip_whitelist_partner_id ON ip_whitelist_partner(partner_id, enabled);
    END IF;

    -- User-specific IP whitelist for WebUI/API access
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'ip_whitelist_user') THEN
        CREATE TABLE ip_whitelist_user (
          id SERIAL PRIMARY KEY,
          user_id INTEGER NOT NULL,
          ip_pattern VARCHAR(255) NOT NULL,
          description VARCHAR(512),
          enabled BOOLEAN DEFAULT TRUE,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          UNIQUE (user_id, ip_pattern),
          FOREIGN KEY (user_id) REFERENCES webui_users(id) ON DELETE CASCADE
        );
        CREATE INDEX idx_ip_whitelist_user_id ON ip_whitelist_user(user_id, enabled);
    END IF;

    -- IP whitelist block log for audit trail
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'ip_whitelist_block_log') THEN
        CREATE TABLE ip_whitelist_block_log (
          id SERIAL PRIMARY KEY,
          blocked_ip VARCHAR(255) NOT NULL,
          target_type VARCHAR(50) NOT NULL,
          attempted_user VARCHAR(255),
          attempted_partner VARCHAR(255),
          block_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          user_agent VARCHAR(512),
          request_path VARCHAR(512)
        );
        CREATE INDEX idx_ip_block_log_time ON ip_whitelist_block_log(block_time);
        CREATE INDEX idx_ip_block_log_ip ON ip_whitelist_block_log(blocked_ip);
    END IF;
END$$;

-- Add default settings for IP whitelist (WebUI and API enabled by default)
DO $$
BEGIN
    -- Set WebUI whitelist enabled by default
    IF NOT EXISTS (SELECT 1 FROM serversettings WHERE vkey = 'ip.whitelist.enabled.webui') THEN
        INSERT INTO serversettings (vkey, vvalue) VALUES ('ip.whitelist.enabled.webui', 'true');
    ELSE
        UPDATE serversettings SET vvalue = 'true' WHERE vkey = 'ip.whitelist.enabled.webui';
    END IF;

    -- Set API whitelist enabled by default
    IF NOT EXISTS (SELECT 1 FROM serversettings WHERE vkey = 'ip.whitelist.enabled.api') THEN
        INSERT INTO serversettings (vkey, vvalue) VALUES ('ip.whitelist.enabled.api', 'true');
    ELSE
        UPDATE serversettings SET vvalue = 'true' WHERE vkey = 'ip.whitelist.enabled.api';
    END IF;
END$$;

-- Other IP whitelist settings use code defaults if not already set
-- (ip.whitelist.enabled.as2 = false, ip.whitelist.enabled.tracker = false,
--  ip.whitelist.mode = GLOBAL_AND_SPECIFIC, ip.whitelist.log.retention.days = 30)


