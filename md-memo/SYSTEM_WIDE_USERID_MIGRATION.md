# System-Wide Certificate user_id Migration Plan

## Problem Statement
Currently, system-wide TLS certificates are stored with `user_id=0` in the `keydata` table. This is conceptually incorrect because:
- `user_id=0` implies the certificates belong to the admin user
- System-wide certificates should not be associated with any particular user
- This creates confusion about ownership and access control

## Proposed Solution
Change system-wide certificates to use `user_id=-1` to clearly indicate they are system-wide and not owned by any user.

**Why -1 instead of 9999:**
- Negative values semantically indicate "special/system" records
- Common database convention (many systems use -1 for system records)
- Avoids potential collision with actual user IDs
- Clear distinction from regular user records (which are always positive)

## Impact Analysis

### Files That Need Changes

1. **KeystoreStorageImplDB.java** (line 63 comment)
   - Update comment from `(0 = admin/system)` to `(-1 = system-wide, 0+ = specific user)`

2. **KeydataAccessDB.java**
   - Add constant: `public static final int SYSTEM_WIDE_USER_ID = -1;`
   - Update all references to use constant

3. **CertificateResource.java** (lines 73, 99, 218, 717)
   - Replace hardcoded `0` with `KeydataAccessDB.SYSTEM_WIDE_USER_ID` for ssl keystoreType
   - Lines to change:
     - Line 99: `userId = 0;` → `userId = KeydataAccessDB.SYSTEM_WIDE_USER_ID;`
     - Line 218: `userId = 0;` → `userId = KeydataAccessDB.SYSTEM_WIDE_USER_ID;`
     - Line 717: `userId = 0;` → `userId = KeydataAccessDB.SYSTEM_WIDE_USER_ID;`

4. **AS2Server.java** (lines 252, 418, 425, 433)
   - Replace hardcoded `0` with `KeydataAccessDB.SYSTEM_WIDE_USER_ID` for system keystores
   - Update comments from "user_id=0" to "user_id=-1 (system-wide)"

5. **JettyCertificateRefreshController.java** (line 64)
   - Replace hardcoded `0` with `KeydataAccessDB.SYSTEM_WIDE_USER_ID`
   - Update comment from "user_id=0" to "user_id=-1 (system-wide)"

### Database Migration Script

**PostgreSQL** (`src/main/resources/sqlscript/postgres/config/UPGRADE.sql`):
```sql
-- Migrate system-wide keystores from user_id=0 to user_id=-1
-- This makes it clear that system-wide certificates are not owned by any particular user
UPDATE keydata SET user_id = -1 WHERE user_id = 0 AND purpose = 1;
```

**MySQL** (`src/main/resources/sqlscript/mysql/config/UPGRADE.sql`):
```sql
-- Migrate system-wide keystores from user_id=0 to user_id=-1
-- This makes it clear that system-wide certificates are not owned by any particular user
UPDATE keydata SET user_id = -1 WHERE user_id = 0 AND purpose = 1;
```

**Notes:**
- `purpose = 1` is `KEYSTORE_USAGE_TLS` (TLS keystores)
- Only migrate TLS keystores because those are the system-wide ones
- Sign/Encrypt keystores (`purpose = 2`) with `user_id=0` likely belong to actual admin user, leave them alone
- The UNIQUE(user_id, purpose) constraint will work fine with user_id=-1

### Testing Strategy

1. **Before Migration:**
   ```sql
   SELECT * FROM keydata WHERE purpose = 1;
   ```
   Should show system TLS keystore with user_id=0

2. **Run Migration:**
   Execute the UPDATE statement

3. **After Migration:**
   ```sql
   SELECT * FROM keydata WHERE purpose = 1;
   ```
   Should show system TLS keystore with user_id=-1

4. **Functional Test:**
   - Start AS2 server → should load system TLS certificates successfully
   - Access WebUI "Sys TLS" page → should show system certificates
   - Test HTTPS connection to server → should work with system certificates
   - Admin user accesses "My Sign/Crypt/TLS" → should NOT see system certificates
   - Admin user creates own TLS cert → should be stored with user_id=(actual user ID)

5. **Verification Queries:**
   ```sql
   -- Should return 0 (no system keystores with user_id=0)
   SELECT COUNT(*) FROM keydata WHERE user_id = 0 AND purpose = 1;
   
   -- Should return 1 (system TLS keystore with user_id=-1)
   SELECT COUNT(*) FROM keydata WHERE user_id = -1 AND purpose = 1;
   ```

## Implementation Order

1. ✅ Create this migration plan document
2. ⬜ Add SYSTEM_WIDE_USER_ID constant to KeydataAccessDB.java
3. ⬜ Update all Java files to use constant instead of hardcoded 0
4. ⬜ Compile and verify no compilation errors
5. ⬜ Add migration SQL to both PostgreSQL and MySQL UPGRADE.sql
6. ⬜ Test on development database
7. ⬜ Verify system TLS functionality after migration
8. ⬜ Document changes in release notes

## Rollback Plan
If issues occur, rollback is simple:
```sql
-- PostgreSQL/MySQL
UPDATE keydata SET user_id = 0 WHERE user_id = -1 AND purpose = 1;
```

## Access Control Verification
After migration, confirm that:
- Users with CERT_TLS_READ can view system TLS certificates (regardless of user_id=-1)
- Users with CERT_TLS_WRITE can modify system TLS certificates
- Regular users cannot see system TLS certificates in their "My Sign/Crypt/TLS" tab
- System TLS certificates are loaded correctly by Jetty for HTTPS server

The access control logic should work correctly because:
- "Sys TLS" page explicitly loads certificates with `keystoreType='ssl'` (no user filtering)
- CertificateResource.java will now use `userId=-1` for ssl keystoreType
- Permission checks (CERT_TLS_READ, CERT_TLS_WRITE) are independent of user_id value
