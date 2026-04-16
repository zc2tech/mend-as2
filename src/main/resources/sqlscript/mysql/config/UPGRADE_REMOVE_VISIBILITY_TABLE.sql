-- ============================================================================
-- Upgrade Script: Remove partner_user_visibility table
-- ============================================================================
-- This script removes the legacy partner_user_visibility table since we now
-- use ownership-based filtering via the created_by_user_id column in the
-- partner table.
--
-- Run this script on existing installations to clean up the legacy table.
-- ============================================================================

-- Drop the partner_user_visibility table
-- This is safe because we no longer use visibility-based filtering
DROP TABLE IF EXISTS partner_user_visibility;

-- Add index on created_by_user_id for better query performance
-- (using IF NOT EXISTS equivalent for MySQL 5.7+)
CREATE INDEX IF NOT EXISTS idx_partner_created_by_user ON partner(created_by_user_id);
