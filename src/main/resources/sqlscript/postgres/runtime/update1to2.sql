-- Migration from runtime database version 1 to 2
-- Add response_body and response_content_type columns to api_request_log table

ALTER TABLE api_request_log
ADD COLUMN response_body BYTEA,
ADD COLUMN response_content_type VARCHAR(255);
