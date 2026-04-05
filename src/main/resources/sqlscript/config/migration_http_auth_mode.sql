-- Migration script for HTTP authentication mode feature
-- Run this if upgrading from a version with boolean usehttpauth fields

-- Step 1: Add new auth mode columns
ALTER TABLE partner ADD COLUMN IF NOT EXISTS authmodehttp INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE partner ADD COLUMN IF NOT EXISTS authmodehttpasynmdn INTEGER DEFAULT 0 NOT NULL;

-- Step 2: Migrate existing data (usehttpauth: 0->0, 1->1; usehttpauthasyncmdn: 0->0, 1->1)
UPDATE partner SET authmodehttp = CASE WHEN usehttpauth = 1 THEN 1 ELSE 0 END WHERE authmodehttp = 0;
UPDATE partner SET authmodehttpasynmdn = CASE WHEN usehttpauthasyncmdn = 1 THEN 1 ELSE 0 END WHERE authmodehttpasynmdn = 0;

-- Step 3: Drop old columns (only if they exist)
ALTER TABLE partner DROP COLUMN IF EXISTS usehttpauth;
ALTER TABLE partner DROP COLUMN IF EXISTS usehttpauthasyncmdn;

-- Step 4: Create user preference table
CREATE TABLE IF NOT EXISTS user_preference_http_auth(
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    partner_id INTEGER NOT NULL,
    use_message_auth BOOLEAN DEFAULT FALSE,
    message_username VARCHAR(256),
    message_password VARCHAR(256),
    use_mdn_auth BOOLEAN DEFAULT FALSE,
    mdn_username VARCHAR(256),
    mdn_password VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(user_id) REFERENCES webui_users(id) ON DELETE CASCADE,
    FOREIGN KEY(partner_id) REFERENCES partner(id) ON DELETE CASCADE,
    UNIQUE(user_id, partner_id)
);

CREATE INDEX IF NOT EXISTS idx_user_http_auth_user ON user_preference_http_auth(user_id);
CREATE INDEX IF NOT EXISTS idx_user_http_auth_partner ON user_preference_http_auth(partner_id);

-- Grant permissions
GRANT ALL PRIVILEGES ON TABLE user_preference_http_auth TO as2user;
GRANT ALL PRIVILEGES ON SEQUENCE user_preference_http_auth_id_seq TO as2user;
