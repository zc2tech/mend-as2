# Database Migration - Version 2 to 3

## Summary

This migration adds the `user_api_response_rules` table to support dynamic REST API response configuration per user.

## Changes

### 1. Database Schema Version
- **Old Version**: 2
- **New Version**: 3
- **Updated**: `AS2ServerVersion.getRequiredDBVersionConfig()` from 2 to 3

### 2. New Table: `user_api_response_rules`

Stores custom response rules per user for the REST API endpoint (/as2/userapi/{username}/*).

**Columns**:
- `id` - Primary key (auto-increment)
- `user_id` - Foreign key to `webui_users(id)`
- `priority` - Rule evaluation order (lower = higher priority)
- `enabled` - Enable/disable rule
- `http_method` - HTTP method (GET, POST, PUT, DELETE, or *)
- `path_pattern` - Path pattern to match
- `path_match_type` - Match type (exact, prefix, wildcard, regex)
- `status_code` - HTTP status code to return
- `content_type` - Response content type
- `response_body` - Response body (supports variables: ${path}, ${method}, ${requestId}, ${1}, ${groupName})
- `created_at` - Timestamp when rule was created
- `updated_at` - Timestamp when rule was last updated

**Indexes**:
- `idx_user_api_response_user_priority` - On (user_id, priority)
- `idx_user_api_response_user_enabled` - On (user_id, enabled)

**Constraints**:
- Foreign key on `user_id` references `webui_users(id)` with CASCADE DELETE

## Migration Scripts

### MySQL
- **Location**: `src/main/resources/sqlscript/mysql/config/update2to3.sql`
- **CREATE.sql Updated**: Version set to 3, table added

### PostgreSQL
- **Location**: `src/main/resources/sqlscript/postgres/config/update2to3.sql`
- **CREATE.sql Updated**: Version set to 3, table added
- **Additional**: Trigger function for `updated_at` column (PostgreSQL doesn't have ON UPDATE CURRENT_TIMESTAMP)

## Automatic Migration

The system will **automatically** migrate the database on server startup:

1. Server checks current DB version from `version` table
2. If current version (2) < required version (3), migration runs
3. Executes `update2to3.sql` script
4. Updates version table to 3
5. Server starts normally

## Manual Migration (if needed)

If automatic migration fails, you can run manually:

### MySQL
```bash
mysql -h localhost -P 3306 -u as2user -pas2password as2_db_config \
  < src/main/resources/sqlscript/mysql/config/update2to3.sql
```

### PostgreSQL
```bash
psql -h localhost -p 5432 -U as2user -d as2_db_config \
  -f src/main/resources/sqlscript/postgres/config/update2to3.sql
```

## Verification

Check that the migration completed successfully:

### MySQL
```bash
mysql -h localhost -P 3306 -u as2user -pas2password as2_db_config \
  -e "SELECT actualversion, updatedate, updatecomment FROM version;"

mysql -h localhost -P 3306 -u as2user -pas2password as2_db_config \
  -e "DESCRIBE user_api_response_rules;"
```

### PostgreSQL
```bash
psql -h localhost -p 5432 -U as2user -d as2_db_config \
  -c "SELECT actualversion, updatedate, updatecomment FROM version;"

psql -h localhost -p 5432 -U as2user -d as2_db_config \
  -c "\d user_api_response_rules"
```

Expected version: **3**

## Rollback (Not Recommended)

If you need to rollback (removes all response rules):

### MySQL
```sql
DROP TABLE IF EXISTS user_api_response_rules;
UPDATE version SET actualversion = 2, 
  updatedate = NOW(), 
  updatecomment = 'Rolled back to version 2'
WHERE id = (SELECT MAX(id) FROM version);
```

### PostgreSQL
```sql
DROP TABLE IF EXISTS user_api_response_rules;
UPDATE version SET actualversion = 2, 
  updatedate = NOW(), 
  updatecomment = 'Rolled back to version 2'
WHERE id = (SELECT MAX(id) FROM version);
```

**Warning**: This will delete all user-configured response rules!

## Files Changed

### Java
1. `AS2ServerVersion.java`
   - `getRequiredDBVersionConfig()` changed from 2 to 3

### SQL Scripts
1. `src/main/resources/sqlscript/mysql/config/update2to3.sql` - **NEW**
2. `src/main/resources/sqlscript/mysql/config/CREATE.sql` - Updated (version 3, added table)
3. `src/main/resources/sqlscript/postgres/config/update2to3.sql` - **NEW**
4. `src/main/resources/sqlscript/postgres/config/CREATE.sql` - Updated (version 3, added table)

### Removed
1. `dev-scripts/create_user_api_response_rules_table.sql` - Deleted (moved to proper sqlscript location)

## Testing

After migration, test that:

1. ✅ Server starts without errors
2. ✅ Database version is 3
3. ✅ `user_api_response_rules` table exists
4. ✅ Can insert/update/delete rules via REST API
5. ✅ Rules are evaluated correctly when making API requests

## Fresh Installation

For fresh installations (no existing database):
- `CREATE.sql` scripts already include the `user_api_response_rules` table
- Database will be created at version 3 directly
- No migration needed

## Notes

- Migration is **idempotent** - safe to run multiple times (uses `IF NOT EXISTS`)
- Original `update2to3.sql` only adds the table, doesn't modify existing data
- PostgreSQL version includes trigger for `updated_at` column auto-update
- MySQL uses `ON UPDATE CURRENT_TIMESTAMP` for `updated_at` column
- Both versions support ON DELETE CASCADE for user_id foreign key
