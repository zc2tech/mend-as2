CREATE TABLE version(
    id SERIAL PRIMARY KEY,
    actualversion INTEGER,
    updatedate TIMESTAMP,
    updatecomment VARCHAR(255)
);

CREATE TABLE oauth2(
    id SERIAL PRIMARY KEY,
    authendpoint VARCHAR(1024) NOT NULL,
    authscope VARCHAR(1024) NOT NULL,
    tokenendpoint VARCHAR(1024) NOT NULL,
    clientsecret VARCHAR(1024) NOT NULL,
    clientid VARCHAR(1024) NOT NULL,
    authcode BYTEA,
    accesstoken BYTEA,
    refreshtoken BYTEA,
    accesstokenuntil BIGINT DEFAULT 0 NOT NULL,
    username VARCHAR(256),
    customparamauth VARCHAR(1024),
    pkce INTEGER DEFAULT 0 NOT NULL,
    pkceverifier VARCHAR(128) DEFAULT '_new' NOT NULL
);

-- Keystore data storage - stores certificate keystores in database
-- User-scoped: each user has their own keystores
-- user_id: 0=system/admin, >0=specific user
-- purpose: 1=TLS keystore, 2=ENC/SIGN keystore
-- storagetype: 1=JKS, 2=PKCS12
CREATE TABLE keydata(
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL DEFAULT 0,
    purpose INTEGER NOT NULL,
    storagedata BYTEA,
    storagetype INTEGER,
    lastchanged BIGINT,
    securityprovider VARCHAR(255),
    UNIQUE(user_id, purpose)
);

CREATE TABLE partner(
    id SERIAL PRIMARY KEY,
    as2ident VARCHAR(255),
    partnername VARCHAR(255),
    islocal INTEGER,
    sign INTEGER,
    encrypt INTEGER,
    email VARCHAR(255),
    url VARCHAR(255),
    mdnurl VARCHAR(255),
    msgsubject VARCHAR(255),
    contenttype VARCHAR(255),
    syncmdn INTEGER,
    pollignorelist VARCHAR(1024),
    pollinterval INTEGER,
    msgcompression INTEGER,
    signedmdn INTEGER,
    authmodehttp INTEGER DEFAULT 0 NOT NULL,
    httpauthuser VARCHAR(256),
    httpauthpass VARCHAR(256),
    authmodehttpasynmdn INTEGER DEFAULT 0 NOT NULL,
    httpauthuserasnymdn VARCHAR(256),
    httpauthpassasnymdn VARCHAR(256),
    keeporiginalfilenameonreceipt INTEGER,
    partnercomment TEXT,
    notifysend INTEGER,
    notifyreceive INTEGER,
    notifysendreceive INTEGER,
    notifysendenabled INTEGER,
    notifyreceiveenabled INTEGER,
    notifysendreceiveenabled INTEGER,
    contenttransferencoding INTEGER,
    httpversion VARCHAR(3) DEFAULT '1.1' NOT NULL,
    maxpollfiles INTEGER DEFAULT 100 NOT NULL,
    partnercontact TEXT,
    partneraddress TEXT,
    algidentprotatt INTEGER DEFAULT 1 NOT NULL,
    enabledirpoll INTEGER DEFAULT 1 NOT NULL,
    useoauth2message INTEGER DEFAULT 0 NOT NULL,
    useoauth2mdn INTEGER DEFAULT 0 NOT NULL,
    oauth2idmessage INTEGER,
    oauth2idmdn INTEGER,
    overwritelocalsecurity INTEGER DEFAULT 0 NOT NULL,
    created_by_user_id INTEGER DEFAULT 0,
    inbound_auth_mode INTEGER DEFAULT 0 NOT NULL,
    inbound_auth_user VARCHAR(256),
    inbound_auth_password VARCHAR(256),
    inbound_auth_cert_fingerprint VARCHAR(255),
    FOREIGN KEY(oauth2idmessage) REFERENCES oauth2(id),
    FOREIGN KEY(oauth2idmdn) REFERENCES oauth2(id)
);


