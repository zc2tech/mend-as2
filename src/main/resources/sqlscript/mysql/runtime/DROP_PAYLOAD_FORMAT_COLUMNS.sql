-- Migration: Remove payload_format and payload_doctype from payload table
-- These are now stored only in messages table (first payload's format)

-- Drop index first
DROP INDEX IF EXISTS idx_payload_format ON payload;

-- Drop columns
ALTER TABLE payload DROP COLUMN IF EXISTS payload_format;
ALTER TABLE payload DROP COLUMN IF EXISTS payload_doctype;
