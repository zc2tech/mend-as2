-- Migration: Add payload format and doctype columns to payload table
-- These columns store EDI format (cXML, X12, EDIFACT) and document type (Purchase Order, Invoice, etc.)

-- Add columns if they don't exist (PostgreSQL syntax)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='payload' AND column_name='payload_format'
    ) THEN
        ALTER TABLE payload ADD COLUMN payload_format VARCHAR(50);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='payload' AND column_name='payload_doctype'
    ) THEN
        ALTER TABLE payload ADD COLUMN payload_doctype VARCHAR(255);
    END IF;
END$$;

-- Create index for faster filtering by format
CREATE INDEX IF NOT EXISTS idx_payload_format ON payload(payload_format);
