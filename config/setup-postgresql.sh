#!/bin/bash
# PostgreSQL Database Setup Script for AS2 Server
# This script helps set up the PostgreSQL databases for the AS2 server

set -e

echo "==================================="
echo "AS2 PostgreSQL Database Setup"
echo "==================================="
echo ""

# Default values
DEFAULT_HOST="localhost"
DEFAULT_PORT="5432"
DEFAULT_USER="as2user"
DEFAULT_PASSWORD="as2password"
DEFAULT_ADMIN_USER="postgres"
DEFAULT_CONFIG_DB="as2_db_config"
DEFAULT_RUNTIME_DB="as2_db_runtime"

# Read configuration
read -p "PostgreSQL admin user [$DEFAULT_ADMIN_USER]: " ADMIN_USER
ADMIN_USER=${ADMIN_USER:-$DEFAULT_ADMIN_USER}

read -p "PostgreSQL host [$DEFAULT_HOST]: " PG_HOST
PG_HOST=${PG_HOST:-$DEFAULT_HOST}

read -p "PostgreSQL port [$DEFAULT_PORT]: " PG_PORT
PG_PORT=${PG_PORT:-$DEFAULT_PORT}

read -p "AS2 database user [$DEFAULT_USER]: " DB_USER
DB_USER=${DB_USER:-$DEFAULT_USER}

read -sp "AS2 database password [$DEFAULT_PASSWORD]: " DB_PASSWORD
echo ""
DB_PASSWORD=${DB_PASSWORD:-$DEFAULT_PASSWORD}

read -p "Config database name [$DEFAULT_CONFIG_DB]: " CONFIG_DB
CONFIG_DB=${CONFIG_DB:-$DEFAULT_CONFIG_DB}

read -p "Runtime database name [$DEFAULT_RUNTIME_DB]: " RUNTIME_DB
RUNTIME_DB=${RUNTIME_DB:-$DEFAULT_RUNTIME_DB}

echo ""
echo "Summary:"
echo "--------"
echo "Host: $PG_HOST"
echo "Port: $PG_PORT"
echo "Admin User: $ADMIN_USER"
echo "AS2 User: $DB_USER"
echo "Config DB: $CONFIG_DB"
echo "Runtime DB: $RUNTIME_DB"
echo ""

read -p "Proceed with database creation? (y/n): " CONFIRM
if [ "$CONFIRM" != "y" ] && [ "$CONFIRM" != "Y" ]; then
    echo "Setup cancelled."
    exit 0
fi

echo ""
echo "Creating databases..."

# Create databases using psql
PGPASSWORD=$DB_PASSWORD psql -h "$PG_HOST" -p "$PG_PORT" -U "$ADMIN_USER" <<EOF
-- Create user if not exists
DO
\$do\$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles WHERE rolname = '$DB_USER'
   ) THEN
      CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';
   END IF;
END
\$do\$;

-- Create config database
DROP DATABASE IF EXISTS $CONFIG_DB;
CREATE DATABASE $CONFIG_DB OWNER $DB_USER;
GRANT ALL PRIVILEGES ON DATABASE $CONFIG_DB TO $DB_USER;

-- Create runtime database
DROP DATABASE IF EXISTS $RUNTIME_DB;
CREATE DATABASE $RUNTIME_DB OWNER $DB_USER;
GRANT ALL PRIVILEGES ON DATABASE $RUNTIME_DB TO $DB_USER;

\echo 'Databases created successfully!'
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Databases created successfully!"
    echo ""
    echo "Creating configuration file..."

    # Create configuration file
    cat > config/database-postgresql.properties <<EOF
# PostgreSQL Database Configuration for AS2 Server
# Generated on $(date)

# Database server settings
postgresql.host=$PG_HOST
postgresql.port=$PG_PORT

# Database credentials
postgresql.user=$DB_USER
postgresql.password=$DB_PASSWORD

# Database names
postgresql.db.config=$CONFIG_DB
postgresql.db.runtime=$RUNTIME_DB

# Connection pool settings
postgresql.pool.maximumPoolSize=10
postgresql.pool.minimumIdle=2
postgresql.pool.connectionTimeout=30000
postgresql.pool.idleTimeout=600000
postgresql.pool.maxLifetime=1800000

# SSL settings
postgresql.ssl.enabled=false
# postgresql.ssl.mode=require
EOF

    echo "✓ Configuration file created at: config/database-postgresql.properties"
    echo ""
    echo "Setup complete! You can now start the AS2 server."
    echo ""
    echo "To start the server:"
    echo "  java -jar mendelson-as2.jar"
    echo ""
else
    echo ""
    echo "✗ Database creation failed!"
    echo "Please check your PostgreSQL connection settings and try again."
    exit 1
fi
