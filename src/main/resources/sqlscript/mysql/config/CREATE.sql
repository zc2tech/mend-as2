-- MySQL/MariaDB Config Database Schema
-- Converted from PostgreSQL version

CREATE TABLE version(
    id INT AUTO_INCREMENT PRIMARY KEY,
    actualversion INT,
    updatedate TIMESTAMP,
    updatecomment VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE oauth2(
    id INT AUTO_INCREMENT PRIMARY KEY,
    authendpoint VARCHAR(1024) NOT NULL,
    authscope VARCHAR(1024) NOT NULL,
    tokenendpoint VARCHAR(1024) NOT NULL,
    clientsecret VARCHAR(1024) NOT NULL,
    clientid VARCHAR(1024) NOT NULL,
    authcode LONGBLOB,
    accesstoken LONGBLOB,
    refreshtoken LONGBLOB,
    accesstokenuntil BIGINT DEFAULT 0 NOT NULL,
    username VARCHAR(256),
    customparamauth VARCHAR(1024),
    pkce INT DEFAULT 0 NOT NULL,
    pkceverifier VARCHAR(128) DEFAULT '_new' NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Keystore data storage - stores certificate keystores in database
-- User-scoped: each user has their own keystores
-- user_id: 0=system/admin, >0=specific user
-- purpose: 1=TLS keystore, 2=ENC/SIGN keystore
-- storagetype: 1=JKS, 2=PKCS12
CREATE TABLE keydata(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL DEFAULT 0,
    purpose INT NOT NULL,
    storagedata LONGBLOB,
    storagetype INT,
    lastchanged BIGINT,
    securityprovider VARCHAR(255),
    UNIQUE KEY unique_user_purpose (user_id, purpose)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE partner(
    id INT AUTO_INCREMENT PRIMARY KEY,
    as2ident VARCHAR(255),
    partnername VARCHAR(255),
    islocal INT,
    sign INT,
    encrypt INT,
    email VARCHAR(255),
    url VARCHAR(255),
    mdnurl VARCHAR(255),
    msgsubject VARCHAR(255),
    contenttype VARCHAR(255),
    syncmdn INT,
    pollignorelist VARCHAR(1024),
    pollinterval INT,
    msgcompression INT,
    signedmdn INT,
    authmodehttp INT DEFAULT 0 NOT NULL,
    httpauthuser VARCHAR(256),
    httpauthpass VARCHAR(256),
    httpauth_cert_fingerprint_message VARCHAR(255),
    authmodehttpasynmdn INT DEFAULT 0 NOT NULL,
    httpauthuserasnymdn VARCHAR(256),
    httpauthpassasnymdn VARCHAR(256),
    httpauth_cert_fingerprint_mdn VARCHAR(255),
    keeporiginalfilenameonreceipt INT,
    partnercomment TEXT,
    notifysend INT,
    notifyreceive INT,
    notifysendreceive INT,
    notifysendenabled INT,
    notifyreceiveenabled INT,
    notifysendreceiveenabled INT,
    contenttransferencoding INT,
    httpversion VARCHAR(3) DEFAULT '1.1' NOT NULL,
    maxpollfiles INT DEFAULT 100 NOT NULL,
    partnercontact TEXT,
    partneraddress TEXT,
    algidentprotatt INT DEFAULT 1 NOT NULL,
    enabledirpoll INT DEFAULT 1 NOT NULL,
    useoauth2message INT DEFAULT 0 NOT NULL,
    useoauth2mdn INT DEFAULT 0 NOT NULL,
    oauth2idmessage INT,
    oauth2idmdn INT,
    overwritelocalsecurity INT DEFAULT 0 NOT NULL,
    created_by_user_id INT DEFAULT 0,
    inbound_auth_mode INT DEFAULT 0 NOT NULL,
    inbound_auth_user VARCHAR(256),
    inbound_auth_password VARCHAR(256),
    inbound_auth_cert_fingerprint VARCHAR(255),
    FOREIGN KEY(oauth2idmessage) REFERENCES oauth2(id),
    FOREIGN KEY(oauth2idmdn) REFERENCES oauth2(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_partner_islocal ON partner(islocal);
CREATE INDEX idx_partner_as2ident ON partner(as2ident);

-- ============================================================================
-- User Management System Schema
-- ============================================================================
-- NOTE: Must be created BEFORE partner_user_visibility and user_preference_http_auth
-- because those tables have foreign keys referencing webui_users(id)

-- Users table - stores WebUI user accounts
CREATE TABLE webui_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    email VARCHAR(128),
    full_name VARCHAR(128),
    enabled BOOLEAN DEFAULT TRUE,
    must_change_password BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_webui_users_username ON webui_users(username);
CREATE INDEX idx_webui_users_enabled ON webui_users(enabled);

-- Roles table - defines user roles (ADMIN, USER, VIEWER, etc.)
CREATE TABLE webui_roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_webui_roles_name ON webui_roles(name);

-- User-Role mapping - many-to-many relationship
CREATE TABLE webui_user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES webui_users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES webui_roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_webui_user_roles_user_id ON webui_user_roles(user_id);
CREATE INDEX idx_webui_user_roles_role_id ON webui_user_roles(role_id);

-- Permissions table - granular permissions (PARTNER_READ, CERT_WRITE, etc.)
CREATE TABLE webui_permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(256),
    category VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_webui_permissions_name ON webui_permissions(name);
CREATE INDEX idx_webui_permissions_category ON webui_permissions(category);

-- Role-Permission mapping - many-to-many relationship
CREATE TABLE webui_role_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES webui_roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES webui_permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_webui_role_permissions_role_id ON webui_role_permissions(role_id);
CREATE INDEX idx_webui_role_permissions_permission_id ON webui_role_permissions(permission_id);

-- MySQL: Auto-update trigger is built into "ON UPDATE CURRENT_TIMESTAMP" above
-- No need for separate trigger function like PostgreSQL

-- ============================================================================
-- Partner-related tables that reference webui_users
-- ============================================================================

-- Partner visibility control for WebUI users
CREATE TABLE partner_user_visibility(
    id INT AUTO_INCREMENT PRIMARY KEY,
    partner_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(partner_id) REFERENCES partner(id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES webui_users(id) ON DELETE CASCADE,
    UNIQUE(partner_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_partner_visibility_partner ON partner_user_visibility(partner_id);
CREATE INDEX idx_partner_visibility_user ON partner_user_visibility(user_id);

-- User preference for HTTP authentication per partner
CREATE TABLE user_preference_http_auth(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    partner_id INT NOT NULL,
    use_message_auth BOOLEAN DEFAULT FALSE,
    message_username VARCHAR(256),
    message_password VARCHAR(256),
    use_mdn_auth BOOLEAN DEFAULT FALSE,
    mdn_username VARCHAR(256),
    mdn_password VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES webui_users(id) ON DELETE CASCADE,
    FOREIGN KEY(partner_id) REFERENCES partner(id) ON DELETE CASCADE,
    UNIQUE(user_id, partner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_user_http_auth_user ON user_preference_http_auth(user_id);
CREATE INDEX idx_user_http_auth_partner ON user_preference_http_auth(partner_id);

-- Inbound authentication credentials for system-wide incoming message authentication
CREATE TABLE inbound_auth_credentials(
    id INT AUTO_INCREMENT PRIMARY KEY,
    auth_type INT NOT NULL,  -- 1=basic auth, 2=certificate auth
    username VARCHAR(255),        -- For basic auth
    password VARCHAR(255),        -- For basic auth
    cert_alias VARCHAR(255),      -- For certificate auth
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_inbound_auth_type CHECK (auth_type IN (1, 2))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_inbound_auth_type ON inbound_auth_credentials(auth_type);

CREATE TABLE partnerevent(
    id INT AUTO_INCREMENT PRIMARY KEY,
    partnerid INT,
    useonreceipt INT DEFAULT 0 NOT NULL,
    useonsenderror INT DEFAULT 0 NOT NULL,
    useonsendsuccess INT DEFAULT 0 NOT NULL,
    typeonreceipt INT DEFAULT 1 NOT NULL,
    typeonsenderror INT DEFAULT 1 NOT NULL,
    typeonsendsuccess INT DEFAULT 1 NOT NULL,
    parameteronreceipt VARCHAR(2048),
    parameteronsenderror VARCHAR(2048),
    parameteronsendsuccess VARCHAR(2048)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE httpheader(
    id INT AUTO_INCREMENT PRIMARY KEY,
    partnerid INT,
    headerkey VARCHAR(255),
    headervalue VARCHAR(255)
    -- ,FOREIGN KEY(partnerid) REFERENCES partner(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE certificates(
    id INT AUTO_INCREMENT PRIMARY KEY,
    partnerid INT,
    fingerprintsha1 VARCHAR(255),
    category INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE partnersystem(
    id INT AUTO_INCREMENT PRIMARY KEY,
    partnerid INT,
    as2version VARCHAR(10),
    productname VARCHAR(255),
    msgcompression INT DEFAULT 0 NOT NULL,
    ma INT DEFAULT 0 NOT NULL,
    cem INT DEFAULT 0 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE serversettings(
    id INT AUTO_INCREMENT PRIMARY KEY,
    vkey VARCHAR(256) NOT NULL,
    vvalue VARCHAR(256) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_serversettings_vkey ON serversettings(vkey);

CREATE TABLE notification(
    id INT AUTO_INCREMENT PRIMARY KEY,
     mailhost VARCHAR(255),
  mailhostport INT,
  notificationemailaddress VARCHAR(255),
  notifycertexpire INT,
  notifytransactionerror INT,
  notifycem INT,
  notifysystemfailure INT,
  replyto VARCHAR(255),
  usesmtpauth INT,
  smtpauthuser VARCHAR(255),
  smtpauthpass VARCHAR(255),
  notifyresend INT,
  security INT,
  maxnotificationspermin INT,
  notifyconnectionproblem INT,
  notifypostprocessing INT,
  usesmtpoauth2 INT,
  smtpoauth2id INT,
  notifyclientserver INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO notification (mailhost, mailhostport, notificationemailaddress, notifycertexpire, notifytransactionerror,
  notifycem, notifysystemfailure, notifypostprocessing, replyto,
  usesmtpauth, smtpauthuser, smtpauthpass, notifyresend, security,
  maxnotificationspermin, notifyconnectionproblem, notifyclientserver, usesmtpoauth2, smtpoauth2id)
VALUES ('smtp.example12345.com', 587, '', 1, 1, 0, 1, 1, '', 1, '', '', 1, 2, 2, 1, 0, 0, 0);

INSERT INTO version
VALUES(
    0,
    0,
    '2025-05-23 09:47:07.544000',
    'mend-as2'
);

-- ============================================================================
-- User Management System - Default Data
-- ============================================================================

-- Default Roles (simplified to ADMIN and USER only)
INSERT INTO webui_roles (id, name, description) VALUES
(1, 'ADMIN', 'Administrator with full system access - can manage all resources and users'),
(2, 'USER', 'Regular user with access to own partners, certificates, and messages');

-- Default Permissions (kept for future extensibility, but currently not enforced)
INSERT INTO webui_permissions (name, description, category) VALUES
-- Partner permissions
('PARTNER_READ', 'View partners', 'Partners'),
('PARTNER_WRITE', 'Create/modify/delete partners', 'Partners'),

-- Certificate permissions
('CERT_READ', 'View certificates', 'Certificates'),
('CERT_WRITE', 'Import/export/generate/delete certificates', 'Certificates'),

-- Message permissions
('MESSAGE_READ', 'View messages', 'Messages'),
('MESSAGE_WRITE', 'Send/delete messages', 'Messages'),

-- System permissions
('SYSTEM_READ', 'View system info and logs', 'System'),
('SYSTEM_WRITE', 'Modify system settings', 'System'),

-- User management permissions
('USER_MANAGE', 'Manage users and roles', 'Administration'),

-- System Configuration permissions (per-panel)
('SYSTEM_CONFIG_CONNECTIVITY', 'Modify HTTP/HTTPS ports and proxy settings', 'System Configuration'),
('SYSTEM_CONFIG_INBOUND_AUTH', 'Configure inbound authentication', 'System Configuration'),
('SYSTEM_CONFIG_DIRECTORIES', 'Change message directories', 'System Configuration'),
('SYSTEM_CONFIG_MAINTENANCE', 'Configure auto-delete and cleanup settings', 'System Configuration'),
('SYSTEM_CONFIG_NOTIFICATIONS', 'Configure email notifications', 'System Configuration'),
('SYSTEM_CONFIG_INTERFACE', 'Modify UI preferences', 'System Configuration'),
('SYSTEM_CONFIG_LOGGING', 'Configure logging settings', 'System Configuration'),

-- System Monitoring permissions
('SYSTEM_INFO_READ', 'View HTTP server information', 'System Monitoring'),
('SYSTEM_EVENTS_READ', 'View system events', 'System Monitoring'),
('SYSTEM_LOGS_READ', 'Search server logs', 'System Monitoring'),

-- Tracker permissions
('TRACKER_CONFIG_READ', 'View tracker configuration', 'Tracker'),
('TRACKER_CONFIG_WRITE', 'Modify tracker settings', 'Tracker'),
('TRACKER_MESSAGE_READ', 'View tracker messages', 'Tracker'),

-- User switching permission
('USER_SWITCH', 'Impersonate other users', 'User Management'),

-- TLS Certificate permissions (separate from sign/crypt)
('CERT_TLS_READ', 'View TLS/SSL certificates', 'Certificates - TLS'),
('CERT_TLS_WRITE', 'Manage TLS/SSL certificates', 'Certificates - TLS');

-- Default admin user (password: "admin" - MUST be changed on first login)
-- Explicitly set id=0 for admin to match code convention that userId=0 means admin
INSERT INTO webui_users (id, username, password_hash, full_name, enabled, must_change_password) VALUES
(0, 'admin', '75000#efbfbd5207efbfbd0159efbfbd4befbfbd2befbfbdefbfbd1f22277e#13fbcaadc6706ff58a7666b6fa82dbed', 'System Administrator', TRUE, TRUE);

-- Reset auto_increment to start from 1 for regular users
ALTER TABLE webui_users AUTO_INCREMENT = 1;

-- Grant ALL permissions to ADMIN role
INSERT INTO webui_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM webui_roles r
CROSS JOIN webui_permissions p
WHERE r.name = 'ADMIN';

-- Grant basic permissions to USER role
INSERT INTO webui_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM webui_roles r
CROSS JOIN webui_permissions p
WHERE r.name = 'USER'
  AND p.name IN ('PARTNER_READ', 'PARTNER_WRITE', 'CERT_READ', 'CERT_WRITE',
                 'MESSAGE_READ', 'MESSAGE_WRITE', 'TRACKER_MESSAGE_READ');

-- Assign ADMIN role to default admin user
INSERT INTO webui_user_roles (user_id, role_id)
SELECT u.id, r.id
FROM webui_users u
CROSS JOIN webui_roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN';
