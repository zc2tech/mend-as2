# Database Migration: Add AS2 ID Unique Constraint

## Issue
The `partner.as2ident` column currently has no UNIQUE constraint, which could theoretically allow duplicate AS2 IDs in the database, even though the application code validates uniqueness.

## Solution
Add a UNIQUE constraint to enforce AS2 ID uniqueness at the database level.

## Application-Level Validation (Already Exists)
The application already validates AS2 ID uniqueness in `AS2ServerProcessing.java`:
- **On partner creation**: Checks if AS2 ID already exists and throws exception if duplicate
- **On partner update**: Validates the partner exists and checks for name changes

## How to Apply Migration

### Option 1: Using the SQL Script
Execute the migration script against your database:

```bash
psql -U <username> -d <database_name> -f src/main/resources/sqlscript/config/ADD_UNIQUE_CONSTRAINT_AS2IDENT.sql
```

### Option 2: Manual SQL
Connect to your database and run:

```sql
-- Check for existing duplicates first
SELECT as2ident, COUNT(*) FROM partner GROUP BY as2ident HAVING COUNT(*) > 1;

-- If no duplicates, add the constraint
ALTER TABLE partner ADD CONSTRAINT partner_as2ident_unique UNIQUE(as2ident);

-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_partner_as2ident ON partner(as2ident);
```

## What if Duplicates Exist?

If you have existing duplicate AS2 IDs (which shouldn't happen due to application validation), you'll need to resolve them first:

```sql
-- Find duplicates
SELECT as2ident, id, partnername, islocal 
FROM partner 
WHERE as2ident IN (
    SELECT as2ident FROM partner GROUP BY as2ident HAVING COUNT(*) > 1
)
ORDER BY as2ident, id;

-- Then manually resolve by either:
-- 1. Deleting duplicates: DELETE FROM partner WHERE id = <duplicate_id>;
-- 2. Updating to unique AS2 ID: UPDATE partner SET as2ident = '<new_unique_id>' WHERE id = <duplicate_id>;
```

## Impact
- **Breaking**: If somehow duplicate AS2 IDs exist, the migration will fail until resolved
- **Performance**: Minimal - adds one index and constraint
- **Compatibility**: PostgreSQL 9.6+ (uses DO blocks and CREATE INDEX IF NOT EXISTS)

## Testing
After applying the migration:

1. Try creating a partner with duplicate AS2 ID via WebUI - should fail with error
2. Verify index exists:
   ```sql
   \d partner
   ```
   Should show `partner_as2ident_unique` constraint and `idx_partner_as2ident` index

## Rollback
To remove the constraint:

```sql
ALTER TABLE partner DROP CONSTRAINT IF EXISTS partner_as2ident_unique;
DROP INDEX IF EXISTS idx_partner_as2ident;
```

## Related Files
- SQL Script: `src/main/resources/sqlscript/config/ADD_UNIQUE_CONSTRAINT_AS2IDENT.sql`
- Application Validation: `src/main/java/de/mendelson/comm/as2/server/AS2ServerProcessing.java` (lines in processPartnerAddRequest method)
- REST API: `src/main/java/de/mendelson/comm/as2/servlet/rest/resources/PartnerResource.java`
