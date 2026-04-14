-- Add certificate fingerprint columns for HTTP authentication
-- These store the client certificate fingerprints used for outbound HTTP authentication

ALTER TABLE partner
ADD COLUMN httpauth_cert_fingerprint_message VARCHAR(255),
ADD COLUMN httpauth_cert_fingerprint_mdn VARCHAR(255);
