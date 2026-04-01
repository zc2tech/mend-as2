-- CloudFoundry PostgreSQL Database Setup for mendelson AS2
-- Run these commands after creating the PostgreSQL service

-- 1. Connect to the PostgreSQL instance provided by CloudFoundry
-- Get credentials: cf service-key as2-postgresql admin-key

-- 2. Create the AS2 user (if not exists)
CREATE USER as2user WITH PASSWORD 'your-secure-password';

-- 3. Create the configuration database
CREATE DATABASE as2_db_config OWNER as2user;

-- 4. Create the runtime database
CREATE DATABASE as2_db_runtime OWNER as2user;

-- 5. Grant privileges
GRANT ALL PRIVILEGES ON DATABASE as2_db_config TO as2user;
GRANT ALL PRIVILEGES ON DATABASE as2_db_runtime TO as2user;

-- 6. Connect to as2_db_config and run the config schema
\c as2_db_config

-- Run all SQL from: target/as2-1.1b67/config/sqlscript/config/CREATE.sql
-- Then run: target/as2-1.1b67/config/sqlscript/config/data.sql

-- 7. Connect to as2_db_runtime and run the runtime schema
\c as2_db_runtime

-- Run all SQL from: target/as2-1.1b67/config/sqlscript/runtime/CREATE.sql
