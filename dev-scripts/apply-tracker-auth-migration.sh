#!/bin/bash

# Apply User Tracker Auth Migration to MySQL Database
# This script adds the tracker_auth_basic_enabled and tracker_auth_cert_enabled columns
# to the webui_users table and creates the user_tracker_auth_credentials table.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MIGRATION_SQL="$SCRIPT_DIR/../src/main/resources/sqlscript/mysql/migration_user_tracker_auth.sql"

# Load database configuration
source "$SCRIPT_DIR/load-db-config.sh"

# Check if migration file exists
if [ ! -f "$MIGRATION_SQL" ]; then
    echo "❌ Migration SQL file not found: $MIGRATION_SQL"
    exit 1
fi

echo "=========================================="
echo "User Tracker Auth Migration (MySQL)"
echo "=========================================="
echo ""
echo "This will:"
echo "  1. Add tracker_auth_basic_enabled and tracker_auth_cert_enabled columns to webui_users"
echo "  2. Create user_tracker_auth_credentials table"
echo ""
echo "Database: $MYSQL_CONFIG_DB"
echo "Host: $MYSQL_HOST:$MYSQL_PORT"
echo ""
read -p "Continue? (y/n) " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Aborted."
    exit 1
fi

echo ""
echo "Running migration..."
mysql -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_CONFIG_DB" < "$MIGRATION_SQL"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Migration completed successfully!"
    echo ""
    echo "Next steps:"
    echo "  1. Restart your AS2 server"
    echo "  2. Login to WebUI"
    echo "  3. Navigate to username dropdown → My Tracker Conf"
    echo "  4. Configure your tracker authentication settings"
else
    echo ""
    echo "❌ Migration failed!"
    exit 1
fi
