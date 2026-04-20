-- Upgrade script for multiple inbound authentication credentials
-- Migrates from single credential per partner to multiple credentials

-- Migrate existing single credentials to the new table
-- Only process local stations with authentication enabled
INSERT INTO partner_inbound_auth_credentials(partner_id, auth_type, username, password, cert_fingerprint, enabled)
SELECT
    id as partner_id,
    CASE
        WHEN inbound_auth_mode = 1 THEN 1  -- Basic auth only
        WHEN inbound_auth_mode = 2 THEN 2  -- Certificate auth only
        WHEN inbound_auth_mode = 3 THEN 1  -- Both - create basic auth entry first
    END as auth_type,
    inbound_auth_user as username,
    inbound_auth_password as password,
    CASE
        WHEN inbound_auth_mode = 1 THEN NULL  -- Basic auth only - no cert
        WHEN inbound_auth_mode = 2 THEN inbound_auth_cert_fingerprint  -- Certificate auth only
        WHEN inbound_auth_mode = 3 THEN NULL  -- Both - basic entry doesn't have cert
    END as cert_fingerprint,
    TRUE as enabled
FROM partner
WHERE islocal = 1
  AND inbound_auth_mode > 0
  AND NOT EXISTS (
    SELECT 1 FROM partner_inbound_auth_credentials
    WHERE partner_id = partner.id
  );

-- For mode 3 (both basic AND certificate), create separate certificate entry
INSERT INTO partner_inbound_auth_credentials(partner_id, auth_type, username, password, cert_fingerprint, enabled)
SELECT
    id as partner_id,
    2 as auth_type,  -- Certificate auth
    NULL as username,
    NULL as password,
    inbound_auth_cert_fingerprint as cert_fingerprint,
    TRUE as enabled
FROM partner
WHERE islocal = 1
  AND inbound_auth_mode = 3
  AND inbound_auth_cert_fingerprint IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM partner_inbound_auth_credentials
    WHERE partner_id = partner.id AND auth_type = 2
  );

-- Note: Keep old columns (inbound_auth_mode, inbound_auth_user, inbound_auth_password,
-- inbound_auth_cert_fingerprint) for backward compatibility. They will be removed in a
-- future version after confirming migration success.
