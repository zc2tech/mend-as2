-- ============================================================================
-- Clear User Management System Data
-- ============================================================================
-- This script removes all user management data while preserving the schema.
-- Run this before re-running data.sql to reset to default state.
--
-- Usage:
--   psql -U your_user -d your_db -f clear-data.sql
--   psql -U your_user -d your_db -f data.sql
-- ============================================================================

-- Delete in order to respect foreign key constraints

-- 1. Remove user-role assignments
DELETE FROM webui_user_roles;

-- 2. Remove role-permission assignments
DELETE FROM webui_role_permissions;

-- 3. Remove users
DELETE FROM webui_users;

-- 4. Remove roles
DELETE FROM webui_roles;

-- 5. Remove permissions
DELETE FROM webui_permissions;

-- Reset sequences (if using serial/auto-increment IDs)
-- This ensures IDs start from 1 again
ALTER SEQUENCE IF EXISTS webui_users_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS webui_roles_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS webui_permissions_id_seq RESTART WITH 1;

-- Verification: Show table counts (should all be 0)
SELECT 'webui_users' AS table_name, COUNT(*) AS row_count FROM webui_users
UNION ALL
SELECT 'webui_roles', COUNT(*) FROM webui_roles
UNION ALL
SELECT 'webui_permissions', COUNT(*) FROM webui_permissions
UNION ALL
SELECT 'webui_user_roles', COUNT(*) FROM webui_user_roles
UNION ALL
SELECT 'webui_role_permissions', COUNT(*) FROM webui_role_permissions;
