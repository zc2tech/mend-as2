-- Add inbound authentication columns to partner table
-- Move inbound auth from system-wide preferences to per-local-station configuration

-- Add columns for inbound authentication (basic auth and certificate auth)
ALTER TABLE partner ADD COLUMN IF NOT EXISTS inbound_auth_mode INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE partner ADD COLUMN IF NOT EXISTS inbound_auth_user VARCHAR(256);
ALTER TABLE partner ADD COLUMN IF NOT EXISTS inbound_auth_password VARCHAR(256);
ALTER TABLE partner ADD COLUMN IF NOT EXISTS inbound_auth_cert_fingerprint VARCHAR(255);

-- Note: inbound_auth_mode is a bitmask:
-- 0 = no authentication required
-- 1 = basic authentication only
-- 2 = certificate authentication only
-- 3 = both basic and certificate authentication (1 + 2)
