-- Migration: Remove payload_format and payload_doctype from payload table
-- These are now stored only in messages table (first payload's format)

DO $$
BEGIN
    -- Drop index if it exists
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_payload_format') THEN
        DROP INDEX idx_payload_format;
    END IF;

    -- Drop columns if they exist
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='payload' AND column_name='payload_format'
    ) THEN
        ALTER TABLE payload DROP COLUMN payload_format;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='payload' AND column_name='payload_doctype'
    ) THEN
        ALTER TABLE payload DROP COLUMN payload_doctype;
    END IF;
END$$;
