# Admin User ID Migration from 0 to 1

## Background

Originally, the system hardcoded admin user with `user_id=0`. This caused tight coupling and special-case handling throughout the codebase. We changed admin to `user_id=1` to:
- Treat admin like any other user (no special hardcoding)
- Look up user IDs from the database instead of assuming admin=0
- Make the code more maintainable

## Convention

- `user_id = -1`: System-wide resources (shared across all users)
- `user_id = 1`: Admin user (default)
- `user_id > 1`: Regular users

## All Changes Made

### 1. Database Schema (CREATE.sql)

**Files:**
- `src/main/resources/sqlscript/postgres/config/CREATE.sql`
- `src/main/resources/sqlscript/mysql/config/CREATE.sql`

**Changes:**
- `webui_users`: Admin INSERT changed from `id=0` to `id=1`
- `keydata.user_id`: DEFAULT changed from `0` to `1`
- `partner.created_by_user_id`: DEFAULT changed from `0` to `1`
- Sequence/AUTO_INCREMENT: Changed to start from `2` instead of `1`
- Comments updated: "user_id: -1=system-wide, 1=admin (default), >1=other users"

### 2. Database Initialization Code

**Files:**
- `src/main/java/de/mendelson/comm/as2/database/DBDriverManagerMySQL.java`
- `src/main/java/de/mendelson/comm/as2/database/DBDriverManagerPostgreSQL.java`

**Changes:**
- When creating empty keystores during first-time initialization
- Changed from `user_id=0` to `user_id=1` for admin's TLS and ENC/SIGN keystores

### 3. SwingUI User ID Lookup

**File:** `src/main/java/de/mendelson/comm/as2/client/AS2Gui.java`

**Changes:**
- Removed hardcoded `if ("admin".equals(username)) return 0;`
- Now looks up ALL users from database via `UserListRequest`
- Returns actual `user.getId()` from `webui_users` table
- Removed `userId==0` checks for admin privileges
- Admin privileges now determined solely by `USER_MANAGE` permission

### 4. Server-Side Keystore Operations

**File:** `src/main/java/de/mendelson/comm/as2/server/AS2ServerProcessing.java`

**Changes:**
- Changed `if (userId > 0)` to `if (userId >= 0)` in multiple places:
  - Line ~1834: `processUploadRequestKeystore()` - load user-specific keystore
  - Line ~1696: `processDownloadRequestKeystore()` - load user-specific TLS keystore  
  - Line ~1900: `processUploadRequestKeystore()` - save user-specific keystore
- This ensures user_id=1 (admin) is treated as user-specific, not system-wide

## Remaining Intentional `userId > 0` Checks

Some places correctly keep `userId > 0` because they explicitly want to EXCLUDE admin:

1. **Message filtering** (AS2ServerProcessing.java ~line 2630):
   ```java
   if (userId > 0 && !hasUserManagePermission) {
       // Regular users see only their messages
       // Admin (userId=1 with USER_MANAGE permission) sees all
   }
   ```
   This is correct - admin with USER_MANAGE should see all messages.

## Migration Path for Existing Databases

If you have an existing database with admin at user_id=0:

### PostgreSQL:
```sql
BEGIN;
-- Create temporary user with id=1
INSERT INTO webui_users (id, username, password_hash, full_name, enabled) 
VALUES (1, 'temp', 'temp', 'Temporary', FALSE);

-- Move foreign key references
UPDATE webui_user_roles SET user_id = 1 WHERE user_id = 0;

-- Delete old admin and temp user
DELETE FROM webui_users WHERE id = 0;
DELETE FROM webui_users WHERE id = 1 AND username = 'temp';

-- Insert admin with id=1
INSERT INTO webui_users (id, username, password_hash, full_name, enabled, must_change_password) 
VALUES (1, 'admin', '75000#...', 'System Administrator', TRUE, TRUE);

-- Move keystores
UPDATE keydata SET user_id = 1 WHERE user_id = 0;

-- Move partner ownership
UPDATE partner SET created_by_user_id = 1 WHERE created_by_user_id = 0;

-- Reset sequence
ALTER SEQUENCE webui_users_id_seq RESTART WITH 2;
COMMIT;
```

### MySQL:
```sql
BEGIN;
UPDATE webui_user_roles SET user_id = 1 WHERE user_id = 0;
UPDATE webui_users SET id = 1 WHERE id = 0;
UPDATE keydata SET user_id = 1 WHERE user_id = 0;
UPDATE partner SET created_by_user_id = 1 WHERE created_by_user_id = 0;
ALTER TABLE webui_users AUTO_INCREMENT = 2;
COMMIT;
```

## Testing Checklist

After migration, verify:

- [ ] Admin can log into WebUI and SwingUI
- [ ] Admin can create TLS certificates in "My TLS"
- [ ] Admin can create Sign/Crypt certificates in "My Sign/Crypt"  
- [ ] Certificates appear after creation (not lost)
- [ ] Admin can view/edit partners
- [ ] Admin can create new users
- [ ] New users get user_id starting from 2
- [ ] Regular users can only see their own data
- [ ] Admin with USER_MANAGE permission can see all data

## Known Issues / Troubleshooting

**Problem:** Created certificate doesn't appear in the list
- **Cause:** Server saved to wrong keystore (user_id=-1 or user_id=0)
- **Fix:** Restart server after code changes, verify database has admin at user_id=1

**Problem:** SwingUI shows empty certificate list
- **Cause:** Code looking for user_id=0 but admin is at user_id=1
- **Fix:** Ensure all code changes applied, getUserIdFromUsername() returns correct ID

**Problem:** WebUI and SwingUI show different certificates
- **Cause:** Inconsistent user_id in database vs code
- **Fix:** Run migration SQL to align database with user_id=1 for admin
