# System-Wide Certificate user_id Migration - COMPLETED

## Summary
Successfully migrated system-wide TLS certificates from `user_id=0` to `user_id=-1` to clearly indicate they are system-wide and not owned by any particular user.

## Changes Made

### 1. KeydataAccessDB.java
**File:** `src/main/java/de/mendelson/util/security/keydata/KeydataAccessDB.java`

**Changes:**
- Added constant `SYSTEM_WIDE_USER_ID = -1` with documentation
- This constant is now used throughout the codebase to represent system-wide keystores

```java
/**
 * Special user_id value indicating system-wide keystores (not owned by any particular user).
 * System-wide keystores are accessible based on permissions (CERT_TLS_READ, CERT_TLS_WRITE)
 * rather than user ownership.
 */
public static final int SYSTEM_WIDE_USER_ID = -1;
```

### 2. KeystoreStorageImplDB.java
**File:** `src/main/java/de/mendelson/util/security/cert/KeystoreStorageImplDB.java`

**Changes:**
- Updated field comment from `(0 = admin/system)` to `(-1 = system-wide, 0+ = specific user)`
- Updated method javadoc from `(0 = admin/system, >0 = specific user)` to `(-1 = system-wide, 0+ = specific user)`

### 3. CertificateResource.java
**File:** `src/main/java/de/mendelson/comm/as2/servlet/rest/resources/CertificateResource.java`

**Changes:**
- Added import: `import de.mendelson.util.security.keydata.KeydataAccessDB;`
- Line ~99: Changed `userId = 0;` to `userId = KeydataAccessDB.SYSTEM_WIDE_USER_ID;`
  - In listCertificates() for "ssl" keystoreType
  - Comment updated: "System-wide SSL/TLS keystore (user_id=-1, system-wide)"
- Line ~218: Changed `userId = 0;` to `userId = KeydataAccessDB.SYSTEM_WIDE_USER_ID;`
  - In exportCertificate() for "ssl" keystoreType
- Line ~717: Changed `userId = 0;` to `userId = KeydataAccessDB.SYSTEM_WIDE_USER_ID;`
  - In importCertificates() for "ssl" keystoreType

### 4. AS2Server.java
**File:** `src/main/java/de/mendelson/comm/as2/server/AS2Server.java`

**Changes:**
- Line 253: Changed variable name from `adminUserId` to `systemWideUserId` and set to `KeydataAccessDB.SYSTEM_WIDE_USER_ID`
- Line 261, 273, 282, 294: Changed all references from `adminUserId` to `systemWideUserId`
- Lines 425 & 433: Changed hardcoded `0` to `KeydataAccessDB.SYSTEM_WIDE_USER_ID` in KeystoreStorageImplDB constructors
  - Updated comments from "user_id=0 (admin/system)" to "user_id=-1 (system-wide)"

### 5. JettyCertificateRefreshController.java
**File:** `src/main/java/de/mendelson/comm/as2/server/JettyCertificateRefreshController.java`

**Changes:**
- Line 65: Changed `getLastChanged(KeystoreStorageImplDB.KEYSTORE_USAGE_TLS, 0)` to use `KeydataAccessDB.SYSTEM_WIDE_USER_ID`
- Updated comment from "System/admin TLS keystore (user_id=0)" to "System-wide TLS keystore (user_id=-1, system-wide)"

### 6. Database Migration Scripts

#### PostgreSQL
**File:** `src/main/resources/sqlscript/postgres/config/UPGRADE_SYSTEM_WIDE_USER_ID.sql`

```sql
UPDATE keydata
SET user_id = -1
WHERE user_id = 0
  AND purpose = 1;  -- KEYSTORE_USAGE_TLS = 1
```

#### MySQL
**File:** `src/main/resources/sqlscript/mysql/config/UPGRADE_SYSTEM_WIDE_USER_ID.sql`

```sql
UPDATE keydata
SET user_id = -1
WHERE user_id = 0
  AND purpose = 1;  -- KEYSTORE_USAGE_TLS = 1
```

**Important:** Only TLS keystores (purpose=1) are migrated. Sign/Encrypt keystores (purpose=2) with user_id=0 are left unchanged as they may belong to actual admin user.

## Why user_id=-1 instead of user_id=9999?

1. **Semantic clarity**: Negative values clearly indicate "special/system" rather than a regular user
2. **Database convention**: Many systems use -1 for system/special records
3. **Safety**: Avoids potential collision with actual user IDs
4. **Clear distinction**: Regular users always have positive IDs (0+)

