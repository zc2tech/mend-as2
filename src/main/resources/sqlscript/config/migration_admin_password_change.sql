-- Migration: Force password change for default admin user
-- Purpose: Ensure admin user must change default password on first login
-- Date: 2026-04-07
-- Security: Addresses initial setup security requirement

-- Set must_change_password flag for admin user if password is still the default
-- This only affects admin users who still have the default "admin" password
UPDATE webui_users
SET must_change_password = TRUE
WHERE username = 'admin'
  AND password_hash = '75000#efbfbd5207efbfbd0159efbfbd4befbfbd2befbfbdefbfbd1f22277e#13fbcaadc6706ff58a7666b6fa82dbed'
  AND must_change_password = FALSE;

-- Note: This migration is safe to run multiple times (idempotent)
-- It only affects users with the default password hash who haven't changed their password yet
