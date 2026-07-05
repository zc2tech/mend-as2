# API Split: SYS_API vs USER_API

## Date
2026-07-04

## Summary

Split the original single `API` endpoint (`TARGET_API`) into two distinct endpoints for better access control and separation of concerns:

1. **SYS_API** (`TARGET_SYS_API`) - System REST API for controlling the AS2 server
2. **USER_API** (`TARGET_USER_API`) - User customized endpoint for receiving external API requests

## Motivation

The original single API endpoint mixed two very different use cases:
- Administrative/control APIs for managing the AS2 server
- User-facing APIs for receiving external requests (webhooks, data ingestion, etc.)

Having separate target types allows administrators to:
- Apply different IP whitelist rules for each type
- Enable/disable each independently
- Better audit and monitor different types of API traffic

## Changes

### URL Paths

**Before:**
- System API: `/as2/api/v1/*`
- User API: `/as2/api/{username}/*`

**After:**
- System API: `/as2/sysapi/v1/*`
- User API: `/as2/userapi/{username}/*`

### Target Type Constants

**File:** `IPWhitelistEntry.java`

```java
// Old
public static final String TARGET_API = "API";

// New
public static final String TARGET_SYS_API = "SYS_API";  // System REST API for server control
public static final String TARGET_USER_API = "USER_API"; // User API endpoint for external requests
```

### Preferences Keys

**File:** `PreferencesAS2.java`

```java
// Old
public static final String IP_WHITELIST_ENABLED_API = "ip.whitelist.enabled.api";

// New
public static final String IP_WHITELIST_ENABLED_SYS_API = "ip.whitelist.enabled.sysapi";
public static final String IP_WHITELIST_ENABLED_USER_API = "ip.whitelist.enabled.userapi";
```

**Default Values:**
- `ip.whitelist.enabled.sysapi` = `true` (enabled by default for security)
- `ip.whitelist.enabled.userapi` = `false` (disabled by default for flexibility)

### Service Methods

**File:** `IPWhitelistService.java`

```java
// Old
public boolean isAllowedForAPI(String ip, int userId)

// New
public boolean isAllowedForSysApi(String ip, int userId)
public boolean isAllowedForUserApi(String ip)
```

Both new methods include automatic localhost bypass for development convenience.

### Database Tables

**Table:** `ip_whitelist_global`

**Column:** `target_type VARCHAR(50)`

**Values:**
- `AS2` - AS2 protocol endpoint
- `TRACKER` - Message tracker endpoint
- `WEBUI` - Web UI access
- `SYS_API` - System REST API (`/sysapi/v1/*`)
- `USER_API` - User API endpoint (`/userapi/*`)
- `ALL` - All endpoints

### Database Migration

**Files:**
- `src/main/resources/sqlscript/mysql/config/update1to2.sql`
- `src/main/resources/sqlscript/postgres/config/update1to2.sql`

**Migration Logic:**
1. Update existing `API` entries to `SYS_API`
2. Duplicate `SYS_API` entries as `USER_API` entries (for backward compatibility)
3. Add new preference keys with default values

### Frontend Changes

**WebUI Components:**

1. **GlobalWhitelistTab.jsx** (line 220-224)
   - Dropdown options: `SYS_API` and `USER_API` instead of `API`

2. **IPWhitelistForm.jsx** (line 212-217)
   - Form options with descriptions:
     - `SYS_API - System REST API (/sysapi/v1/*)`
     - `USER_API - User API endpoint (/userapi/*)`

3. **SettingsTab.jsx**
   - Split single "REST API Access" toggle into two:
     - "System API Access (/sysapi/v1/*)"
     - "User API Access (/userapi/*)"

4. **api/client.js** (line 25, 48)
   - Updated base URL from `/as2/api/v1` to `/as2/sysapi/v1`

5. **ApiRequestList.jsx** (lines 55, 64, 79)
   - Updated URL construction to use `/as2/userapi/${username}`

6. **MyApiConfig.jsx** (line 263)
   - Updated base URL from `/as2/api/` to `/as2/userapi/`

7. **BlockLogTab.jsx**
   - Added pagination (default page size: 25)

### Swing GUI Changes

**File:** `JDialogIPWhitelistManagement.java`

