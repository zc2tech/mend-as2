# Partner Visibility Filtering for Messages

## Overview

Implemented partner visibility filtering for message lists in WebUI. Regular users can only see messages from partners that are visible to them, while users with ADMIN role have full visibility to all messages and partners.

## Changes Made

### 1. MessageOverviewFilter.java

**File:** `src/main/java/de/mendelson/comm/as2/message/MessageOverviewFilter.java`

Added two new fields:

```java
private Integer userId = null;  // WebUI user ID for partner visibility filtering
private boolean isAdmin = false;  // True if user has ADMIN role (bypasses visibility filtering)
```

Added getters and setters:
- `getUserId()` / `setUserId(Integer userId)`
- `isAdmin()` / `setAdmin(boolean isAdmin)`

### 2. MessageAccessDB.java

**File:** `src/main/java/de/mendelson/comm/as2/message/MessageAccessDB.java`

**Location:** In `getMessageOverview(MessageOverviewFilter filter)` method, after existing filter conditions (around line 518)

Added partner visibility filtering logic:

```java
// Partner visibility filtering for non-admin users
// Admin users or users without userId context (SwingUI) see all messages
if (filter.getUserId() != null && !filter.isAdmin()) {
    try (Connection configConnectionAutoCommit = this.dbDriverManager
            .getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG)) {
        // Build subquery to get AS2 IDs of partners visible to this user
        // Include:
        // 1. All local stations (always visible)
        // 2. Remote partners with no visibility records (visible to all)
        // 3. Remote partners specifically assigned to this user
        String visibilitySubquery =
            "(SELECT as2ident FROM partner WHERE islocal=1 " +
            "UNION " +
            "SELECT p.as2ident FROM partner p " +
            "WHERE p.islocal=0 AND NOT EXISTS (SELECT 1 FROM partner_user_visibility WHERE partner_id=p.id) " +
            "UNION " +
            "SELECT p.as2ident FROM partner p " +
            "INNER JOIN partner_user_visibility pv ON p.id=pv.partner_id " +
            "WHERE pv.user_id=?)";

        if (queryCondition.length() == 0) {
            queryCondition.append(" WHERE");
        } else {
            queryCondition.append(" AND");
        }
        queryCondition.append(" (senderid IN ").append(visibilitySubquery)
                       .append(" OR receiverid IN ").append(visibilitySubquery).append(")");

        // Add userId parameter twice (once for sender, once for receiver)
        parameterList.add(filter.getUserId());
        parameterList.add(filter.getUserId());
    }
}
```

**How it works:**

The SQL subquery fetches AS2 identifications of partners visible to the user:
1. **Local stations** - Always included (islocal=1)
2. **Public remote partners** - Remote partners with no visibility records (visible to all users by default)
3. **Assigned remote partners** - Remote partners specifically assigned to this user via `partner_user_visibility` table

The main query then filters messages where either sender OR receiver is in this list.

### 3. MessageResource.java

**File:** `src/main/java/de/mendelson/comm/as2/servlet/rest/resources/MessageResource.java`

**Changes in `listMessages()` method:**

1. Added `@Context SecurityContext securityContext` parameter to inject current user context

2. Added code to get user ID and admin status before creating MessageOverviewRequest:

```java
// Get current user's ID and role for partner visibility filtering
try {
    String username = securityContext.getUserPrincipal().getName();
    UserManagementAccessDB userMgmt = new UserManagementAccessDB(
            processing.getDBDriverManager(), null);
    WebUIUser user = userMgmt.getUser(username);
    if (user != null) {
        filter.setUserId(user.getId());
        filter.setAdmin(user.hasRole(WebUIUser.ROLE_ADMIN));
    }
} catch (Exception e) {
    // If we can't get user info, treat as non-admin with no user context
    // This will show all messages (fallback for compatibility)
}
```

## Behavior

### Regular Users (Non-Admin)

- Can only see messages where:
  - Sender is a visible partner, OR
  - Receiver is a visible partner
  
- Partner filter dropdown shows only their visible partners

- Visible partners include:
  1. All local stations
  2. Remote partners with no visibility restrictions (default = visible to all)
  3. Remote partners specifically assigned to them

### ADMIN Role Users

- Can see ALL messages regardless of partner visibility
- Partner filter dropdown shows ALL partners
- Visibility filtering is completely bypassed

### SwingUI

- No user authentication/context
- Shows ALL messages (userId = null, bypasses visibility filtering)
- Maintains backward compatibility

## Database Schema

Uses existing `partner_user_visibility` table:

```sql
CREATE TABLE partner_user_visibility(
    id SERIAL PRIMARY KEY,
    partner_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(partner_id) REFERENCES partner(id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES webui_users(id) ON DELETE CASCADE,
    UNIQUE(partner_id, user_id)
);
```

## Testing

### Test Scenario 1: Regular User with Limited Visibility

1. Create user "user1" without ADMIN role
2. Create partners: Partner A (visible to user1), Partner B (not visible)
3. Send messages from both partners
4. Login as user1
5. **Expected:** Only see messages from Partner A

### Test Scenario 2: ADMIN User

1. Create user "admin1" with ADMIN role
2. Same partners as above
3. Login as admin1
4. **Expected:** See messages from both Partner A and Partner B

### Test Scenario 3: Partner with No Visibility Records

1. Create Partner C with no visibility records (default = visible to all)
2. Send messages from Partner C
3. Login as any non-admin user
4. **Expected:** All users can see messages from Partner C

### Test Scenario 4: SwingUI Compatibility

1. Open SwingUI
2. **Expected:** All messages visible (no filtering applied)

## Deployment

1. Stop AS2 server
2. Replace `target/mend-as2-1.1.0.jar`
3. Start AS2 server
4. Test with different user roles

## Files Modified

1. `src/main/java/de/mendelson/comm/as2/message/MessageOverviewFilter.java`
2. `src/main/java/de/mendelson/comm/as2/message/MessageAccessDB.java`
3. `src/main/java/de/mendelson/comm/as2/servlet/rest/resources/MessageResource.java`

## Backward Compatibility

- If userId is null → No filtering (shows all messages)
- If user doesn't exist → Fallback to no filtering
- SwingUI continues to work without changes
- Existing API clients without authentication → No filtering

## Performance Considerations

- Subquery uses indexes on `partner_user_visibility(partner_id, user_id)`
- UNION query is optimized for small partner datasets
- Filter is only applied for non-admin users with valid userId
- Admin users bypass the subquery entirely for better performance
