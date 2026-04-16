# Database Backup/Restore Scripts - Summary

## ✅ Implementation Complete

Created comprehensive backup and restore scripts for the AS2 database under the `dev-scripts/` folder.

## Files Created

```
dev-scripts/
├── README.md                           # 📖 User guide (400+ lines)
├── BACKUP_RESTORE_IMPLEMENTATION.md    # 📋 Implementation details
├── backup.sh                           # 🐧 Linux/Mac backup (executable)
├── backup.bat                          # 🪟 Windows backup
├── restore.sh                          # 🐧 Linux/Mac restore (executable)
├── restore.bat                         # 🪟 Windows restore
└── backups/
    └── .gitignore                      # 🚫 Prevents committing backups
```

## Features

### ✅ Requirements Met

1. **Backup Script**
   - ✅ Backs up full `as2_db_config` database
   - ✅ Backs up `version` table from `as2_db_runtime`
   - ✅ Creates timestamped backup files
   - ✅ Supports custom backup names

2. **Restore Script**
   - ✅ Clears all tables in both databases
   - ✅ Restores data from backup file
   - ✅ User specifies backup filename
   - ✅ Requires confirmation (type 'YES')

3. **Configuration Detection**
   - ✅ Reads database type from `config/as2.properties`
   - ✅ Reads connection settings from properties files:
     - `config/database-mysql.properties`
     - `config/database-postgresql.properties`
   - ✅ No manual configuration needed!

4. **Cross-Platform**
   - ✅ Shell scripts (.sh) for Linux/Mac
   - ✅ Batch files (.bat) for Windows
   - ✅ Both MySQL and PostgreSQL support

### 🎯 Why Not Pure SQL?

Pure SQL cannot:
- ❌ Read configuration files
- ❌ Execute external commands (mysqldump/pg_dump)
- ❌ Handle file I/O
- ❌ Detect database type automatically
- ❌ Provide user interaction (prompts)

Shell/batch scripts provide:
- ✅ Configuration parsing
- ✅ External tool execution
- ✅ File operations
- ✅ User prompts and validation
- ✅ Error handling

## Usage

### Backup
```bash
# Linux/Mac
./dev-scripts/backup.sh
./dev-scripts/backup.sh my_custom_name

# Windows
dev-scripts\backup.bat
dev-scripts\backup.bat my_custom_name
```

**Output:**
- `dev-scripts/backups/backup_20260416_120000.sql`
- `dev-scripts/backups/backup_20260416_120000.info`

### Restore
```bash
# Linux/Mac
./dev-scripts/restore.sh backup_20260416_120000.sql

# Windows
dev-scripts\restore.bat backup_20260416_120000.sql
```

**Safety:** Must type 'YES' to confirm deletion of existing data.

## What Gets Backed Up

### Config Database (Full)
- ✅ Partners
- ✅ Certificates
- ✅ Users & Permissions
- ✅ HTTP Headers
- ✅ System Preferences
- ✅ All configuration

### Runtime Database (Version Only)
- ✅ Version table (database version info)
- ❌ Messages (too large, not needed)
- ❌ MDN receipts
- ❌ Statistics
- ❌ Logs

**Rationale:** Runtime data can be massive. Only version table is needed for configuration restore.

## Automatic Configuration

Scripts automatically detect:
```
Reading: config/as2.properties
  → Database type: mysql

Reading: config/database-mysql.properties
  → Host: localhost
  → Port: 3306
  → User: as2user
  → Config DB: as2_db_config
  → Runtime DB: as2_db_runtime
```

**No user input needed!**

## Testing

Tested and working:
```bash
$ ./dev-scripts/backup.sh test_config
Detected database type: mysql
Database Configuration:
  Host: localhost
  Port: 3306
  User: as2user
  Config DB: as2_db_config
  Runtime DB: as2_db_runtime

Creating backup: .../backups/test_config.sql

✓ Backup completed successfully!
  File: .../backups/test_config.sql
  Size: 65K
```

## Security

⚠️ **Backup files contain sensitive data:**
- Database credentials (encrypted)
- Certificate private keys
- Partner configurations

**Protection:**
- ✅ `.gitignore` prevents git commits
- ✅ Stored in `backups/` subdirectory
- ⚠️ User must secure backup files appropriately

## Production Use

### Daily Backups (Cron)
```bash
# Add to crontab
0 2 * * * cd /path/to/mend-as2/dev-scripts && ./backup.sh daily_$(date +\%Y\%m\%d)
```

### Before Upgrades
```bash
./backup.sh before_upgrade_v1.2.0
# ... perform upgrade ...
# If problems: ./restore.sh before_upgrade_v1.2.0.sql
```

### Retention Strategy
```bash
# Keep daily backups for 7 days
find backups/ -name "daily_*.sql" -mtime +7 -delete

# Keep weekly backups for 4 weeks
find backups/ -name "weekly_*.sql" -mtime +28 -delete
```

## Documentation

Three levels of documentation:
1. **README.md** - User guide with examples, troubleshooting
2. **BACKUP_RESTORE_IMPLEMENTATION.md** - Technical details
3. **Inline comments** - Code documentation

## Prerequisites

### MySQL/MariaDB
- `mysqldump` (backup)
- `mysql` (restore)

### PostgreSQL
- `pg_dump` (backup)
- `psql` (restore)

Install:
```bash
# Debian/Ubuntu
sudo apt-get install mysql-client postgresql-client

# macOS
brew install mysql-client postgresql

# Windows
Download from official websites
```

## Error Handling

Scripts handle:
- ✅ Missing database tools
- ✅ Invalid credentials
- ✅ Missing backup files
- ✅ Database connection errors
- ✅ User cancellation

Example:
```
ERROR: Backup file not found: old_backup.sql

Available backups:
  backup_20260416_120000.sql
  backup_20260415_100000.sql
```

## Summary

**Created fully functional backup/restore solution:**
- ✅ Zero-configuration (reads from config files)
- ✅ Cross-platform (Linux/Mac/Windows)
- ✅ Multi-database (MySQL/PostgreSQL)
- ✅ Production-ready
- ✅ Well-documented
- ✅ Error handling
- ✅ Security conscious

**Users can now:**
```bash
# Create backup
./backup.sh

# Restore when needed
./restore.sh backup_20260416_120000.sql
```

**No manual SQL execution required!**
