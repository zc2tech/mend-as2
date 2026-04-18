-- MySQL version: Add payload format and doctype columns to messages table
-- These columns store EDI format (cXML, X12, EDIFACT) and document type (Purchase Order, Invoice, etc.)

-- Add columns if they don't exist (MySQL syntax)
DELIMITER $$

CREATE PROCEDURE add_message_payload_columns_if_not_exists()
BEGIN
    DECLARE format_col_exists INT DEFAULT 0;
    DECLARE doctype_col_exists INT DEFAULT 0;

    -- Check if payloadformat column exists
    SELECT COUNT(*) INTO format_col_exists
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'messages'
      AND column_name = 'payloadformat';

    -- Add payloadformat if it doesn't exist
    IF format_col_exists = 0 THEN
        ALTER TABLE messages ADD COLUMN payloadformat VARCHAR(50);
    END IF;

    -- Check if payloaddoctype column exists
    SELECT COUNT(*) INTO doctype_col_exists
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'messages'
      AND column_name = 'payloaddoctype';

    -- Add payloaddoctype if it doesn't exist
    IF doctype_col_exists = 0 THEN
        ALTER TABLE messages ADD COLUMN payloaddoctype VARCHAR(255);
    END IF;
END$$

DELIMITER ;

CALL add_message_payload_columns_if_not_exists();
DROP PROCEDURE add_message_payload_columns_if_not_exists;

-- Create index for faster filtering by format
CREATE INDEX IF NOT EXISTS idx_message_payloadformat ON messages(payloadformat);
