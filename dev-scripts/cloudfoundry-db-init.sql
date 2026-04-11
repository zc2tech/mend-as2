-- CloudFoundry Database Initialization Script
-- Run this inside the CloudFoundry container via cf ssh

-- Connect to the default database first
-- psql $DATABASE_URL

-- Create AS2 user (might already exist if using the default user from service)
-- CREATE USER as2user WITH PASSWORD 'your-password';

-- Create config database
CREATE DATABASE as2_db_config;

-- Create runtime database
CREATE DATABASE as2_db_runtime;

-- Grant privileges to the service user
GRANT ALL PRIVILEGES ON DATABASE as2_db_config TO "245a50905e66";
GRANT ALL PRIVILEGES ON DATABASE as2_db_runtime TO "245a50905e66";

-- Note: The service user (245a50905e66) will be used by the AS2 server
-- You don't need to create a separate as2user if using the CloudFoundry service credentials
