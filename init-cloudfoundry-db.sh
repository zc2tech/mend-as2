#!/bin/bash
# Initialize CloudFoundry PostgreSQL databases for mendelson AS2
# Run this script after establishing the SSH tunnel

set -e

PGHOST=localhost
PGPORT=5432
PGUSER=245a50905e66
PGDATABASE=BUIOHWYdgYnp

echo "Initializing CloudFoundry PostgreSQL databases for mendelson AS2..."
echo ""

# Check if we can connect
echo "Testing connection..."
psql -h $PGHOST -p $PGPORT -U $PGUSER -d $PGDATABASE -c "SELECT version();"

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Cannot connect to PostgreSQL."
    echo "Make sure the SSH tunnel is running in another terminal:"
    echo "  ./cloudfoundry-ssh-tunnel.sh"
    exit 1
fi

echo ""
echo "Connection successful!"
echo ""

# Create the two databases
echo "Creating as2_db_config database..."
psql -h $PGHOST -p $PGPORT -U $PGUSER -d $PGDATABASE -c "CREATE DATABASE as2_db_config;" || echo "Database may already exist"

echo "Creating as2_db_runtime database..."
psql -h $PGHOST -p $PGPORT -U $PGUSER -d $PGDATABASE -c "CREATE DATABASE as2_db_runtime;" || echo "Database may already exist"

echo ""
echo "Databases created successfully!"
echo ""

# Initialize as2_db_config schema
echo "Initializing as2_db_config schema..."
if [ -f "target/as2-1.1b67/config/sqlscript/config/CREATE.sql" ]; then
    psql -h $PGHOST -p $PGPORT -U $PGUSER -d as2_db_config -f target/as2-1.1b67/config/sqlscript/config/CREATE.sql
    echo "Config schema created"
else
    echo "ERROR: Cannot find target/as2-1.1b67/config/sqlscript/config/CREATE.sql"
    echo "Run 'mvn package' first to extract SQL scripts"
    exit 1
fi

echo "Loading initial configuration data..."
if [ -f "target/as2-1.1b67/config/sqlscript/config/data.sql" ]; then
    psql -h $PGHOST -p $PGPORT -U $PGUSER -d as2_db_config -f target/as2-1.1b67/config/sqlscript/config/data.sql
    echo "Config data loaded"
else
    echo "ERROR: Cannot find target/as2-1.1b67/config/sqlscript/config/data.sql"
    exit 1
fi

echo ""
echo "Initializing as2_db_runtime schema..."
if [ -f "target/as2-1.1b67/config/sqlscript/runtime/CREATE.sql" ]; then
    psql -h $PGHOST -p $PGPORT -U $PGUSER -d as2_db_runtime -f target/as2-1.1b67/config/sqlscript/runtime/CREATE.sql
    echo "Runtime schema created"
else
    echo "ERROR: Cannot find target/as2-1.1b67/config/sqlscript/runtime/CREATE.sql"
    exit 1
fi

echo ""
echo "============================================"
echo "Database initialization complete!"
echo "============================================"
echo ""
echo "Next steps:"
echo "1. Close the SSH tunnel (Ctrl+C in the tunnel terminal)"
echo "2. Update src/main/resources/as2.properties with CloudFoundry credentials"
echo "3. Deploy to CloudFoundry: cf push"
echo ""
