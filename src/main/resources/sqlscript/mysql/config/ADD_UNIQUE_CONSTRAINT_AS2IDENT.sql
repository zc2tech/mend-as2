-- MySQL version: Add unique constraint to partner.as2ident to prevent duplicate AS2 IDs
-- This ensures each partner has a unique AS2 identification

-- First, check if there are any existing duplicates
-- If duplicates exist, this will help identify them:
-- SELECT as2ident, COUNT(*) FROM partner GROUP BY as2ident HAVING COUNT(*) > 1;

-- Add the unique constraint (only if it doesn't exist)
-- MySQL doesn't support IF NOT EXISTS for constraints, so we use a procedure
DELIMITER $$

CREATE PROCEDURE add_unique_constraint_if_not_exists()
BEGIN
    DECLARE constraint_exists INT DEFAULT 0;

    -- Check if constraint already exists
    SELECT COUNT(*) INTO constraint_exists
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'partner'
      AND CONSTRAINT_NAME = 'partner_as2ident_unique'
      AND CONSTRAINT_TYPE = 'UNIQUE';

    -- Add constraint if it doesn't exist
    IF constraint_exists = 0 THEN
        ALTER TABLE partner ADD CONSTRAINT partner_as2ident_unique UNIQUE(as2ident);
    END IF;
END$$

DELIMITER ;

CALL add_unique_constraint_if_not_exists();
DROP PROCEDURE add_unique_constraint_if_not_exists;

-- Index already exists from CREATE.sql, but add if not exists
-- MySQL 5.7+ supports CREATE INDEX IF NOT EXISTS (MariaDB 10.1.4+)
CREATE INDEX IF NOT EXISTS idx_partner_as2ident ON partner(as2ident);