CREATE INDEX idx_partner_islocal ON partner(islocal);
CREATE INDEX idx_partner_as2ident ON partner(as2ident);

-- ============================================================================
-- User Management System Schema
-- ============================================================================
-- NOTE: Must be created BEFORE partner_user_visibility and user_preference_http_auth
-- because those tables have foreign keys referencing webui_users(id)

-- Users table - stores WebUI user accounts
CREATE TABLE webui_users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    email VARCHAR(128),
    full_name VARCHAR(128),
    enabled BOOLEAN DEFAULT TRUE,
    must_change_password BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);
CREATE INDEX idx_webui_users_username ON webui_users(username);
CREATE INDEX idx_webui_users_enabled ON webui_users(enabled);

-- Roles table - defines user roles (ADMIN, USER, VIEWER, etc.)
CREATE TABLE webui_roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_webui_roles_name ON webui_roles(name);

-- User-Role mapping - many-to-many relationship
CREATE TABLE webui_user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES webui_users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES webui_roles(id) ON DELETE CASCADE
);
CREATE INDEX idx_webui_user_roles_user_id ON webui_user_roles(user_id);
CREATE INDEX idx_webui_user_roles_role_id ON webui_user_roles(role_id);

-- Permissions table - granular permissions (PARTNER_READ, CERT_WRITE, etc.)
CREATE TABLE webui_permissions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    description VARCHAR(256),
    category VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_webui_permissions_name ON webui_permissions(name);
CREATE INDEX idx_webui_permissions_category ON webui_permissions(category);

-- Role-Permission mapping - many-to-many relationship
CREATE TABLE webui_role_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES webui_roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES webui_permissions(id) ON DELETE CASCADE
);
CREATE INDEX idx_webui_role_permissions_role_id ON webui_role_permissions(role_id);
CREATE INDEX idx_webui_role_permissions_permission_id ON webui_role_permissions(permission_id);

-- Trigger function to auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_webui_users_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to call the function before update
CREATE TRIGGER trigger_update_webui_users_updated_at
    BEFORE UPDATE ON webui_users
    FOR EACH ROW
    EXECUTE FUNCTION update_webui_users_updated_at();

-- ============================================================================
-- Partner-related tables that reference webui_users
-- ============================================================================

