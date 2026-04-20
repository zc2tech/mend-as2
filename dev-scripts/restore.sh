#!/bin/bash
#
# AS2 Database Restore Script
# Restores:
#   - Drops all tables in as2_db_config database (keeps database)
#   - Drops all tables in as2_db_runtime database (keeps database)
#   - Restores full as2_db_config from backup
#   - Restores version table to as2_db_runtime from backup
#
# Usage: ./restore.sh <backup_file>
#   backup_file: Name of backup file (e.g., backup_20260416_120000.sql)
#                Can be full path or just filename (looks in backups/ directory)
#
# WARNING: This will DELETE all existing data!
#
# Note: This script assumes the databases already exist and only drops tables.
#       It does NOT drop or recreate the databases themselves.
#

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CONFIG_DIR="$PROJECT_ROOT/config"
BACKUP_DIR="$SCRIPT_DIR/backups"

# Check if backup file is specified
if [ -z "$1" ]; then
    echo "ERROR: Backup file not specified"
    echo ""
    echo "Usage: $0 <backup_file>"
    echo ""
    echo "Available backups:"
    ls -1 "$BACKUP_DIR"/*.sql 2>/dev/null | xargs -n 1 basename || echo "  (none found)"
    exit 1
fi

# Determine backup file path
BACKUP_FILE="$1"
if [ ! -f "$BACKUP_FILE" ]; then
    # Try in backups directory
    BACKUP_FILE="$BACKUP_DIR/$1"
    if [ ! -f "$BACKUP_FILE" ]; then
        echo "ERROR: Backup file not found: $1"
        echo ""
        echo "Available backups:"
        ls -1 "$BACKUP_DIR"/*.sql 2>/dev/null | xargs -n 1 basename || echo "  (none found)"
        exit 1
    fi
fi

echo "Restore file: $BACKUP_FILE"
echo "File size: $(du -h "$BACKUP_FILE" | cut -f1)"
echo ""

# Function to read property from file
read_property() {
    local file="$1"
    local key="$2"
    local default="$3"

    if [ -f "$file" ]; then
        local value=$(grep "^${key}=" "$file" 2>/dev/null | cut -d'=' -f2- | sed 's/#.*//' | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
        echo "${value:-$default}"
    else
        echo "$default"
    fi
}

# Detect database type from as2.properties
DB_TYPE=$(read_property "$CONFIG_DIR/as2.properties" "as2.database.type" "postgresql")
echo "Detected database type: $DB_TYPE"

# Read database configuration based on type
if [ "$DB_TYPE" = "mysql" ]; then
    PROPS_FILE="$CONFIG_DIR/database-mysql.properties"
    DB_HOST=$(read_property "$PROPS_FILE" "mysql.host" "localhost")
    DB_PORT=$(read_property "$PROPS_FILE" "mysql.port" "3306")
    DB_USER=$(read_property "$PROPS_FILE" "mysql.user" "as2user")
    DB_PASSWORD=$(read_property "$PROPS_FILE" "mysql.password" "as2password")
    DB_CONFIG=$(read_property "$PROPS_FILE" "mysql.db.config" "as2_db_config")
    DB_RUNTIME=$(read_property "$PROPS_FILE" "mysql.db.runtime" "as2_db_runtime")
else
    PROPS_FILE="$CONFIG_DIR/database-postgresql.properties"
    DB_HOST=$(read_property "$PROPS_FILE" "postgresql.host" "localhost")
    DB_PORT=$(read_property "$PROPS_FILE" "postgresql.port" "5432")
    DB_USER=$(read_property "$PROPS_FILE" "postgresql.user" "as2user")
    DB_PASSWORD=$(read_property "$PROPS_FILE" "postgresql.password" "as2password")
    DB_CONFIG=$(read_property "$PROPS_FILE" "postgresql.db.config" "as2_db_config")
    DB_RUNTIME=$(read_property "$PROPS_FILE" "postgresql.db.runtime" "as2_db_runtime")
fi

echo "Database Configuration:"
echo "  Host: $DB_HOST"
echo "  Port: $DB_PORT"
echo "  User: $DB_USER"
echo "  Config DB: $DB_CONFIG"
echo "  Runtime DB: $DB_RUNTIME"
echo ""

# WARNING prompt
echo "⚠️  WARNING: This will DELETE ALL existing tables in:"
echo "  - $DB_CONFIG (all tables will be dropped)"
echo "  - $DB_RUNTIME (all tables will be dropped)"
echo ""
echo "  Note: The databases themselves will NOT be dropped or recreated."
echo "        Only the tables within them will be removed."
echo ""
echo "This action CANNOT be undone!"
echo ""
read -p "Type 'YES' to continue: " CONFIRM

