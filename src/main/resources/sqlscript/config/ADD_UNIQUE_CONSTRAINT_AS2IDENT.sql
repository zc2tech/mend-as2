-- Add unique constraint to partner.as2ident to prevent duplicate AS2 IDs
-- This ensures each partner has a unique AS2 identification

-- First, check if there are any existing duplicates
-- If duplicates exist, this will help identify them:
-- SELECT as2ident, COUNT(*) FROM partner GROUP BY as2ident HAVING COUNT(*) > 1;

-- Add the unique constraint (only if it doesn't exist)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'partner_as2ident_unique'
    ) THEN
        ALTER TABLE partner ADD CONSTRAINT partner_as2ident_unique UNIQUE(as2ident);
    END IF;
END $$;

-- Create index for faster lookups by AS2 ID (if not already exists)
CREATE INDEX IF NOT EXISTS idx_partner_as2ident ON partner(as2ident);
