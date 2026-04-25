# README.md Updates Summary

## Latest Changes (Content-Type Auto-Fill)

Updated README.md to document the new **Content-Type Auto-Fill** feature in manual message sending.

### Sections Updated:

1. **Usage Section - Tracker Endpoint** (after line ~610)
   - Added new "Sending AS2 Messages (WebUI)" subsection
   - Documented 7-step process for manual message sending
   - Highlighted content type auto-fill from receiver partner configuration
   - Explained fallback behavior (auto-detect from extension for additional files)

2. **WebUI Access Section - Messages** (line ~638)
   - Added bullet: "Manual send with content type auto-fill from receiver partner configuration"

## Key Messages Conveyed:

- ✅ Content Type field automatically fills with selected receiver's configured content type
- ✅ User can optionally override the auto-filled value
- ✅ First file uses specified/auto-filled content type
- ✅ Additional files use auto-detected content type from file extension
- ✅ Improves user experience by reducing manual configuration

## Implementation Details:

**Backend Flow:**
1. `Partner.getContentType()` - Returns partner's configured content type
2. `AS2MessageCreation.java:458-468` - Fallback logic uses receiver's content type if not specified
3. `MessageHttpUploader.java:706` - Sets Content-Type HTTP header

**Frontend:**
- `ManualSend.jsx:88-99` - Added useEffect hook that watches receiverPartnerId
- Automatically updates contentType state when receiver changes
- Sets to empty string if no receiver selected or receiver has no content type

**Priority Order:**
1. User-specified content type (first file only)
2. Auto-detected from file extension (additional files)
3. Partner configuration content type (fallback)

---

## Previous Changes (User-Specific Tracker Authentication)

Updated README.md to document the new **User-Specific Tracker Authentication** feature.

### Sections Updated:

1. **Features Section** (line ~107)
   - Expanded "Tracker Messages" bullet to include user-specific authentication details
   - Added description of Basic Auth + Certificate Auth with OR logic
   - Noted that system-wide URL is deprecated

2. **Tracker Endpoint Section** (lines ~496-543)
   - Complete rewrite with detailed user-specific authentication documentation
   - Added step-by-step configuration instructions for WebUI
   - Updated example curl commands for both auth types
   - Clarified behavior differences between user-specific and system-wide URLs
   - Added certificate auth example

3. **WebUI Access Section** (lines ~581-600)
   - Updated System → Tracker Conf to show as DEPRECATED
   - Added "My Tracker Conf" menu item under Preferences
   - Listed dual authentication types (Basic Auth + Certificate Auth)

4. **Roadmap Section** (lines ~890-896)
   - Added completed items:
     - User-specific tracker authentication (Basic + Certificate Auth)
     - My Tracker Conf menu (WebUI user dropdown)
     - Database migration scripts for user tracker auth

5. **Troubleshooting Section** (lines ~945-953)
   - Added new "Tracker Authentication Not Working" section
   - Documented configuration location and URL formats
   - Clarified that old system-wide config is no longer used
   - Provided debugging tips for both Basic and Certificate auth

## Key Messages Conveyed:

- ✅ Each user independently configures their tracker authentication
- ✅ User-specific URL: `/as2/tracker/{username}` (with auth)
- ✅ System-wide URL: `/as2/tracker` (no auth, deprecated)
- ✅ Configuration via "My Tracker Conf" menu in WebUI user dropdown
- ✅ Support for both Basic Auth and Certificate Auth
- ✅ OR logic: Pass if ANY enabled credential matches
- ✅ Old system-wide "Require Authentication" config is obsolete
- ✅ Database migration scripts provided for existing installations

## Migration Scripts Referenced:

- `/Users/I572958/SAPDevelop/github/mend-as2/src/main/resources/sqlscript/postgres/migration_user_tracker_auth.sql`
- `/Users/I572958/SAPDevelop/github/mend-as2/src/main/resources/sqlscript/mysql/migration_user_tracker_auth.sql`
- `/Users/I572958/SAPDevelop/github/mend-as2/src/main/resources/sqlscript/postgres/rollback_user_tracker_auth.sql`
- `/Users/I572958/SAPDevelop/github/mend-as2/src/main/resources/sqlscript/mysql/rollback_user_tracker_auth.sql`

## Files Modified:

- ✅ README.md - Complete documentation of user-specific tracker authentication feature

All documentation is consistent with the implementation in:
- Backend: `UserTrackerAuthResource.java`, `UserTrackerAuthDB.java`, `UserTrackerAuthCredential.java`, `TrackerServlet.java`
- Frontend: `MyTrackerConfig.jsx`, `Layout.jsx`, `App.jsx`
- Database: Migration and rollback scripts for PostgreSQL and MySQL
