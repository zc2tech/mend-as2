-- MySQL version: Add payload format and doctype columns to payload table
-- These columns store EDI format (cXML, X12, EDIFACT) and document type (Purchase Order, Invoice, etc.)

-- Add columns if they don't exist (MySQL syntax)
DELIMITER $$

CREATE PROCEDURE add_payload_columns_if_not_exists()
BEGIN
    DECLARE format_col_exists INT DEFAULT 0;
    DECLARE doctype_col_exists INT DEFAULT 0;

    -- Check if payload_format column exists
    SELECT COUNT(*) INTO format_col_exists
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'payload'
      AND column_name = 'payload_format';

    -- Add payload_format if it doesn't exist
    IF format_col_exists = 0 THEN
        ALTER TABLE payload ADD COLUMN payload_format VARCHAR(50);
    END IF;

    -- Check if payload_doctype column exists
    SELECT COUNT(*) INTO doctype_col_exists
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'payload'
      AND column_name = 'payload_doctype';

    -- Add payload_doctype if it doesn't exist
    IF doctype_col_exists = 0 THEN
        ALTER TABLE payload ADD COLUMN payload_doctype VARCHAR(255);
    END IF;
END$$

DELIMITER ;

CALL add_payload_columns_if_not_exists();
DROP PROCEDURE add_payload_columns_if_not_exists;

-- Create index for faster filtering by format
CREATE INDEX IF NOT EXISTS idx_payload_format ON payload(payload_format);