-- Partner visibility control for WebUI users
CREATE TABLE partner_user_visibility(
    id SERIAL PRIMARY KEY,
    partner_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(partner_id) REFERENCES partner(id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES webui_users(id) ON DELETE CASCADE,
    UNIQUE(partner_id, user_id)
);

CREATE INDEX idx_partner_visibility_partner ON partner_user_visibility(partner_id);
CREATE INDEX idx_partner_visibility_user ON partner_user_visibility(user_id);

-- User preference for HTTP authentication per partner
CREATE TABLE user_preference_http_auth(
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    partner_id INTEGER NOT NULL,
    use_message_auth BOOLEAN DEFAULT FALSE,
    message_username VARCHAR(256),
    message_password VARCHAR(256),
    use_mdn_auth BOOLEAN DEFAULT FALSE,
    mdn_username VARCHAR(256),
    mdn_password VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES webui_users(id) ON DELETE CASCADE,
    FOREIGN KEY(partner_id) REFERENCES partner(id) ON DELETE CASCADE,
    UNIQUE(user_id, partner_id)
);

CREATE INDEX idx_user_http_auth_user ON user_preference_http_auth(user_id);
CREATE INDEX idx_user_http_auth_partner ON user_preference_http_auth(partner_id);

-- Inbound authentication credentials for system-wide incoming message authentication
CREATE TABLE inbound_auth_credentials(
    id SERIAL PRIMARY KEY,
    auth_type INTEGER NOT NULL,  -- 1=basic auth, 2=certificate auth
    username VARCHAR(255),        -- For basic auth
    password VARCHAR(255),        -- For basic auth
    cert_alias VARCHAR(255),      -- For certificate auth
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_inbound_auth_type CHECK (auth_type IN (1, 2))
);

CREATE INDEX idx_inbound_auth_type ON inbound_auth_credentials(auth_type);

CREATE TABLE partnerevent(
    id SERIAL PRIMARY KEY,
    partnerid INTEGER,
    useonreceipt INTEGER DEFAULT 0 NOT NULL,
    useonsenderror INTEGER DEFAULT 0 NOT NULL,
    useonsendsuccess INTEGER DEFAULT 0 NOT NULL,
    typeonreceipt INTEGER DEFAULT 1 NOT NULL,
    typeonsenderror INTEGER DEFAULT 1 NOT NULL,
    typeonsendsuccess INTEGER DEFAULT 1 NOT NULL,
    parameteronreceipt VARCHAR(2048),
    parameteronsenderror VARCHAR(2048),
    parameteronsendsuccess VARCHAR(2048)
);
CREATE TABLE httpheader(
    id SERIAL PRIMARY KEY,
    partnerid INTEGER,
    headerkey VARCHAR(255),
    headervalue VARCHAR(255)
    -- ,FOREIGN KEY(partnerid) REFERENCES partner(id)
);
CREATE TABLE certificates(
    id SERIAL PRIMARY KEY,
    partnerid INTEGER,
    fingerprintsha1 VARCHAR(255),
    category INTEGER
);
CREATE TABLE partnersystem(
    id SERIAL PRIMARY KEY,
    partnerid INTEGER,
    as2version VARCHAR(10),
    productname VARCHAR(255),
    msgcompression INTEGER DEFAULT 0 NOT NULL,
    ma INTEGER DEFAULT 0 NOT NULL,
    cem INTEGER DEFAULT 0 NOT NULL
);
CREATE TABLE serversettings(
    id SERIAL PRIMARY KEY,
    vkey VARCHAR(256) NOT NULL,
    vvalue VARCHAR(256) NOT NULL
);

CREATE INDEX idx_serversettings_vkey ON serversettings(vkey);

CREATE TABLE notification(
    id SERIAL PRIMARY KEY,
     mailhost VARCHAR(255),
  mailhostport INTEGER,
  notificationemailaddress VARCHAR(255),
  notifycertexpire INTEGER,
  notifytransactionerror INTEGER,
  notifycem INTEGER,
  notifysystemfailure INTEGER,
  replyto VARCHAR(255),
  usesmtpauth INTEGER,
  smtpauthuser VARCHAR(255),
  smtpauthpass VARCHAR(255),
  notifyresend INTEGER,
  security INTEGER,
  maxnotificationspermin INTEGER,
  notifyconnectionproblem INTEGER,
  notifypostprocessing INTEGER,
  usesmtpoauth2 INTEGER,
  smtpoauth2id INTEGER,
  notifyclientserver INTEGER
);

INSERT INTO notification (mailhost, mailhostport, notificationemailaddress, notifycertexpire, notifytransactionerror,
  notifycem, notifysystemfailure, notifypostprocessing, replyto,
  usesmtpauth, smtpauthuser, smtpauthpass, notifyresend, security,
  maxnotificationspermin, notifyconnectionproblem, notifyclientserver, usesmtpoauth2, smtpoauth2id)
VALUES ('smtp.example12345.com', 587, '', 1, 1, 0, 1, 1, '', 1, '', '', 1, 2, 2, 1, 0, 0, 0);

-- INSERT INTO BLOCKS VALUES(0, 2147483647, 0)
INSERT INTO VERSION
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
-- Note: PostgreSQL doesn't support explicit ID in INSERT for SERIAL columns by default
-- We use SELECT to ensure consistent IDs
INSERT INTO webui_roles (name, description)
SELECT 'ADMIN', 'Administrator with full system access - can manage all resources and users'
WHERE NOT EXISTS (SELECT 1 FROM webui_roles WHERE name = 'ADMIN');

INSERT INTO webui_roles (name, description)
SELECT 'USER', 'Regular user with access to own partners, certificates, and messages'
WHERE NOT EXISTS (SELECT 1 FROM webui_roles WHERE name = 'USER');

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

-- Reset sequence to start from 1 for regular users
ALTER SEQUENCE webui_users_id_seq RESTART WITH 1;

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
