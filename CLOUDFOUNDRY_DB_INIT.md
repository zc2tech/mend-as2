# CloudFoundry PostgreSQL Database Initialization

## Overview

The mendelson AS2 server requires two separate PostgreSQL databases:
- **as2_db_config**: Configuration database (partners, certificates, preferences)
- **as2_db_runtime**: Runtime database (messages, transactions, statistics)

This guide explains how to initialize these databases in your CloudFoundry PostgreSQL service.

## Prerequisites

- CloudFoundry CLI installed: `cf --version`
- PostgreSQL service created: `cf service as2-postgresql`
- AS2 project built: `mvn package -DskipTests` (to extract SQL scripts)

## Your CloudFoundry PostgreSQL Credentials

```
Host:     postgres-6d0b82f6-568c-4e83-be41-0c0af924e5d3.crkc3ulytfr9.eu-central-1.rds.amazonaws.com
Port:     8062
User:     245a50905e66
Password: tYjNLqQI7A
Database: BUIOHWYdgYnp
```

## Method 1: SSH Tunnel from Local Machine (Recommended)

This approach lets you connect to CloudFoundry PostgreSQL from your local machine using an SSH tunnel.

### Step 1 - Build the project (if not already done)

```bash
cd /Users/I572958/SAPDevelop/github/mend-as2
mvn package -DskipTests
```

This extracts the SQL scripts to `target/as2-1.1b67/config/sqlscript/`.

### Step 2 - Start SSH tunnel

**Terminal 1** - Keep this running:
```bash
./cloudfoundry-ssh-tunnel.sh
```

This creates a tunnel from `localhost:5432` to your CloudFoundry PostgreSQL service. Keep this terminal open.

### Step 3 - Initialize databases

**Terminal 2** - Run the initialization script:
```bash
./init-cloudfoundry-db.sh
```

When prompted for password, enter: `tYjNLqQI7A`

The script will:
1. Test the connection
2. Create `as2_db_config` and `as2_db_runtime` databases
3. Run SQL schema scripts for both databases
4. Load initial configuration data

### Step 4 - Verify initialization

```bash
# Connect to config database
psql -h localhost -U 245a50905e66 -d as2_db_config -p 5432

# Inside psql:
\dt    -- List tables (should see partners, certificates, etc.)
\q     -- Exit

# Connect to runtime database
psql -h localhost -U 245a50905e66 -d as2_db_runtime -p 5432

# Inside psql:
\dt    -- List tables (should see messages, mdn, etc.)
\q     -- Exit
```

### Step 5 - Close the tunnel

Press `Ctrl+C` in Terminal 1 to close the SSH tunnel.

## Method 2: Initialize from CloudFoundry Container

If the SSH tunnel doesn't work, you can initialize from within the CloudFoundry container.

### Step 1 - Deploy the application first

```bash
cf push
```

The app may fail to start (because databases aren't initialized yet), but the container will be running.

### Step 2 - SSH into the container

```bash
cf ssh mendelson-as2
```

### Step 3 - Connect to PostgreSQL

Inside the container:
```bash
export PGPASSWORD="tYjNLqQI7A"
psql -h postgres-6d0b82f6-568c-4e83-be41-0c0af924e5d3.crkc3ulytfr9.eu-central-1.rds.amazonaws.com -U 245a50905e66 -d BUIOHWYdgYnp -p 8062
```

### Step 4 - Create databases

Inside psql:
```sql
-- Create the two databases
CREATE DATABASE as2_db_config;
CREATE DATABASE as2_db_runtime;

-- Exit psql
\q
```

### Step 5 - Initialize config database

```bash
# Connect to as2_db_config
psql -h postgres-6d0b82f6-568c-4e83-be41-0c0af924e5d3.crkc3ulytfr9.eu-central-1.rds.amazonaws.com -U 245a50905e66 -d as2_db_config -p 8062

# Inside psql, copy/paste the entire contents of these files:
# 1. app/config/sqlscript/config/CREATE.sql
# 2. app/config/sqlscript/config/data.sql

\q
```

### Step 6 - Initialize runtime database

```bash
# Connect to as2_db_runtime
psql -h postgres-6d0b82f6-568c-4e83-be41-0c0af924e5d3.crkc3ulytfr9.eu-central-1.rds.amazonaws.com -U 245a50905e66 -d as2_db_runtime -p 8062

# Inside psql, copy/paste the entire contents of:
# app/config/sqlscript/runtime/CREATE.sql

\q
```

### Step 7 - Exit SSH and restart app

```bash
exit  # Exit from cf ssh

# Restart the application
cf restart mendelson-as2
```

## Configuration for CloudFoundry

The AS2 server automatically reads PostgreSQL credentials from the `VCAP_SERVICES` environment variable when the `as2-postgresql` service is bound.

However, since you need TWO databases, you'll need to configure the connection URLs manually.

### Option A - Use VCAP_SERVICES parsing

Modify the database connection code to parse VCAP_SERVICES and connect to both databases using the same credentials but different database names.

### Option B - Set environment variables

```bash
cf set-env mendelson-as2 DB_CONFIG_NAME as2_db_config
cf set-env mendelson-as2 DB_RUNTIME_NAME as2_db_runtime
cf restage mendelson-as2
```

Then modify the database connection code to use these database names instead of the default.

## Troubleshooting

### Connection Refused

If you get "Connection refused" when trying to connect directly:
- CloudFoundry PostgreSQL is not accessible from outside the CF network
- You MUST use the SSH tunnel (Method 1) or connect from within the container (Method 2)

### SSH Tunnel Fails

If `cf ssh` fails:
```bash
# Check if SSH is enabled for your app
cf ssh-enabled mendelson-as2

# Enable SSH if needed
cf enable-ssh mendelson-as2
cf restart mendelson-as2
```

### Database Already Exists

If you see "database already exists" errors, it's safe to ignore them. Continue with running the schema scripts.

### SQL Scripts Not Found

If the SQL scripts are missing from `target/as2-1.1b67/`:
```bash
# Rebuild the project
mvn clean package -DskipTests
```

### Permission Denied

If you get permission errors creating databases:
```bash
# Verify your user has CREATEDB privilege
psql -h localhost -U 245a50905e66 -d BUIOHWYdgYnp -p 5432 -c "\du"
```

The CloudFoundry service user (245a50905e66) should have full privileges.

## Next Steps

After database initialization:

1. **Deploy to CloudFoundry:**
   ```bash
   cf push
   ```

2. **Verify deployment:**
   ```bash
   cf app mendelson-as2
   cf logs mendelson-as2 --recent
   ```

3. **Access the web UI:**
   ```
   https://mendelson-as2.apps.example.com/as2/admin/
   ```

4. **Login with default credentials:**
   - Username: `admin`
   - Password: `admin`
   - **IMPORTANT:** Change this password immediately after first login!

## Database Connection in AS2 Server

The AS2 server needs to connect to TWO databases. You'll need to configure this in the database connection code or properties file:

**Config database connection:**
```
jdbc:postgresql://postgres-6d0b82f6-568c-4e83-be41-0c0af924e5d3.crkc3ulytfr9.eu-central-1.rds.amazonaws.com:8062/as2_db_config
User: 245a50905e66
Password: tYjNLqQI7A
```

**Runtime database connection:**
```
jdbc:postgresql://postgres-6d0b82f6-568c-4e83-be41-0c0af924e5d3.crkc3ulytfr9.eu-central-1.rds.amazonaws.com:8062/as2_db_runtime
User: 245a50905e66
Password: tYjNLqQI7A
```

Both use the same host, port, user, and password - only the database name differs.
