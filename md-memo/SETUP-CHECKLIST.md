# Complete Setup Checklist for PostgreSQL

## Issues Resolved

✅ **Issue 1:** Added `keydata` table to database schema
✅ **Issue 2:** PostgreSQL JDBC driver missing - Added to `pom.xml`
✅ **Issue 3:** Resources not copied - Configured Maven to include SVG/images

## Quick Start Guide

Follow these steps to get the AS2 server running with PostgreSQL:

### 1. Build the Project

```bash
# Clean and build with all dependencies
mvn clean install
```

**Expected output:**
```
[INFO] Copying 2 resources from src/main/resources to target/classes
[INFO] Copying 222 resources from src/main/java to target/classes
[INFO] BUILD SUCCESS
```

### 2. Set Up PostgreSQL

**Option A: Automated Setup (Recommended)**
```bash
./config/setup-postgresql.sh
```

**Option B: Manual Setup**
```bash
# Create databases
psql -U postgres <<EOF
CREATE USER as2user WITH PASSWORD 'as2password';
CREATE DATABASE as2_db_config OWNER as2user;
CREATE DATABASE as2_db_runtime OWNER as2user;
GRANT ALL PRIVILEGES ON DATABASE as2_db_config TO as2user;
GRANT ALL PRIVILEGES ON DATABASE as2_db_runtime TO as2user;
EOF

# Configure connection
nano config/database-postgresql.properties
```

**Option C: Docker**
```bash
docker-compose -f config/docker-compose-postgresql.yml up -d
```

### 3. Configure Database Connection

Edit `config/database-postgresql.properties`:

```properties
postgresql.host=localhost
postgresql.port=5432
postgresql.user=as2user
postgresql.password=as2password
postgresql.db.config=as2_db_config
postgresql.db.runtime=as2_db_runtime
```

Or use environment variables:
```bash
export POSTGRES_HOST=localhost
export POSTGRES_USER=as2user
export POSTGRES_PASSWORD=as2password
```

### 4. Run the Application

```bash
# With Maven
mvn exec:java

# Or directly
java -jar target/mend-as2-1.0b0.jar
```

## Verification Checklist

### ✓ Build Verification

```bash
# 1. Check PostgreSQL driver is downloaded
ls ~/.m2/repository/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar

# 2. Check resources are in target
find target/classes -name "*.svg" | head -5

# 3. Check JAR was built
ls -lh target/as2-*.jar
```

### ✓ Database Verification

```bash
# 1. Test PostgreSQL connection
psql -h localhost -U as2user -d as2_db_config -c "SELECT version();"

# 2. Check databases exist
psql -U postgres -c "\l" | grep as2_db

# 3. Verify tables (after first run)
psql -U as2user -d as2_db_config -c "\dt"
```

### ✓ Configuration Verification

```bash
# 1. Check config file exists
cat config/database-postgresql.properties

# 2. Check environment variables (if using)
env | grep POSTGRES

# 3. Test configuration loading
java -cp "target/classes:~/.m2/repository/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar" \
  de.mendelson.comm.as2.database.PostgreSQLConfig
```

## Common Issues & Solutions

### Issue: ClassNotFoundException: org.postgresql.Driver

**Solution:**
```bash
mvn clean install
# Verify driver is downloaded
ls ~/.m2/repository/org/postgresql/postgresql/42.7.4/
```

**See:** [POSTGRESQL-DRIVER-SETUP.md](POSTGRESQL-DRIVER-SETUP.md)

### Issue: Resource missing: splash_mendelson_opensource_as2.svg

**Solution:**
```bash
mvn clean compile
# Verify resources are copied
find target/classes -name "*.svg"
```

**See:** [MISSING-RESOURCES-FIX.md](MISSING-RESOURCES-FIX.md)

### Issue: Cannot connect to database

**Solutions:**
```bash
# Check PostgreSQL is running
pg_isready -h localhost -p 5432

# Check configuration
cat config/database-postgresql.properties

# Check databases exist
psql -U postgres -c "\l" | grep as2_db

# Test connection manually
psql -h localhost -U as2user -d as2_db_config
```

**See:** [README-postgres.MD](README-postgres.MD) - Troubleshooting section

