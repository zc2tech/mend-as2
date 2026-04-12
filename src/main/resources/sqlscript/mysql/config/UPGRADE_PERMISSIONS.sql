-- ============================================================================
-- Permission System Upgrade Script (MySQL)
-- Adds 16 new fine-grained permissions for SwingUI
-- Safe to run multiple times - checks for existing permissions
-- ============================================================================

-- System Configuration permissions (per-panel)
INSERT INTO webui_permissions (name, description, category)
SELECT 'SYSTEM_CONFIG_CONNECTIVITY', 'Modify HTTP/HTTPS ports and proxy settings', 'System Configuration'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'SYSTEM_CONFIG_CONNECTIVITY');

INSERT INTO webui_permissions (name, description, category)
SELECT 'SYSTEM_CONFIG_INBOUND_AUTH', 'Configure inbound authentication', 'System Configuration'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'SYSTEM_CONFIG_INBOUND_AUTH');

INSERT INTO webui_permissions (name, description, category)
SELECT 'SYSTEM_CONFIG_DIRECTORIES', 'Change message directories', 'System Configuration'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'SYSTEM_CONFIG_DIRECTORIES');

INSERT INTO webui_permissions (name, description, category)
SELECT 'SYSTEM_CONFIG_MAINTENANCE', 'Configure auto-delete and cleanup settings', 'System Configuration'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'SYSTEM_CONFIG_MAINTENANCE');

INSERT INTO webui_permissions (name, description, category)
SELECT 'SYSTEM_CONFIG_NOTIFICATIONS', 'Configure email notifications', 'System Configuration'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'SYSTEM_CONFIG_NOTIFICATIONS');

INSERT INTO webui_permissions (name, description, category)
SELECT 'SYSTEM_CONFIG_INTERFACE', 'Modify UI preferences', 'System Configuration'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'SYSTEM_CONFIG_INTERFACE');

INSERT INTO webui_permissions (name, description, category)
SELECT 'SYSTEM_CONFIG_LOGGING', 'Configure logging settings', 'System Configuration'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'SYSTEM_CONFIG_LOGGING');

-- System Monitoring permissions
INSERT INTO webui_permissions (name, description, category)
SELECT 'SYSTEM_INFO_READ', 'View HTTP server information', 'System Monitoring'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'SYSTEM_INFO_READ');

INSERT INTO webui_permissions (name, description, category)
SELECT 'SYSTEM_EVENTS_READ', 'View system events', 'System Monitoring'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'SYSTEM_EVENTS_READ');

INSERT INTO webui_permissions (name, description, category)
SELECT 'SYSTEM_LOGS_READ', 'Search server logs', 'System Monitoring'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'SYSTEM_LOGS_READ');

-- Tracker permissions
INSERT INTO webui_permissions (name, description, category)
SELECT 'TRACKER_CONFIG_READ', 'View tracker configuration', 'Tracker'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'TRACKER_CONFIG_READ');

INSERT INTO webui_permissions (name, description, category)
SELECT 'TRACKER_CONFIG_WRITE', 'Modify tracker settings', 'Tracker'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'TRACKER_CONFIG_WRITE');

INSERT INTO webui_permissions (name, description, category)
SELECT 'TRACKER_MESSAGE_READ', 'View tracker messages', 'Tracker'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'TRACKER_MESSAGE_READ');

-- User switching permission
INSERT INTO webui_permissions (name, description, category)
SELECT 'USER_SWITCH', 'Impersonate other users', 'User Management'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'USER_SWITCH');

-- TLS Certificate permissions (separate from sign/crypt)
INSERT INTO webui_permissions (name, description, category)
SELECT 'CERT_TLS_READ', 'View TLS/SSL certificates', 'Certificates - TLS'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'CERT_TLS_READ');

INSERT INTO webui_permissions (name, description, category)
SELECT 'CERT_TLS_WRITE', 'Manage TLS/SSL certificates', 'Certificates - TLS'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM webui_permissions WHERE name = 'CERT_TLS_WRITE');

-- ============================================================================
-- Grant new permissions to ADMIN role
-- ============================================================================

INSERT INTO webui_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM webui_roles r
CROSS JOIN webui_permissions p
WHERE r.name = 'ADMIN'
  AND p.name IN (
    'SYSTEM_CONFIG_CONNECTIVITY', 'SYSTEM_CONFIG_INBOUND_AUTH',
    'SYSTEM_CONFIG_DIRECTORIES', 'SYSTEM_CONFIG_MAINTENANCE',
    'SYSTEM_CONFIG_NOTIFICATIONS', 'SYSTEM_CONFIG_INTERFACE',
    'SYSTEM_CONFIG_LOGGING', 'SYSTEM_INFO_READ',
    'SYSTEM_EVENTS_READ', 'SYSTEM_LOGS_READ',
    'TRACKER_CONFIG_READ', 'TRACKER_CONFIG_WRITE',
    'TRACKER_MESSAGE_READ', 'USER_SWITCH',
    'CERT_TLS_READ', 'CERT_TLS_WRITE'
  )
  AND NOT EXISTS (
    SELECT 1 FROM webui_role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- ============================================================================
-- Grant limited permissions to USER role
-- ============================================================================

INSERT INTO webui_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM webui_roles r
CROSS JOIN webui_permissions p
WHERE r.name = 'USER'
  AND p.name IN (
    'SYSTEM_CONFIG_INTERFACE',  -- Personal UI preferences only
    'SYSTEM_INFO_READ',          -- View server info
    'TRACKER_MESSAGE_READ',      -- View tracker messages
    'CERT_TLS_READ'              -- View TLS certs (read-only)
  )
  AND NOT EXISTS (
    SELECT 1 FROM webui_role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
