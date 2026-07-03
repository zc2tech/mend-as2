-- ============================================================
-- PostgreSQL Rollback Script - CONFIG DATABASE
-- Remove User-Specific API Authentication Tables and Columns
-- WARNING: This will DELETE all user API auth data!
-- ============================================================

-- Step 1: Drop config table
DROP TABLE IF EXISTS user_api_auth_credentials CASCADE;

-- Step 2: Remove columns from webui_users table
ALTER TABLE webui_users
DROP COLUMN IF EXISTS api_auth_basic_enabled,
DROP COLUMN IF EXISTS api_auth_cert_enabled;

-- Step 3: Verify rollback
SELECT
    'Rollback complete' as status,
    CASE
        WHEN COUNT(*) = 0 THEN 'Config table successfully removed'
        ELSE 'Config table still exists - manual cleanup needed'
    END as table_status
FROM information_schema.tables
WHERE table_name = 'user_api_auth_credentials';

SELECT
    'Column removal complete' as status,
    CASE
        WHEN COUNT(*) = 0 THEN 'Columns successfully removed'
        ELSE 'Columns still exist - manual cleanup needed'
    END as column_status
FROM information_schema.columns
WHERE table_name = 'webui_users'
AND column_name IN ('api_auth_basic_enabled', 'api_auth_cert_enabled');

-- Done!
