#!/bin/bash
#
# AS2 Database Backup Script
# Backs up:
#   - Full as2_db_config database
#   - Only version table from as2_db_runtime database
#
# Usage: ./backup.sh [backup_name]
#   backup_name: Optional custom name (default: backup_YYYYMMDD_HHMMSS)
#
# Output: Creates backup file in dev-scripts/backups/ directory
#

set -e  # Exit on error

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CONFIG_DIR="$PROJECT_ROOT/config"
BACKUP_DIR="$SCRIPT_DIR/backups"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

# Function to read property from file
read_property() {
    local file="$1"
    local key="$2"
    local default="$3"

    if [ -f "$file" ]; then
        # Read property, remove comments, trim whitespace
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

# Generate backup filename
if [ -n "$1" ]; then
    BACKUP_NAME="$1"
else
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)
    BACKUP_NAME="backup_${TIMESTAMP}"
fi

BACKUP_FILE="$BACKUP_DIR/${BACKUP_NAME}.sql"
BACKUP_INFO="$BACKUP_DIR/${BACKUP_NAME}.info"

echo ""
echo "Creating backup: $BACKUP_FILE"
echo ""

# Export password for database clients
export PGPASSWORD="$DB_PASSWORD"
export MYSQL_PWD="$DB_PASSWORD"

# Perform backup based on database type
if [ "$DB_TYPE" = "mysql" ]; then
    echo "=== Backing up MySQL databases ==="

    # Check if mysql client is available
    if ! command -v mysqldump &> /dev/null; then
        echo "ERROR: mysqldump command not found. Please install MySQL client tools."
        exit 1
    fi

    # Create backup with both databases
    echo "Dumping full config database: $DB_CONFIG"
    echo "Dumping version table from runtime database: $DB_RUNTIME"

    {
        echo "-- AS2 Database Backup"
        echo "-- Created: $(date)"
        echo "-- Database Type: MySQL"
        echo "-- Config Database: $DB_CONFIG"
        echo "-- Runtime Database: $DB_RUNTIME (version table only)"
        echo ""

        # Backup full config database
        mysqldump \
            --host="$DB_HOST" \
            --port="$DB_PORT" \
            --user="$DB_USER" \
            --single-transaction \
            --routines \
            --triggers \
            --events \
            --add-drop-table \
            --databases "$DB_CONFIG"

        echo ""
        echo "-- Version table from runtime database"
        echo ""

        # Backup only version table from runtime database
        mysqldump \
            --host="$DB_HOST" \
            --port="$DB_PORT" \
            --user="$DB_USER" \
            --single-transaction \
            --add-drop-table \
            "$DB_RUNTIME" version

    } > "$BACKUP_FILE"

else
    echo "=== Backing up PostgreSQL databases ==="

    # Check if pg_dump is available
    if ! command -v pg_dump &> /dev/null; then
        echo "ERROR: pg_dump command not found. Please install PostgreSQL client tools."
        exit 1
    fi

    # Create backup with both databases
    echo "Dumping full config database: $DB_CONFIG"
    echo "Dumping version table from runtime database: $DB_RUNTIME"

    {
        echo "-- AS2 Database Backup"
        echo "-- Created: $(date)"
        echo "-- Database Type: PostgreSQL"
        echo "-- Config Database: $DB_CONFIG"
        echo "-- Runtime Database: $DB_RUNTIME (version table only)"
        echo ""

        # Backup full config database
        pg_dump \
            --host="$DB_HOST" \
            --port="$DB_PORT" \
            --username="$DB_USER" \
            --clean \
            --if-exists \
            --create \
            "$DB_CONFIG"

        echo ""
        echo "-- Version table from runtime database"
        echo ""

        # Backup only version table from runtime database
        pg_dump \
            --host="$DB_HOST" \
            --port="$DB_PORT" \
            --username="$DB_USER" \
            --clean \
            --if-exists \
            --table=version \
            "$DB_RUNTIME"

    } > "$BACKUP_FILE"
fi

# Create backup info file
cat > "$BACKUP_INFO" << EOF
Backup Information
==================
Created: $(date)
Database Type: $DB_TYPE
Hostname: $DB_HOST
Port: $DB_PORT
Config Database: $DB_CONFIG
Runtime Database: $DB_RUNTIME
Backup File: $BACKUP_FILE
File Size: $(du -h "$BACKUP_FILE" | cut -f1)
EOF

# Clear password variables
unset PGPASSWORD
unset MYSQL_PWD

echo ""
echo "✓ Backup completed successfully!"
echo "  File: $BACKUP_FILE"
echo "  Size: $(du -h "$BACKUP_FILE" | cut -f1)"
echo "  Info: $BACKUP_INFO"
echo ""
echo "To restore this backup, run:"
echo "  ./restore.sh $(basename "$BACKUP_FILE")"
echo ""
