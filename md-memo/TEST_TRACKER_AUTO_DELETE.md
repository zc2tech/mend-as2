# Tracker Message Auto-Delete Testing

This directory contains scripts to test the automatic deletion of old tracker messages.

## Overview

The AS2 server has an auto-delete feature that removes tracker messages older than a configured number of days (default: 5 days). This test suite creates dummy data that should be automatically deleted.

**IMPORTANT:** All dates use **UTC timezone** to ensure consistency between:
- Database timestamp storage (`initdateutc` column)
- Folder creation (yyyyMMdd format folders)
- Auto-delete logic

## Test Configuration

Current auto-delete settings (in `as2_db_config.serversettings`):
- `autotrackerdelete` = TRUE (enabled)
- `autotrackerdeleteolderthan` = 5 (days)

The auto-delete process runs **every minute** via `TrackerMessageDeleteController`.

## Timezone Handling

### Why UTC?

The system uses UTC for consistency:
1. **Database timestamps**: Stored in UTC in the `initdateutc` column
2. **Folder names**: Created using UTC date (yyyyMMdd format)
3. **Auto-delete cutoff**: Calculated in UTC

This ensures that:
- A message stored on "2026-04-23 UTC" goes into folder `20260423/`
- When checking if it's older than 5 days, the comparison is UTC-to-UTC
- No timezone conversion errors or DST issues

### Code Implementation

**TrackerMessageStoreHandler.java:**
```java
LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);  // ← Uses UTC
String dateFolder = now.format(DATE_FORMAT);  // yyyyMMdd
```

**TrackerMessageAccessDB.java:**
```java
private final Calendar calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
stmt.setTimestamp(1, new Timestamp(cutoffDate.getTime()), calendarUTC);  // ← Uses UTC
```

## Test Scripts

### 1. `create_test_tracker_files.sh`
Creates test tracker message files in the filesystem:
- Dynamically calculates UTC date folders for 10, 8, and 6 days ago
- Creates 3 files in the 10-day-old folder
- Creates 2 files in the 8-day-old folder
- Creates 1 file in the 6-day-old folder

All files are older than the 5-day retention policy.

**Example output:**
```
Creating test tracker message files using UTC dates...
  10 days ago UTC: 20260413
  8 days ago UTC: 20260415
  6 days ago UTC: 20260417
```

### 2. `create_test_tracker_data.sql`
Inserts corresponding test records into the database:
- 6 test messages with IDs: `TEST-MSG-001` through `TEST-MSG-006`
- Uses `UTC_TIMESTAMP()` for date calculations
- Folder names in `rawfilename` match UTC dates calculated by SQL
- Mix of auth statuses and users for variety

**Key SQL features:**
```sql
SET @test_date_10days_ago = DATE_SUB(UTC_TIMESTAMP(), INTERVAL 10 DAY);
SET @folder_10days_ago = DATE_FORMAT(@test_date_10days_ago, '%Y%m%d');
-- rawfilename: CONCAT('tracker/', @folder_10days_ago, '/...')
```

### 3. `verify_test_tracker_data.sh`
Verification script that shows:
- Test messages in database with age calculation
- Test files in filesystem
- Current auto-delete settings
- Summary of what should be deleted

### 4. `cleanup_test_tracker_data.sh`
Cleanup script to remove all test data:
- Deletes test messages from database
- Dynamically finds and removes test files/folders from filesystem
- Safe: only removes items matching `TEST-MSG-*` pattern
- Works with any UTC date folders created by the test scripts

## Testing Process

### Step 1: Create Test Data
```bash
# Create test files
./create_test_tracker_files.sh

# Insert test database records
mysql -h localhost -P 3306 -u as2user -p'as2password' < create_test_tracker_data.sql
```

### Step 2: Verify Test Data Exists
```bash
./verify_test_tracker_data.sh
```

You should see:
- 6 test messages in database (all marked "SHOULD DELETE")
- 3 date folders with test files in filesystem