### Issue: Table 'keydata' does not exist

**Solution:**
The table will be created automatically on first run. If not:
```bash
# Check if database has version table
psql -U as2user -d as2_db_config -c "SELECT * FROM version;"

# If database is empty, it will be initialized automatically
# Or manually run CREATE.sql:
psql -U as2user -d as2_db_config -f src/main/resources/sqlscript/config/CREATE.sql
```

## File Structure After Setup

```
mend-as2/
├── config/
│   ├── database-postgresql.properties  (Your config - not in git)
│   ├── database-postgresql.env.example (Template)
│   └── setup-postgresql.sh            (Setup script)
├── target/
│   ├── mend-as2-1.0b0.jar                 (Built application)
│   └── classes/
│       └── de/mendelson/.../*.svg     (Resources)
└── ~/.m2/repository/
    └── org/postgresql/postgresql/
        └── 42.7.4/
            └── postgresql-42.7.4.jar   (JDBC driver)
```

## Documentation Reference

| Document | Purpose |
|----------|---------|
| [README-postgres.MD](README-postgres.MD) | Complete PostgreSQL guide |
| [POSTGRESQL-DRIVER-SETUP.md](POSTGRESQL-DRIVER-SETUP.md) | JDBC driver troubleshooting |
| [MISSING-RESOURCES-FIX.md](MISSING-RESOURCES-FIX.md) | Resource files issue |
| [DBSERVER-POSTGRESQL-IMPLEMENTATION.md](DBSERVER-POSTGRESQL-IMPLEMENTATION.md) | Implementation details |
| [config/POSTGRESQL-CONFIG.md](config/POSTGRESQL-CONFIG.md) | Configuration reference |

## Complete Command Sequence

Here's the complete sequence from a fresh clone:

```bash
# 1. Clone repository (if needed)
git clone https://github.com/your-repo/mend-as2.git
cd mend-as2

# 2. Build project
mvn clean install

# 3. Set up PostgreSQL
./config/setup-postgresql.sh
# OR manually:
# psql -U postgres < config/init-db.sql

# 4. Configure (if not using setup script)
cp config/database-postgresql.env.example config/database-postgresql.env
nano config/database-postgresql.env
source config/database-postgresql.env

# 5. Run application
mvn exec:java
# OR
java -jar target/mend-as2-1.0b0.jar
```

## Success Indicators

When everything is set up correctly, you should see:

```
INFO: Loaded PostgreSQL configuration from config/database-postgresql.properties
INFO: PostgreSQL Configuration:
  Host: localhost
  Port: 5432
  User: as2user
  Config DB: as2_db_config
  Runtime DB: as2_db_runtime
  Max Pool Size: 10
  Min Idle: 2
  SSL Enabled: false

INFO: Database already exists for as2_db_config, skipping creation
INFO: Database already exists for as2_db_runtime, skipping creation
INFO: [CONFIG DB] Total [10] Active [2] Idle [8]
INFO: [RUNTIME DB] Total [10] Active [1] Idle [9]
INFO: Server startup completed successfully
```

## Next Steps

After successful setup:

1. **Access the web interface:**
   - URL: http://localhost:8080/webas2
   - Default credentials: (check documentation)

2. **Configure partners:**
   - Use the web UI or client application

3. **Test message sending:**
   - Send a test AS2 message

4. **Monitor:**
   - Check logs: `tail -f logs/as2.log`
   - Check database: `psql -U as2user -d as2_db_runtime -c "SELECT * FROM messages;"`

## Getting Help

If you encounter issues not covered here:

1. Check the relevant documentation file (see table above)
2. Review the error message and search for similar issues
3. Verify all checklist items are complete
4. Check PostgreSQL logs: `sudo journalctl -u postgresql -f`
5. Check application logs: `tail -f logs/as2.log`

## Rollback to HSQLDB

If you prefer to use HSQLDB instead:

1. In your application code, use:
   ```java
   IDBDriverManager dbDriverManager = DBDriverManagerHSQL.instance();
   IDBServer dbServer = new DBServerHSQL(...);
   ```

2. No external database setup needed - HSQLDB is embedded

3. Database files will be in `data/` directory
