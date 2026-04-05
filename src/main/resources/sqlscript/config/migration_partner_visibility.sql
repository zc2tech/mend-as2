-- Migration script for partner visibility feature
-- Run this if upgrading from a version without visibility control

-- Create table if not exists
CREATE TABLE IF NOT EXISTS partner_user_visibility(
    id SERIAL PRIMARY KEY,
    partner_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(partner_id) REFERENCES partner(id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES webui_users(id) ON DELETE CASCADE,
    UNIQUE(partner_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_partner_visibility_partner ON partner_user_visibility(partner_id);
CREATE INDEX IF NOT EXISTS idx_partner_visibility_user ON partner_user_visibility(user_id);

-- Grant permissions to as2user (adjust username if different in your setup)
GRANT ALL PRIVILEGES ON TABLE partner_user_visibility TO as2user;
GRANT ALL PRIVILEGES ON SEQUENCE partner_user_visibility_id_seq TO as2user;

-- Add created_by_user_id column to partner table if not exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'partner' AND column_name = 'created_by_user_id'
    ) THEN
        ALTER TABLE partner ADD COLUMN created_by_user_id INTEGER DEFAULT 0;
    END IF;
END $$;

-- By default, all existing remote partners are visible to all users (no entries needed)
-- If you want to restrict access to existing partners, manually insert records
