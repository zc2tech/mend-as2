# API System Database Migration Guide

Migration and rollback scripts are now organized in **config** and **runtime** folders.

## File Structure

```
mysql/
├── config/
│   ├── migration_user_api_auth_config.sql
│   └── rollback_user_api_auth.sql
├── runtime/
│   ├── migration_user_api_auth_runtime.sql
│   └── rollback_user_api_auth.sql
└── migration_user_api_auth.sql (combined - if both DBs are the same)

postgres/
├── config/
│   ├── migration_user_api_auth_config.sql
│   └── rollback_user_api_auth.sql
├── runtime/
│   ├── migration_user_api_auth_runtime.sql
│   └── rollback_user_api_auth.sql
└── migration_user_api_auth.sql (combined - if both DBs are the same)
```

## How to Run

### MySQL (Separate Databases)

```bash
# 1. Run CONFIG migration
mysql -u your_user -p your_config_database < mysql/config/migration_user_api_auth_config.sql

# 2. Run RUNTIME migration
mysql -u your_user -p your_runtime_database < mysql/runtime/migration_user_api_auth_runtime.sql
```

### MySQL (Same Database)

```bash
# If CONFIG and RUNTIME tables are in the same database
mysql -u your_user -p your_database < mysql/migration_user_api_auth.sql
```

### PostgreSQL (Separate Databases)

```bash
# 1. Run CONFIG migration
psql -U your_user -d your_config_database -f postgres/config/migration_user_api_auth_config.sql

# 2. Run RUNTIME migration
psql -U your_user -d your_runtime_database -f postgres/runtime/migration_user_api_auth_runtime.sql
```

### PostgreSQL (Same Database)

```bash
# If CONFIG and RUNTIME tables are in the same database
psql -U your_user -d your_database -f postgres/migration_user_api_auth.sql
```

## What Gets Created

### CONFIG Database
- **New columns in `webui_users`**:
  - `api_auth_basic_enabled` (BOOLEAN)
  - `api_auth_cert_enabled` (BOOLEAN)
- **New table `user_api_auth_credentials`**: Stores user API authentication credentials

### RUNTIME Database
- **New table `api_request_log`**: Stores all API request details
- **New table `api_auth_failure`**: Tracks failed authentication attempts

## Verification

After running the migrations, verify success:

### CONFIG Database
```sql
-- Check columns exist
SELECT api_auth_basic_enabled, api_auth_cert_enabled 
FROM webui_users LIMIT 1;

-- Check table exists
SELECT COUNT(*) FROM user_api_auth_credentials;
```

### RUNTIME Database
```sql
-- Check tables exist
SELECT COUNT(*) FROM api_request_log;
SELECT COUNT(*) FROM api_auth_failure;
```

## Rollback

If you need to undo these changes, use:
- `rollback_user_api_auth.sql` (drops all API-related tables and columns)

⚠️ **WARNING**: Rollback will delete all API authentication data and request logs!

## Backup First!

Always backup your databases before running migrations:

```bash
# MySQL
mysqldump -u user -p database_name > backup_before_api_migration.sql

# PostgreSQL
pg_dump -U user database_name > backup_before_api_migration.sql
```
