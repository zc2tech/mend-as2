# README.md Updates Summary

## Changes Made

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