- Split `checkAPI` checkbox into:
  - `checkSysAPI` - Enable for System API (/sysapi/v1/*)
  - `checkUserAPI` - Enable for User API (/userapi/*)

### Resource Bundles

**Files:**
- `ResourceBundleIPWhitelist.java`
- `ResourceBundleIPWhitelist_de.java`

**Added Keys:**
- `SETTINGS_ENABLE_SYS_API` (EN: "Enable for System API (/sysapi/v1/*)")
- `SETTINGS_ENABLE_USER_API` (EN: "Enable for User API (/userapi/*)")
- `SETTINGS_ENABLE_SYS_API` (DE: "Für System-API aktivieren (/sysapi/v1/*)")
- `SETTINGS_ENABLE_USER_API` (DE: "Für Benutzer-API aktivieren (/userapi/*)")

### Import Tool

**File:** `IPWhitelistImporter.java`

Updated to support new target types:
- Valid types: `AS2`, `TRACKER`, `WEBUI`, `SYS_API`, `USER_API`, `ALL`
- When `ALL` is specified, imports for all 5 individual types

**Usage:**
```bash
java -cp mend-as2.jar de.mendelson.comm.as2.tools.IPWhitelistImporter public_ip_cidr.json SYS_API
java -cp mend-as2.jar de.mendelson.comm.as2.tools.IPWhitelistImporter public_ip_cidr.json USER_API
```

### Servlet Configuration

**File:** `web.xml`

```xml
<!-- System API - for controlling server -->
<servlet-mapping>
    <servlet-name>jersey-sysapi</servlet-name>
    <url-pattern>/sysapi/v1/*</url-pattern>
</servlet-mapping>

<!-- User API - for receiving external requests -->
<servlet-mapping>
    <servlet-name>jersey-userapi</servlet-name>
    <url-pattern>/userapi/*</url-pattern>
</servlet-mapping>
```

## Backward Compatibility

The database migration script ensures smooth transition:
1. Existing `API` whitelist entries are converted to `SYS_API`
2. Duplicate entries are created for `USER_API`
3. This ensures existing whitelist rules continue to work for both endpoint types

## Testing

### System API
```bash
# Should work from localhost (auto-bypass)
curl -X POST http://localhost:8080/as2/sysapi/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Should be blocked from remote IP if not whitelisted
curl -X GET http://remote-server:8080/as2/sysapi/v1/partners
# Expected: 403 Forbidden (if SYS_API whitelist enabled and IP not in list)
```

### User API
```bash
# Should work from localhost (auto-bypass)
curl -X POST http://localhost:8080/as2/userapi/admin1/webhook \
  -H "Content-Type: application/json" \
  -d '{"event":"test"}'

# Should work from any IP by default (USER_API whitelist disabled by default)
curl -X POST http://remote-server:8080/as2/userapi/admin1/data
```

## Files Modified

### Backend
1. `IPWhitelistEntry.java` - Added TARGET_SYS_API and TARGET_USER_API constants
2. `PreferencesAS2.java` - Split IP_WHITELIST_ENABLED_API into two keys
3. `IPWhitelistService.java` - Split isAllowedForAPI into two methods
4. `JwtAuthenticationFilter.java` - Updated to check SYS_API whitelist
5. `ApiServlet.java` - Updated to check USER_API whitelist
6. `IPWhitelistResource.java` - Updated DTO for two API types
7. `JDialogIPWhitelistManagement.java` - Split API checkbox into two
8. `ResourceBundleIPWhitelist.java` - Added new translation keys
9. `ResourceBundleIPWhitelist_de.java` - Added German translations
10. `IPWhitelistImporter.java` - Updated valid target types

### Frontend
1. `api/client.js` - Updated base URL to /sysapi/v1
2. `GlobalWhitelistTab.jsx` - Updated dropdown options, added pagination
3. `IPWhitelistForm.jsx` - Updated form options
4. `SettingsTab.jsx` - Split API toggle into two
5. `ApiRequestList.jsx` - Updated URL to /userapi/
6. `MyApiConfig.jsx` - Updated base URL to /userapi/
7. `BlockLogTab.jsx` - Added pagination

### Database
1. `mysql/config/CREATE.sql` - Added comments for target types
2. `mysql/config/update1to2.sql` - Migration script
3. `postgres/config/CREATE.sql` - Added comments for target types
4. `postgres/config/update1to2.sql` - Migration script

### Documentation
1. `md-memo/IP_WHITELIST_LOCALHOST_HANDLING.md` - Updated to mention API split
2. `md-memo/API_SPLIT_SYS_USER.md` - This document

## Security Considerations

### System API (`/sysapi/v1/*`)
- **Purpose:** Administrative control of AS2 server
- **Default:** Whitelist enabled (security-first)
- **Authentication:** JWT tokens (requires login)
- **Localhost:** Auto-bypass enabled
- **Recommendation:** Keep whitelist enabled, add only trusted admin IPs

### User API (`/userapi/*`)
- **Purpose:** Receive external requests (webhooks, data ingestion)
- **Default:** Whitelist disabled (flexibility-first)
- **Authentication:** Basic Auth or Certificate per user
- **Localhost:** Auto-bypass enabled
- **Recommendation:** Enable whitelist only if you know the source IPs

## Best Practices

1. **For System API:**
   - Keep whitelist enabled
   - Add only administrator workstation IPs
   - Review and update IP list regularly
   - Monitor blocked attempts in Block Log tab

2. **For User API:**
   - Enable whitelist if you know partner IPs
   - Use authentication (Basic or Certificate) always
   - Monitor requests in REST API Requests tab
   - Consider rate limiting for public-facing endpoints

3. **General:**
   - Use HTTPS in production
   - Bind server to specific interfaces (not 0.0.0.0)
   - Use firewall rules as first line of defense
   - IP whitelist as second layer of security
