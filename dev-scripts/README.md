# Database Backup and Restore Scripts

This directory contains scripts for backing up and restoring the AS2 database.

## Overview

The backup scripts create a snapshot of:
- **Full config database** (`as2_db_config`) - All tables including partners, certificates, users, permissions, etc.
- **Version table only** from runtime database (`as2_db_runtime`) - Just the version table, not message data

The restore scripts:
- **Clear all data** from both config and runtime databases
- **Restore the full config database** from backup
- **Restore only the version table** to runtime database

## Scripts

### Linux/Mac
- `backup.sh` - Create a backup
- `restore.sh` - Restore from a backup

### Windows
- `backup.bat` - Create a backup
- `restore.bat` - Restore from a backup

## Prerequisites

### For MySQL/MariaDB
- `mysqldump` command (for backup)
- `mysql` command (for restore)
- Install: `sudo apt-get install mysql-client` (Linux) or download from [MySQL Downloads](https://dev.mysql.com/downloads/)

### For PostgreSQL
- `pg_dump` command (for backup)
- `psql` command (for restore)
- Install: `sudo apt-get install postgresql-client` (Linux) or download from [PostgreSQL Downloads](https://www.postgresql.org/download/)

## Usage

### Backup

**Linux/Mac:**
```bash
cd dev-scripts

# Create backup with auto-generated name (backup_YYYYMMDD_HHMMSS.sql)
./backup.sh

# Create backup with custom name
./backup.sh my_backup_name
```

**Windows:**
```cmd
cd dev-scripts

REM Create backup with auto-generated name
backup.bat

REM Create backup with custom name
backup.bat my_backup_name
```

**Output:**
- Backup file: `dev-scripts/backups/backup_20260416_120000.sql`
- Info file: `dev-scripts/backups/backup_20260416_120000.info`

### Restore

**Linux/Mac:**
```bash
cd dev-scripts

# List available backups
ls -1 backups/*.sql

# Restore from backup (specify filename)
./restore.sh backup_20260416_120000.sql

# Or use full path
./restore.sh backups/backup_20260416_120000.sql
```

**Windows:**
```cmd
cd dev-scripts

REM List available backups
dir backups\*.sql

REM Restore from backup
restore.bat backup_20260416_120000.sql
```

**⚠️ WARNING:** Restore will delete ALL existing data! You must type 'YES' to confirm.

## How It Works

### Configuration Detection

The scripts automatically detect database settings from:
1. `config/as2.properties` - Database type (mysql or postgresql)
2. `config/database-mysql.properties` - MySQL connection settings
3. `config/database-postgresql.properties` - PostgreSQL connection settings

No manual configuration needed!

### Backup Process

1. Reads database type from `config/as2.properties`
2. Reads connection settings from appropriate properties file
3. Creates `dev-scripts/backups/` directory if needed
4. Dumps full config database using `mysqldump` or `pg_dump`
5. Dumps only version table from runtime database
6. Combines into single SQL file
7. Creates info file with backup metadata

### Restore Process

1. Reads database type and connection settings (same as backup)
2. Prompts for confirmation (must type 'YES')
3. Drops and recreates config database (clears all tables)
4. Drops and recreates runtime database (clears all tables)
5. Restores from backup SQL file
6. Config database fully restored
7. Runtime database has only version table restored

## Backup File Structure

Example backup file:
```sql
-- AS2 Database Backup
-- Created: Wed Apr 16 12:00:00 CST 2026
-- Database Type: MySQL
-- Config Database: as2_db_config
-- Runtime Database: as2_db_runtime (version table only)

-- Full config database dump
DROP DATABASE IF EXISTS as2_db_config;
CREATE DATABASE as2_db_config;
USE as2_db_config;
-- ... all tables and data ...

-- Version table from runtime database
DROP TABLE IF EXISTS version;
CREATE TABLE version (...);
INSERT INTO version VALUES (...);
```

## What Gets Backed Up

### Config Database (Full Backup)
✅ Partners and partner configurations
✅ Certificates and keystores
✅ Users and permissions
✅ HTTP headers
✅ System preferences
✅ Notification settings
✅ All other config tables

### Runtime Database (Version Table Only)
✅ Version table (database version info)
❌ Messages (not backed up)
❌ MDN receipts (not backed up)
❌ Statistics (not backed up)
❌ Message logs (not backed up)

**Rationale:** Runtime data (messages, MDNs) can be very large and is typically not needed for configuration restore. The version table is backed up to maintain database version consistency.

## Important Notes

### Security
- **Passwords in backup files:** Config database backup includes encrypted passwords and certificate private keys. Store backup files securely!
- **File permissions:** Backup files may contain sensitive data. Protect them appropriately.

### Disk Space
- Config database typically: 1-100 MB (depends on number of partners/certificates)
- Runtime database (if full): Can be very large (GBs) depending on message history
- Version table only: < 1 KB

### Before Restore
Always create a fresh backup before restoring:
```bash
./backup.sh before_restore_$(date +%Y%m%d_%H%M%S)
./restore.sh old_backup.sql
```

### After Restore
- Restart the AS2 server for changes to take effect
- Verify partner configurations
- Check certificate validity
- Test message sending/receiving

## Backup Strategy Recommendations

### Development
```bash
# Quick backup before testing
./backup.sh dev_test

# Restore if needed
./restore.sh dev_test.sql
```

### Production
```bash
# Daily backup (run via cron)
0 2 * * * /path/to/dev-scripts/backup.sh daily_$(date +%Y%m%d)

# Weekly backup
0 3 * * 0 /path/to/dev-scripts/backup.sh weekly_$(date +%Y%W)

# Before upgrade
./backup.sh before_upgrade_v1.2.0
```

### Retention Policy
```bash
# Keep daily backups for 7 days
find backups/ -name "daily_*.sql" -mtime +7 -delete

# Keep weekly backups for 4 weeks
find backups/ -name "weekly_*.sql" -mtime +28 -delete

# Keep manual backups indefinitely
```

## Troubleshooting

### "command not found" Error
**Problem:** `mysqldump`, `mysql`, `pg_dump`, or `psql` not in PATH

**Solution:**
```bash
# Linux/Mac - Install client tools
# MySQL
sudo apt-get install mysql-client

# PostgreSQL
sudo apt-get install postgresql-client

# Windows - Add to PATH or use full path
set PATH=%PATH%;C:\Program Files\MySQL\MySQL Server 8.0\bin
```

### "Access denied" Error
**Problem:** Database credentials incorrect

**Solution:**
1. Check `config/database-*.properties`
2. Verify username and password
3. Test connection manually:
   ```bash
   # MySQL
   mysql -h localhost -u as2user -p
   
   # PostgreSQL
   psql -h localhost -U as2user -d as2_db_config
   ```

### "Database does not exist" Error
**Problem:** Database not created yet (PostgreSQL only)

**Solution:**
```bash
# PostgreSQL - Create databases manually
createdb -U as2user as2_db_config
createdb -U as2user as2_db_runtime

# MySQL - Databases created automatically
```

### Large Backup File
**Problem:** Backup file is too large

**Solution:**
```bash
# Compress backup
gzip backups/backup_20260416_120000.sql

# Restore compressed backup
gunzip -c backups/backup_20260416_120000.sql.gz | mysql -u as2user -p
```

## Advanced Usage

### Remote Database Backup
```bash
# Edit config/database-*.properties
mysql.host=remote-server.example.com
postgresql.host=remote-server.example.com

# Run backup normally
./backup.sh remote_backup
```

### Scheduled Backups (Linux/Mac)
```bash
# Add to crontab
crontab -e

# Daily at 2 AM
0 2 * * * cd /path/to/mend-as2/dev-scripts && ./backup.sh daily_$(date +\%Y\%m\%d) >> backup.log 2>&1
```

### Scheduled Backups (Windows)
```cmd
REM Create task in Task Scheduler
schtasks /create /tn "AS2 Daily Backup" /tr "C:\path\to\dev-scripts\backup.bat daily" /sc daily /st 02:00
```

## See Also

- [DATABASE_INITIALIZATION.md](../DATABASE_INITIALIZATION.md) - How databases are created
- [MYSQL-CONFIG.md](../config/MYSQL-CONFIG.md) - MySQL configuration guide
- [POSTGRESQL-CONFIG.md](../config/POSTGRESQL-CONFIG.md) - PostgreSQL configuration guide
