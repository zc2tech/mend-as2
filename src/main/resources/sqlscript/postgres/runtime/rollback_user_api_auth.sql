-- ============================================================
-- PostgreSQL Rollback Script - RUNTIME DATABASE
-- Remove API Request Log and Auth Failure Tables
-- WARNING: This will DELETE all API request logs and auth failure data!
-- ============================================================

-- Step 1: Drop runtime tables
DROP TABLE IF EXISTS api_auth_failure CASCADE;
DROP TABLE IF EXISTS api_request_log CASCADE;

-- Step 2: Verify rollback
SELECT
    'Runtime tables check' as status,
    CASE
        WHEN COUNT(*) = 0 THEN 'Runtime tables successfully removed'
        ELSE 'Runtime tables still exist - manual cleanup needed'
    END as table_status
FROM information_schema.tables
WHERE table_name IN ('api_request_log', 'api_auth_failure');

-- Done!
