# Database Backup/Restore Scripts - Implementation Summary

## Created Files

### Scripts
1. **dev-scripts/backup.sh** - Linux/Mac backup script (executable)
2. **dev-scripts/backup.bat** - Windows backup script
3. **dev-scripts/restore.sh** - Linux/Mac restore script (executable)
4. **dev-scripts/restore.bat** - Windows restore script

### Documentation
5. **dev-scripts/README.md** - Comprehensive usage guide
6. **dev-scripts/backups/.gitignore** - Prevents committing backup files to git

## Features

### Automatic Configuration Detection
✅ Reads database type from `config/as2.properties`
✅ Reads connection settings from appropriate properties file:
  - `config/database-mysql.properties` for MySQL
  - `config/database-postgresql.properties` for PostgreSQL
✅ No manual configuration needed!

### Backup Capabilities
✅ Backs up **full config database** (as2_db_config)
  - All partners, certificates, users, permissions
  - All configuration tables
✅ Backs up **version table only** from runtime database (as2_db_runtime)
  - Excludes message data (too large)
  - Keeps database version info
✅ Auto-generated or custom backup names
✅ Creates backup info file with metadata
✅ Stores in `dev-scripts/backups/` directory

### Restore Capabilities
✅ Clears all tables in both databases
✅ Restores full config database from backup
✅ Restores version table to runtime database
✅ Confirmation prompt (must type 'YES')
✅ Detailed progress messages
✅ Error handling

### Cross-Platform Support
✅ Linux/Mac: Shell scripts (.sh)
✅ Windows: Batch files (.bat)
✅ Both MySQL and PostgreSQL supported
✅ Automatic detection of available tools (mysqldump, pg_dump, etc.)

## Usage Examples

### Backup

**Linux/Mac:**
```bash
cd dev-scripts
./backup.sh                    # Auto-generated name
./backup.sh my_backup          # Custom name
```

**Windows:**
```cmd
cd dev-scripts
backup.bat                     REM Auto-generated name
backup.bat my_backup           REM Custom name
```

**Output:**
```
dev-scripts/backups/backup_20260416_120000.sql
dev-scripts/backups/backup_20260416_120000.info
```

### Restore

**Linux/Mac:**
```bash
cd dev-scripts
./restore.sh backup_20260416_120000.sql
```

**Windows:**
```cmd
cd dev-scripts
restore.bat backup_20260416_120000.sql
```

**Confirmation Required:**
```
⚠️  WARNING: This will DELETE ALL existing data in:
  - as2_db_config (all tables)
  - as2_db_runtime (all tables)

This action CANNOT be undone!

Type 'YES' to continue: YES
```

## Technical Implementation

### Configuration Reading
Both scripts use the same approach:

1. **Read database type:**
   ```bash
   # Linux/Mac
   DB_TYPE=$(grep "^as2.database.type=" config/as2.properties | cut -d'=' -f2)
   ```
   ```batch
   REM Windows
   for /f "tokens=1,2 delims==" %%a in (config\as2.properties) do (
       if "%%a"=="as2.database.type" set DB_TYPE=%%b
   )
   ```

2. **Read connection parameters:**
   - Parse appropriate properties file based on DB_TYPE
   - Extract host, port, user, password, database names
   - Use defaults if values not found

### Backup Process

**MySQL:**
```bash
mysqldump \
  --host=$DB_HOST \
  --port=$DB_PORT \
  --user=$DB_USER \
  --password=$DB_PASSWORD \
  --single-transaction \
  --routines --triggers --events \
  --add-drop-table \
  --databases $DB_CONFIG

mysqldump \
  --single-transaction \
  --add-drop-table \
  $DB_RUNTIME version
```

**PostgreSQL:**
```bash
pg_dump \
  --host=$DB_HOST \
  --port=$DB_PORT \
  --username=$DB_USER \
  --clean --if-exists --create \
  $DB_CONFIG

pg_dump \
  --clean --if-exists \
  --table=version \
  $DB_RUNTIME
```

### Restore Process

**MySQL:**
```bash
# Drop and recreate databases
mysql -e "DROP DATABASE IF EXISTS $DB_CONFIG;
          CREATE DATABASE $DB_CONFIG CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -e "DROP DATABASE IF EXISTS $DB_RUNTIME;
          CREATE DATABASE $DB_RUNTIME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Restore from backup
mysql < backup_file.sql
```

