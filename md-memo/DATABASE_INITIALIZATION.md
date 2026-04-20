# Database Initialization in mend-as2

## Summary
The CREATE.sql files are **automatically executed** by the program during startup. End users do **NOT** need to run them manually.

## How It Works

### Automatic Database Initialization Flow

1. **Server Startup** (`AS2Server.java`)
   ```
   AS2Server.start() 
     → ensureRunningDBServer()
       → dbServer.ensureServerIsRunning()
   ```

2. **Database Check** (`DBServerPostgreSQL.java` or `DBServerMySQL.java`)
   ```
   ensureServerIsRunning()
     → createCheck()
       → databaseExists(DB_CONFIG)?
       → databaseExists(DB_RUNTIME)?
   ```

3. **Table Creation** (`DBDriverManagerPostgreSQL.java` or `DBDriverManagerMySQL.java`)
   ```
   If tables don't exist:
     createDatabase(DB_TYPE)
       → SQLScriptExecutor.create()
         → executeScript(RESOURCE + "CREATE.sql")
   ```

### SQL Script Locations

**PostgreSQL:**
- Config DB: `src/main/resources/sqlscript/postgres/config/CREATE.sql`
- Runtime DB: `src/main/resources/sqlscript/postgres/runtime/CREATE.sql`

**MySQL:**
- Config DB: `src/main/resources/sqlscript/mysql/config/CREATE.sql`
- Runtime DB: `src/main/resources/sqlscript/mysql/runtime/CREATE.sql`

## Database Types

### 1. Config Database (CONFIG DB)
- Contains: partners, certificates, users, permissions, system configuration
- Tables: partner, keydata, webui_user, webui_permission, httpheader, etc.
- Script: `config/CREATE.sql`

### 2. Runtime Database (RUNTIME DB)
- Contains: messages, MDNs, statistics, logs
- Tables: messages, mdn, statistic, messagelog, etc.
- Script: `runtime/CREATE.sql`

## Initialization Logic

### First Run (No Tables Exist)
```
1. Server starts
2. Checks if VERSION table exists
3. If not → executes CREATE.sql
4. Creates all tables
5. Initializes empty keystores (for config DB only)
6. Inserts version record
```

### Subsequent Runs (Tables Exist)
```
1. Server starts
2. Checks if VERSION table exists
3. If yes → skips table creation
4. Checks if database upgrade needed
5. Executes upgrade scripts if needed
```

### PostgreSQL Special Case
```
1. Server tries to connect to database
2. If database doesn't exist → throws error:
   "PostgreSQL database as2_config_db does not exist.
    Please create it manually:
    CREATE DATABASE as2_config_db OWNER as2admin;"
3. User creates database manually
4. Server connects and creates tables automatically
```

### MySQL Special Case
```
1. Server tries to connect to database
2. If database doesn't exist → creates it automatically:
   "CREATE DATABASE IF NOT EXISTS as2_config_db"
3. Creates tables automatically
```

## Key Classes

### SQLScriptExecutor.java
- **Purpose:** Executes SQL scripts
- **Key Method:** `create(Connection, RESOURCE, dbVersion, productVersion)`
- **Functionality:**
  - Reads CREATE.sql from classpath resources
  - Parses SQL statements (handles comments, dollar quotes, semicolons)
  - Executes each statement
  - Inserts version record

### DBDriverManagerPostgreSQL.java / DBDriverManagerMySQL.java
- **Purpose:** Database connection and initialization
- **Key Method:** `createDatabase(int DB_TYPE)`
- **Functionality:**
  - Checks if tables exist (VERSION table check)
  - If not → calls SQLScriptExecutor.create()
  - Initializes empty keystores (PKCS12 for sign/encrypt, JKS for TLS)

### DBServerPostgreSQL.java / DBServerMySQL.java
- **Purpose:** Database server lifecycle management
- **Key Method:** `ensureServerIsRunning()`
- **Functionality:**
  - Called during AS2 server startup
  - Checks if databases exist
  - Creates tables if needed
  - Runs ANALYZE for optimization
  - Performs database upgrades

## Version Management

After creating tables, the system:
1. Inserts a record into `version` table:
   ```sql
   INSERT INTO version(actualversion, updatedate, updatecomment)
   VALUES(?, ?, ?)
   ```
2. Stores current DB version (e.g., 15 for config, 10 for runtime)
3. Uses this for upgrade detection on subsequent runs

## Upgrade Process

When tables exist:
1. Read current version from `version` table
2. Compare with required version (from `AS2ServerVersion.java`)
3. If different → execute upgrade scripts:
   - `UPGRADE_USER_SCOPED.sql`
   - `UPGRADE_PERMISSIONS.sql`
   - `UPGRADE_INBOUND_AUTH.sql`
   - `UPGRADE_MULTI_INBOUND_AUTH.sql`
   - `UPGRADE_SYSTEM_WIDE_USER_ID.sql`
   - etc.

## Empty Keystore Initialization

After creating config DB tables:
```java
initializeEmptyKeystores(connection)
  → Creates empty PKCS12 keystore for sign/encrypt (user_id=-1, purpose=2)
  → Creates empty JKS keystore for TLS (user_id=-1, purpose=1)
  → Inserts into keydata table
```

This ensures keystores exist even before any certificates are imported.

## Manual Intervention Required

### PostgreSQL Only
**User must manually create the database:**
```sql
-- As PostgreSQL superuser or database owner
CREATE DATABASE as2_config_db OWNER as2admin;
CREATE DATABASE as2_runtime_db OWNER as2admin;
```

**Program automatically creates tables** after database exists.

### MySQL
**Everything is automatic:**
- Database creation: automatic
- Table creation: automatic
- Keystore initialization: automatic

## Summary

| Task | PostgreSQL | MySQL | Manual? |
|------|-----------|-------|---------|
| Create database | ❌ Manual | ✅ Auto | PostgreSQL only |
| Create tables | ✅ Auto | ✅ Auto | No |
| Initialize keystores | ✅ Auto | ✅ Auto | No |
| Run CREATE.sql | ✅ Auto | ✅ Auto | No |
| Run upgrade scripts | ✅ Auto | ✅ Auto | No |

## Conclusion

**End users do NOT need to run any SQL scripts manually** (except creating the PostgreSQL database itself). The mend-as2 server:
- ✅ Automatically detects if tables exist
- ✅ Automatically executes CREATE.sql on first run
- ✅ Automatically initializes empty keystores
- ✅ Automatically runs upgrade scripts
- ✅ All packaged inside the JAR file (no external SQL files needed)

The CREATE.sql files are resources embedded in the application JAR and are executed programmatically during server startup.
