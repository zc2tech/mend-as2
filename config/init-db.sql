-- PostgreSQL initialization script for AS2 Server
-- This script creates the required databases for the AS2 server

-- Create config database
CREATE DATABASE as2_db_config OWNER as2user;
GRANT ALL PRIVILEGES ON DATABASE as2_db_config TO as2user;

-- Create runtime database
CREATE DATABASE as2_db_runtime OWNER as2user;
GRANT ALL PRIVILEGES ON DATABASE as2_db_runtime TO as2user;

-- Log completion
\echo 'AS2 databases created successfully'