**PostgreSQL:**
```bash
# Drop and recreate databases
psql -d postgres -c "DROP DATABASE IF EXISTS $DB_CONFIG;
                      CREATE DATABASE $DB_CONFIG OWNER $DB_USER;"
psql -d postgres -c "DROP DATABASE IF EXISTS $DB_RUNTIME;
                      CREATE DATABASE $DB_RUNTIME OWNER $DB_USER;"

# Restore from backup
psql -d postgres < backup_file.sql
```

## Security Considerations

### Sensitive Data in Backups
⚠️ Backup files contain:
- Database credentials (encrypted passwords)
- Certificate private keys
- Partner configurations
- User accounts

**Recommendations:**
1. Store backups in secure location
2. Encrypt backup files if storing remotely
3. Set appropriate file permissions (chmod 600)
4. Don't commit to version control (.gitignore already configured)

### Password Handling
- Scripts use environment variables (PGPASSWORD, MYSQL_PWD)
- Passwords cleared after use
- Not displayed in output
- Read from config files only

## Backup Strategy

### Development
```bash
# Before testing changes
./backup.sh dev_test_$(date +%Y%m%d_%H%M%S)

# Quick restore if needed
./restore.sh dev_test_20260416_120000.sql
```

### Production
```bash
# Daily backups (cron)
0 2 * * * cd /path/to/dev-scripts && ./backup.sh daily_$(date +\%Y\%m\%d)

# Before upgrades
./backup.sh before_upgrade_v1.2.0

# Before configuration changes
./backup.sh before_partner_changes
```

### Retention
```bash
# Delete old daily backups (keep 7 days)
find backups/ -name "daily_*.sql" -mtime +7 -delete

# Delete old weekly backups (keep 4 weeks)
find backups/ -name "weekly_*.sql" -mtime +28 -delete
```

## Why Not Pure SQL?

Pure SQL scripts (.sql only) cannot:
1. ❌ Read configuration from properties files
2. ❌ Execute system commands (mysqldump, pg_dump)
3. ❌ Handle file I/O operations
4. ❌ Detect database type automatically
5. ❌ Provide cross-platform support
6. ❌ Create backup files outside database

Shell scripts (.sh) and batch files (.bat) provide:
1. ✅ File system operations
2. ✅ Configuration file parsing
3. ✅ External command execution
4. ✅ User interaction (prompts)
5. ✅ Error handling and validation
6. ✅ Cross-platform compatibility

## Error Handling

### Missing Tools
```
ERROR: mysqldump command not found. Please install MySQL client tools.
```
**Solution:** Install appropriate client tools

### Wrong Credentials
```
ERROR: Access denied for user 'as2user'@'localhost'
```
**Solution:** Check database credentials in config files

### Database Not Found (PostgreSQL)
```
ERROR: Database "as2_db_config" does not exist
```
**Solution:** Create database manually:
```bash
createdb -U as2user as2_db_config
```

### Backup File Not Found
```
ERROR: Backup file not found: backup_old.sql

Available backups:
  backup_20260416_120000.sql
  backup_20260415_100000.sql
```
**Solution:** Use correct filename from list

## Testing

### Test Backup
```bash
cd dev-scripts
./backup.sh test_backup
ls -lh backups/test_backup.sql
cat backups/test_backup.info
```

### Test Restore
```bash
# Create test backup first
./backup.sh before_test

# Make some changes in database (via UI or directly)

# Test restore
./restore.sh before_test.sql

# Verify data is restored
```

## Files Created

```
dev-scripts/
├── README.md                    # Comprehensive documentation
├── backup.sh                    # Linux/Mac backup script (executable)
├── backup.bat                   # Windows backup script
├── restore.sh                   # Linux/Mac restore script (executable)
├── restore.bat                  # Windows restore script
└── backups/                     # Backup storage directory
    ├── .gitignore              # Prevents committing backups to git
    ├── backup_*.sql            # SQL backup files (git ignored)
    └── backup_*.info           # Backup metadata files (git ignored)
```

## Summary

The backup/restore scripts provide:
- ✅ Zero-configuration operation (reads from config files)
- ✅ Cross-platform support (Linux/Mac/Windows)
- ✅ Multi-database support (MySQL/PostgreSQL)
- ✅ Selective backup (config full, runtime version only)
- ✅ Safe restore (confirmation required)
- ✅ Comprehensive documentation
- ✅ Production-ready with error handling
- ✅ No pure SQL because it can't handle file operations and configuration reading

Users can now:
1. Run `./backup.sh` to create backups
2. Run `./restore.sh backup_file.sql` to restore
3. No manual configuration needed
4. Works with both MySQL and PostgreSQL automatically