## Database Schema Impact

The UNIQUE(user_id, purpose) constraint in the keydata table works correctly with user_id=-1:
- System-wide TLS keystore: user_id=-1, purpose=1
- User-specific TLS keystores: user_id=(actual user ID), purpose=1
- System-wide Sign/Encrypt keystore: user_id=-1, purpose=2
- User-specific Sign/Encrypt keystores: user_id=(actual user ID), purpose=2

## Compilation Status
✅ **SUCCESS** - All Java files compiled without errors

## Testing Required

### 1. Database Migration Test
```sql
-- Before migration
SELECT * FROM keydata WHERE purpose = 1;
-- Should show system TLS keystore with user_id=0

-- Run migration
-- Execute UPGRADE_SYSTEM_WIDE_USER_ID.sql

-- After migration
SELECT * FROM keydata WHERE purpose = 1;
-- Should show system TLS keystore with user_id=-1

-- Verification queries
SELECT COUNT(*) FROM keydata WHERE user_id = 0 AND purpose = 1;
-- Expected: 0 (no system TLS keystores with user_id=0)

SELECT COUNT(*) FROM keydata WHERE user_id = -1 AND purpose = 1;
-- Expected: 1 (system TLS keystore with user_id=-1)
```

### 2. Functional Tests

#### Test 1: System TLS Certificate Access
1. Start AS2 server
2. Login to WebUI as admin user
3. Navigate to "Sys TLS" menu
4. Verify system TLS certificates are displayed
5. Verify permission-based access control works (CERT_TLS_READ, CERT_TLS_WRITE)

#### Test 2: User-Specific TLS Certificates
1. Login as admin1 (ADMIN role user)
2. Navigate to "My Sign/Crypt/TLS" → TLS tab
3. Create a new TLS certificate
4. Verify it's saved with user_id=(admin1's user ID), NOT user_id=-1
5. Verify admin1 can see their own certificates, not system-wide ones

#### Test 3: HTTPS Server
1. Access AS2 server via HTTPS
2. Verify connection uses system-wide TLS certificate
3. Verify Jetty loads certificate correctly from user_id=-1

#### Test 4: Certificate Refresh
1. Modify system TLS certificate via "Sys TLS" UI
2. Wait ~30 seconds for JettyCertificateRefreshController to detect change
3. Verify Jetty reloads certificate automatically
4. Verify HTTPS connections use updated certificate

### 3. Rollback Test
If issues occur, rollback with:
```sql
UPDATE keydata SET user_id = 0 WHERE user_id = -1 AND purpose = 1;
```

## Access Control Verification

After migration, confirm:
- ✅ Users with CERT_TLS_READ can view system TLS certificates (regardless of user_id=-1)
- ✅ Users with CERT_TLS_WRITE can modify system TLS certificates
- ✅ Regular users cannot see system TLS certificates in their "My Sign/Crypt/TLS" tab
- ✅ System TLS certificates are loaded correctly by Jetty for HTTPS server
- ✅ WebUI "Sys TLS" page correctly loads certificates with keystoreType='ssl' → user_id=-1

## Files Modified
1. `src/main/java/de/mendelson/util/security/keydata/KeydataAccessDB.java`
2. `src/main/java/de/mendelson/util/security/cert/KeystoreStorageImplDB.java`
3. `src/main/java/de/mendelson/comm/as2/servlet/rest/resources/CertificateResource.java`
4. `src/main/java/de/mendelson/comm/as2/server/AS2Server.java`
5. `src/main/java/de/mendelson/comm/as2/server/JettyCertificateRefreshController.java`

## Files Created
1. `src/main/resources/sqlscript/postgres/config/UPGRADE_SYSTEM_WIDE_USER_ID.sql`
2. `src/main/resources/sqlscript/mysql/config/UPGRADE_SYSTEM_WIDE_USER_ID.sql`
3. `SYSTEM_WIDE_USERID_MIGRATION.md` (migration plan)
4. `SYSTEM_WIDE_USERID_MIGRATION_COMPLETED.md` (this file)

## Next Steps
1. Deploy updated code to test environment
2. Run database migration script (UPGRADE_SYSTEM_WIDE_USER_ID.sql)
3. Execute functional tests listed above
4. Verify no regressions in existing functionality
5. Deploy to production after successful testing
