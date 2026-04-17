-- Add created_by_user_id column to keydata table
-- This separates "where certificate is stored" (user_id) from "who created it" (created_by_user_id)

-- Add the column (allow NULL temporarily for migration)
ALTER TABLE keydata ADD COLUMN created_by_user_id INTEGER NULL;

-- For existing data, set created_by_user_id same as user_id
-- (we don't know who actually created old certificates, so assume owner = creator)
UPDATE keydata SET created_by_user_id = user_id WHERE created_by_user_id IS NULL;

-- Now make it NOT NULL with default value
ALTER TABLE keydata ALTER COLUMN created_by_user_id SET NOT NULL;
ALTER TABLE keydata ALTER COLUMN created_by_user_id SET DEFAULT 1;

-- Add comments
COMMENT ON COLUMN keydata.user_id IS 'Keystore location: -1=system-wide, 1+=user-specific';
COMMENT ON COLUMN keydata.created_by_user_id IS 'Who created this certificate (for auditing)';
