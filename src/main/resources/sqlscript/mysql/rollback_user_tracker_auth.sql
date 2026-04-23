-- ============================================================
-- MySQL Rollback Script
-- Remove User-Specific Tracker Authentication Tables and Columns
-- WARNING: This will DELETE all user tracker auth data!
-- ============================================================

-- Step 1: Drop the credentials table (this will cascade delete all credentials)
DROP TABLE IF EXISTS user_tracker_auth_credentials;

-- Step 2: Remove columns from webui_users table
ALTER TABLE webui_users
DROP COLUMN tracker_auth_basic_enabled,
DROP COLUMN tracker_auth_cert_enabled;

-- Step 3: Verify rollback
SELECT
    'Rollback complete' as status,
    CASE
        WHEN COUNT(*) = 0 THEN 'Table successfully removed'
        ELSE 'Table still exists - manual cleanup needed'
    END as table_status
FROM information_schema.tables
WHERE table_schema = DATABASE()
AND table_name = 'user_tracker_auth_credentials';

SELECT
    'Column removal complete' as status,
    CASE
        WHEN COUNT(*) = 0 THEN 'Columns successfully removed'
        ELSE 'Columns still exist - manual cleanup needed'
    END as column_status
FROM information_schema.columns
WHERE table_schema = DATABASE()
AND table_name = 'webui_users'
AND column_name IN ('tracker_auth_basic_enabled', 'tracker_auth_cert_enabled');

-- Done!
