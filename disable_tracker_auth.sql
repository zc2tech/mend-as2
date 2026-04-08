-- Disable tracker authentication for testing
-- Run this against the runtime database

-- Check current settings
SELECT * FROM preferences WHERE key LIKE 'tracker.%';

-- Disable authentication requirement
UPDATE preferences SET value = 'false' WHERE key = 'tracker.auth.required';

-- Verify
SELECT * FROM preferences WHERE key = 'tracker.auth.required';