### Step 3: Start Server and Wait
```bash
# Start the AS2 server (if not already running)
mvn exec:java -Dexec.mainClass="de.mendelson.comm.as2.AS2"

# Wait 1-2 minutes for auto-delete to run
```

The `TrackerMessageDeleteController` runs every minute and will:
1. Query for messages older than 5 days
2. Delete database records in a transaction
3. Delete entire date folders from filesystem

### Step 4: Verify Deletion
```bash
./verify_test_tracker_data.sh
```

Expected results after auto-delete:
- ✅ All test messages removed from database (0 records)
- ✅ UTC date folders containing test files deleted
- ✅ Server logs show: "Tracker messages deleted: 6 records"

**Note:** The exact folder names will depend on when you run the test (calculated dynamically in UTC).

### Step 5: Cleanup (if needed)
```bash
# If auto-delete didn't work or you want to manually cleanup
./cleanup_test_tracker_data.sh
```

## Implementation Details

### Database Schema
Table: `as2_db_runtime.tracker_message`
- `id` - Primary key
- `messageid` - Message identifier
- `tracker_id` - Unique tracker ID
- `initdateutc` - Creation timestamp (used for age calculation)
- `rawfilename` - Relative path to file (e.g., `tracker/20260413/...`)
- Other fields: `auth_status`, `auth_user`, `payload_format`, etc.

### File Structure
```
messages/tracker/
├── 20260413/          ← Example: 10 days ago UTC (actual date varies)
│   ├── 10-00-00-001_TRACKER_test-tracker-001.msg
│   ├── 10-00-01-002_TRACKER_test-tracker-002.msg
│   └── 10-00-02-003_TRACKER_test-tracker-003.msg
├── 20260415/          ← Example: 8 days ago UTC
│   ├── 10-00-00-004_TRACKER_test-tracker-004.msg
│   └── 10-00-01-005_TRACKER_test-tracker-005.msg
└── 20260417/          ← Example: 6 days ago UTC
    └── 10-00-00-006_TRACKER_test-tracker-006.msg
```

**Note:** Actual folder names depend on current UTC date when tests are created.

### Auto-Delete Logic
Located in: `de.mendelson.comm.as2.timing.TrackerMessageDeleteController`

```java
// Runs every minute
if (preferences.getBoolean(PreferencesAS2.AUTO_TRACKER_DELETE)) {
    long olderThanDays = preferences.getInt(PreferencesAS2.AUTO_TRACKER_DELETE_OLDERTHAN);
    Date cutoffDate = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(olderThanDays));
    
    // Delete messages older than cutoff
    int deletedCount = trackerMessageAccess.deleteTrackerMessagesOlderThan(cutoffDate);
}
```

The `deleteTrackerMessagesOlderThan()` method:
1. Queries database for distinct date folders from old messages
2. Deletes database records in transaction
3. Calls `TrackerMessageStoreHandler.deleteTrackerFolder()` for each date folder
4. Deletes entire folders (not individual files) for efficiency

## Troubleshooting

### Test data not being deleted?

Check server logs for errors:
```bash
tail -f logs/server.log | grep -i tracker
```

Verify settings:
```bash
mysql -h localhost -P 3306 -u as2user -p'as2password' -D as2_db_config \
  -e "SELECT * FROM serversettings WHERE vkey LIKE '%tracker%'"
```

### Date folders not being deleted?

Check folder permissions:
```bash
ls -la messages/tracker/
```

Verify folder structure matches database `rawfilename` paths.

### Need to change retention period?

Update via SwingUI:
1. Open Preferences → System Maintenance
2. Change "Auto delete tracker messages older than" to desired days
3. Click OK

Or update database directly:
```sql
UPDATE serversettings 
SET vvalue = '3' 
WHERE vkey = 'autotrackerdeleteolderthan';
```

## Notes

- Auto-delete only runs when `autotrackerdelete` = TRUE
- The delete controller runs every 60 seconds
- Date folders are deleted entirely (all files for that date)
- Transaction ensures database/filesystem consistency
- Test messages use prefix `TEST-MSG-` for easy identification
