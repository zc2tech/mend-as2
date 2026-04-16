-- Migration: Change system-wide TLS keystore from user_id=0 to user_id=-1
-- This makes it clear that system-wide certificates are not owned by any particular user
--
-- Background:
-- - Previously, system-wide TLS keystores used user_id=0
-- - This implied they belonged to the admin user, which was conceptually incorrect
-- - Now using user_id=-1 to clearly indicate system-wide ownership
--
-- Note: Only migrating TLS keystores (purpose=1)
-- Sign/Encrypt keystores (purpose=2) with user_id=0 may belong to actual admin user

-- Migrate system-wide TLS keystore from user_id=0 to user_id=-1
UPDATE keydata
SET user_id = -1
WHERE user_id = 0
  AND purpose = 1;  -- KEYSTORE_USAGE_TLS = 1

-- Verify migration
-- Expected: 0 rows (no system TLS keystores with user_id=0)
-- SELECT COUNT(*) FROM keydata WHERE user_id = 0 AND purpose = 1;

-- Expected: 1 row (system TLS keystore with user_id=-1)
-- SELECT COUNT(*) FROM keydata WHERE user_id = -1 AND purpose = 1;
