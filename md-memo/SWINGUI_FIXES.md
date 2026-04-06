# SwingUI Fixes Required

## Issue 1: Add "Use User Preference" Radio Button Option to Partner Dialog

### Status: ⚠️ Requires Manual NetBeans Form Designer Work

The Java code already has the radio button variables declared:
- `jRadioButtonHttpAuthUserPreferenceMessage`
- `jRadioButtonHttpAuthUserPreferenceMDN`

And the event handlers are implemented in `JPanelPartner.java`.

**What needs to be done manually in NetBeans:**

1. Open `JPanelPartner.java` in NetBeans Form Designer
2. Locate the HTTP Authentication section for Message
3. Add a new JRadioButton:
   - Variable name: `jRadioButtonHttpAuthUserPreferenceMessage`
   - Text: "Use User Preference"
   - Add to the ButtonGroup with the other two radio buttons (None, Basic Auth)
   - Position it between "Basic Auth" and OAuth2 options
4. Connect the ItemStateChanged event to: `jRadioButtonHttpAuthUserPreferenceMessageItemStateChanged`
5. Repeat steps 2-4 for MDN section:
   - Variable name: `jRadioButtonHttpAuthUserPreferenceMDN`
   - Event handler: `jRadioButtonHttpAuthUserPreferenceMDNItemStateChanged`

**Alternative if NetBeans is not available:**
Since the .form file doesn't exist, you need to add the radio buttons programmatically in the `initComponents()` method of JPanelPartner.java. However, this requires understanding the existing layout structure.

---

## Issue 2: AS2 Messages Not Showing in SwingUI Message List

### Investigation Results

- ✅ Database has 8 messages in the `messages` table
- ✅ Messages have valid data (messageid, direction, state, initdateutc)
- ✅ Sample messages exist from recent dates (2026-04-03, 2026-04-05)

### Possible Causes:

1. **Filter Issue**: The message overview might have a default filter that's hiding messages
2. **Client-Server Communication**: SwingUI might not be properly requesting messages
3. **Date/Time Issue**: Check if there's a date range filter set

### Debugging Steps:

1. **Check Server Logs**:
   ```bash
   tail -f logs/server.log | grep -i message
   ```

2. **Check if Server is Running**:
   ```bash
   ps aux | grep java | grep as2
   ```

3. **Test Direct Database Query**:
   ```bash
   PGPASSWORD=as2password psql -h localhost -p 5432 -U as2user -d as2_db_runtime -c "SELECT COUNT(*) FROM messages WHERE state=1;"
   ```

4. **In SwingUI**:
   - Check if there are any filter settings in the message overview window
   - Look for date range filters
   - Check "Show finished messages" checkbox
   - Try clicking "Refresh" button

### Potential Fix Locations:

- `TableModelMessageOverview.java` - Check how messages are loaded
- `MessageOverviewRequest.java` - Check default filter parameters
- `MessageAccessDB.java` - Check the SELECT query for messages

---

## Issue 3: HTTP Authentication Preferences Menu Missing in SwingUI

### Current State

The preferences dialog exists at:
- `src/main/java/de/mendelson/comm/as2/preferences/JDialogPreferences.java`

However, HTTP Authentication preferences tab has NOT been added to this dialog yet.

### Solution Required:

**Option A: Add to Existing Preferences Dialog (Recommended)**

1. Create a new panel: `JPanelHttpAuthPreferences.java`
   - Display a table of all visible partners
   - For each partner, show checkboxes and input fields for:
     - Message Auth: Enable + Username + Password
     - MDN Auth: Enable + Username + Password
   - Similar to WebUI implementation

2. Add the tab to `JDialogPreferences.java`:
   ```java
   private JPanelHttpAuthPreferences jPanelHttpAuth;
   
   // In initComponents():
   jPanelHttpAuth = new JPanelHttpAuthPreferences(clientserver, dbDriverManager);
   // Add to tabbed pane or list
   ```

**Option B: Create Standalone Dialog (Alternative)**

Create a new menu item in AS2Gui.java:
- Menu: File → Preferences → HTTP Authentication
- Opens: `JDialogHttpAuthPreferences.java`

### Implementation Details:

Since SwingUI doesn't have user login, we consider the user as "admin":
- Hardcode userId = 1 (or get the admin user from database)
- Load preferences for this single user
- Save preferences to `user_preference_http_auth` table

### Database Access:

The backend classes already exist:
- ✅ `UserHttpAuthPreferenceAccessDB.java`
- ✅ `UserHttpAuthPreference.java`
- ✅ Database table: `user_preference_http_auth`

---

## Quick Workaround for Testing

**For Issue 1 (User Preference Radio Button):**
The functionality is already implemented in the backend. You can test it by:
1. Directly updating the database:
   ```sql
   UPDATE partner SET authmodehttp = 2 WHERE id = 2;
   ```
2. Adding user preferences:
   ```sql
   INSERT INTO user_preference_http_auth (user_id, partner_id, use_message_auth, message_username, message_password)
   VALUES (1, 2, true, 'testuser', 'testpass');
   ```
3. Send a message from WebUI to test

**For Issue 2 (Messages Not Showing):**
Try running the server with increased logging:
```bash
java -Djava.util.logging.config.file=logging.properties -jar target/mend-as2-1.1.0.jar
```

**For Issue 3 (HTTP Auth Preferences UI):**
Use WebUI Preferences page as a temporary workaround - it fully works and saves to the same database table.

---

## Priority Recommendation

1. **HIGH**: Issue 2 (Messages not showing) - This is blocking normal operation
2. **MEDIUM**: Issue 3 (HTTP Auth Preferences menu) - Workaround: Use WebUI
3. **LOW**: Issue 1 (Radio button UI) - Backend works, just needs UI update

---

## Files to Check/Modify

### Issue 1:
- `src/main/java/de/mendelson/comm/as2/partner/gui/JPanelPartner.java` (NetBeans Form Designer)

### Issue 2:
- `src/main/java/de/mendelson/comm/as2/message/loggui/TableModelMessageOverview.java`
- `src/main/java/de/mendelson/comm/as2/message/MessageAccessDB.java`
- `src/main/java/de/mendelson/comm/as2/message/clientserver/MessageOverviewRequest.java`

### Issue 3:
- Create: `src/main/java/de/mendelson/comm/as2/preferences/JPanelHttpAuthPreferences.java`
- Modify: `src/main/java/de/mendelson/comm/as2/preferences/JDialogPreferences.java`
- Modify: `src/main/java/de/mendelson/comm/as2/client/AS2Gui.java` (add menu item)
