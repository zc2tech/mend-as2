# Tracker Message Timezone Fix Summary

## Issue Identified

There was a timezone mismatch between folder creation and database timestamp storage that could cause inconsistent auto-delete behavior.

## Problem Details

### Before Fix:

**Folder Creation (`TrackerMessageStoreHandler.java`):**
```java
LocalDateTime now = LocalDateTime.now();  // ← Used SYSTEM LOCAL timezone
String dateFolder = now.format(DATE_FORMAT);  // yyyyMMdd
```

**Database Timestamps (`TrackerMessageAccessDB.java`):**
```java
private final Calendar calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
stmt.setTimestamp(7, new Timestamp(info.getInitDate().getTime()), calendarUTC);  // ← Used UTC
```

### The Problem:

If server is in **UTC+8** timezone (e.g., Shanghai, Singapore):
- Message arrives at **2026-04-23 01:00 local** (which is **2026-04-22 17:00 UTC**)
- **Folder created:** `20260423` (based on local date)
- **DB timestamp:** `2026-04-22 17:00:00` UTC
- **Result:** Folder date ≠ UTC timestamp date (off by one day)

### Impact:

1. **Inconsistent cleanup**: Auto-delete might miss folders or delete wrong folders
2. **DST issues**: Daylight saving time changes could cause additional confusion
3. **Server migration**: Moving server to different timezone breaks folder/timestamp alignment

## Solution Implemented

### Change Made:

Updated `TrackerMessageStoreHandler.java` to use **UTC timezone** for folder creation:

```java
// BEFORE:
LocalDateTime now = LocalDateTime.now();

// AFTER:
LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
```

Also added import:
```java
import java.time.ZoneOffset;
```

### Result:

Now **everything uses UTC**:
1. ✅ Folder names: Created using UTC date (e.g., `20260423`)
2. ✅ Database timestamps: Stored in UTC (`initdateutc` column)
3. ✅ Auto-delete queries: Compare using UTC calendar

### Benefits:

- ✅ **Consistency**: Folder date always matches UTC timestamp date
- ✅ **No DST issues**: UTC doesn't observe daylight saving time
- ✅ **Portable**: Server can move timezones without breaking cleanup
- ✅ **Predictable**: Easy to debug and understand

## Testing

All test scripts updated to use UTC dates:

1. **`create_test_tracker_files.sh`**: Uses `date -u` to calculate UTC date folders
2. **`create_test_tracker_data.sql`**: Uses `UTC_TIMESTAMP()` instead of `NOW()`
3. **`verify_test_tracker_data.sh`**: Shows UTC dates and compares using UTC
4. **`cleanup_test_tracker_data.sh`**: Works with any UTC date folders

## Migration Notes

### For Existing Deployments:

If your server already has tracker messages stored with local timezone folder names:

1. **Old messages**: Will continue to work, but folder names might not match UTC dates
2. **New messages**: Will use UTC folder names going forward
3. **Auto-delete**: Will still work correctly because it extracts folder name from database `rawfilename`, not by calculating dates

### No Action Required:

The fix is **backward compatible**. Auto-delete logic doesn't assume folder names match any particular timezone - it simply:
1. Queries database for old messages
2. Extracts folder names from `rawfilename` column
3. Deletes those specific folders

So old local-timezone folders will still be deleted correctly based on their database timestamps.

## Files Modified

1. **`src/main/java/de/mendelson/comm/as2/tracker/TrackerMessageStoreHandler.java`**
   - Line 11: Added `import java.time.ZoneOffset;`
   - Line 60: Changed to `LocalDateTime.now(ZoneOffset.UTC)`
   - Line 59: Updated comment to mention UTC

2. **Test scripts updated:**
   - `create_test_tracker_files.sh` - Uses UTC date calculation
   - `create_test_tracker_data.sql` - Uses `UTC_TIMESTAMP()`
   - `verify_test_tracker_data.sh` - Shows UTC time reference
   - `cleanup_test_tracker_data.sh` - Dynamic folder detection
   - `TEST_TRACKER_AUTO_DELETE.md` - Added timezone documentation

## Verification

To verify the fix is working:

```bash
# 1. Start server
mvn exec:java -Dexec.mainClass="de.mendelson.comm.as2.AS2"

# 2. Send a test tracker message
curl -X POST http://localhost:8080/as2/tracker/testuser \
  -H "Content-Type: application/json" \
  -d '{"test":"data"}'

# 3. Check folder name matches UTC date
ls -la messages/tracker/

# Expected: Folder name should be today's UTC date (yyyyMMdd format)
# Example: If UTC date is 2026-04-23, folder should be 20260423/
```

Compare folder name with:
```bash
date -u +%Y%m%d  # Should match the folder name
```

## Conclusion

The timezone fix ensures consistent behavior across different server timezones and eliminates potential DST-related bugs. All tracker messages now use UTC for both storage and filesystem organization, following industry best practices.