if [ "$CONFIRM" != "YES" ]; then
    echo "Restore cancelled."
    exit 0
fi

echo ""
echo "Starting restore..."
echo ""

# Export password for database clients
export PGPASSWORD="$DB_PASSWORD"
export MYSQL_PWD="$DB_PASSWORD"

# Perform restore based on database type
if [ "$DB_TYPE" = "mysql" ]; then
    echo "=== Restoring MySQL databases ==="

    # Check if mysql client is available
    if ! command -v mysql &> /dev/null; then
        echo "ERROR: mysql command not found. Please install MySQL client tools."
        exit 1
    fi

    echo "Step 1: Dropping all tables in $DB_CONFIG..."
    mysql --host="$DB_HOST" --port="$DB_PORT" --user="$DB_USER" --database="$DB_CONFIG" -e "
        SET FOREIGN_KEY_CHECKS = 0;
        SET GROUP_CONCAT_MAX_LEN = 32768;
        SET @tables = NULL;
        SELECT GROUP_CONCAT('\`', table_name, '\`') INTO @tables
        FROM information_schema.tables
        WHERE table_schema = '$DB_CONFIG';
        SET @tables = CONCAT('DROP TABLE IF EXISTS ', @tables);
        PREPARE stmt FROM @tables;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET FOREIGN_KEY_CHECKS = 1;
    "

    echo "Step 2: Dropping all tables in $DB_RUNTIME..."
    mysql --host="$DB_HOST" --port="$DB_PORT" --user="$DB_USER" --database="$DB_RUNTIME" -e "
        SET FOREIGN_KEY_CHECKS = 0;
        SET GROUP_CONCAT_MAX_LEN = 32768;
        SET @tables = NULL;
        SELECT GROUP_CONCAT('\`', table_name, '\`') INTO @tables
        FROM information_schema.tables
        WHERE table_schema = '$DB_RUNTIME';
        SET @tables = CONCAT('DROP TABLE IF EXISTS ', @tables);
        PREPARE stmt FROM @tables;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET FOREIGN_KEY_CHECKS = 1;
    "

    echo "Step 3: Restoring from backup file..."
    mysql --host="$DB_HOST" --port="$DB_PORT" --user="$DB_USER" < "$BACKUP_FILE"

else
    echo "=== Restoring PostgreSQL databases ==="

    # Check if psql is available
    if ! command -v psql &> /dev/null; then
        echo "ERROR: psql command not found. Please install PostgreSQL client tools."
        exit 1
    fi

    echo "Step 1: Dropping all tables in $DB_CONFIG..."
    psql --host="$DB_HOST" --port="$DB_PORT" --username="$DB_USER" --dbname="$DB_CONFIG" <<EOF
DO \$\$ DECLARE
    r RECORD;
BEGIN
    -- Drop all tables
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
    END LOOP;
    -- Drop all sequences
    FOR r IN (SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public') LOOP
        EXECUTE 'DROP SEQUENCE IF EXISTS ' || quote_ident(r.sequence_name) || ' CASCADE';
    END LOOP;
END \$\$;
EOF

    echo "Step 2: Dropping all tables in $DB_RUNTIME..."
    psql --host="$DB_HOST" --port="$DB_PORT" --username="$DB_USER" --dbname="$DB_RUNTIME" <<EOF
DO \$\$ DECLARE
    r RECORD;
BEGIN
    -- Drop all tables
    FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
        EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
    END LOOP;
    -- Drop all sequences
    FOR r IN (SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public') LOOP
        EXECUTE 'DROP SEQUENCE IF EXISTS ' || quote_ident(r.sequence_name) || ' CASCADE';
    END LOOP;
END \$\$;
EOF

    echo "Step 3: Restoring from backup file..."
    psql --host="$DB_HOST" --port="$DB_PORT" --username="$DB_USER" --dbname="postgres" < "$BACKUP_FILE"
fi

# Clear password variables
unset PGPASSWORD
unset MYSQL_PWD

echo ""
echo "✓ Restore completed successfully!"
echo ""
echo "Database has been restored to backup state:"
echo "  Backup file: $(basename "$BACKUP_FILE")"
echo "  Config DB: $DB_CONFIG (full restore)"
echo "  Runtime DB: $DB_RUNTIME (version table only)"
echo ""
echo "Note: Runtime database has only the version table restored."
echo "      All message data (messages, MDN, logs) has been cleared."
echo ""
