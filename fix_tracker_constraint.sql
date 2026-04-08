-- Fix tracker_message table foreign key constraint
-- This removes the foreign key constraint that was preventing tracker messages from being inserted
-- Tracker messages are independent entities and don't need to reference the messages table

ALTER TABLE tracker_message DROP CONSTRAINT IF EXISTS tracker_message_messageid_fkey;
