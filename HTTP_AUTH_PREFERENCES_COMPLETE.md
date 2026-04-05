# HTTP Authentication Preferences - Complete Implementation

## Overview

Implemented complete HTTP Authentication preferences management for both SwingUI and WebUI, allowing users to configure HTTP Basic Auth credentials for partner connections.

## Implementation Date

2026-04-05

## Components Implemented

### 1. Server-Side Messages (Client-Server Communication)

Created message classes for SwingUI communication with the server:

#### **UserHttpAuthPreferenceRequest.java**
- Request to get all HTTP auth preferences for a user
- Parameters: userId

#### **UserHttpAuthPreferenceResponse.java**
- Response containing all preferences for a user
- Structure: `Map<Integer, Map<String, Map<String, String>>>` (partnerId → type → field → value)
- Methods:
  - `addPreference(partnerId, type, field, value)`
  - `getPreference(partnerId, type, field)`

#### **UserHttpAuthPreferenceSaveRequest.java**
- Request to save HTTP auth preferences
- Contains full preferences structure for the user
- Methods: `addPreference(partnerId, type, field, value)`

#### **UserHttpAuthPreferenceSaveResponse.java**
- Response after saving preferences
- Fields: `success`, `errorMessage`

### 2. Server-Side Message Handlers

#### **AS2ServerProcessing.java**

Added two new message processing methods:

**processUserHttpAuthPreferenceRequest()**
- Loads all partners via PartnerAccessDB
- Loads existing preferences from UserHttpAuthPreferenceAccessDB
- Converts UserHttpAuthPreference objects to response structure
- Returns preferences structured by partner → type (message/mdn) → field (username/password)
- Only includes enabled auth types in response

**processUserHttpAuthPreferenceSaveRequest()**
- Gets all remote partners via PartnerAccessDB
- Deletes ALL existing preferences for the user (clean slate)
- Converts request structure to UserHttpAuthPreference objects
- Saves UserHttpAuthPreference objects via UserHttpAuthPreferenceAccessDB
- Only saves preferences where at least one auth type is enabled

### 3. SwingUI Preferences Panel

#### **PreferencesPanelHttpAuth.java**

Complete preferences panel for SwingUI with:

**Features:**
- Table display of all remote partners
- Columns:
  - Partner name
  - Message Auth (checkbox + username/password fields)
  - MDN Auth (checkbox + username/password fields)
- Real-time editing in table cells
- Uses BaseClient for client-server communication
- Admin user context (userId = 1)

**Methods:**
- `loadData()` - Loads partners and preferences via messages
- `savePreferences()` - Saves preferences via UserHttpAuthPreferenceSaveRequest
- `loadPreferences()` - Reloads data (implements PreferencesPanel abstract method)
- `preferencesAreModified()` - Returns false (not tracking changes yet)
- `getIcon()` - Returns preferences icon
- `getTabResource()` - Returns "tab.httpauth" resource key

**Integration:**
- Added to AS2Gui preferences panel list (line 1415)
- Added resource strings in ResourceBundlePreferences.java:
  - `tab.httpauth` - "HTTP Authentication"
  - `httpauth.info` - Info text
  - `httpauth.col.*` - Column headers

### 4. WebUI Implementation

#### **UserHttpAuthPreferenceResource.java** (Already existed)

REST API endpoints:

**GET /user-preferences/http-auth**
- Returns all HTTP auth preferences for current user
- Filters to only visible partners (respects partner visibility rules)
- Returns empty preferences for partners without saved credentials
- Returns: `List<UserHttpAuthPreference>`

**POST /user-preferences/http-auth**
- Saves a single preference
- Validates user authentication
- Request body: UserHttpAuthPreference object

**DELETE /user-preferences/http-auth/{partnerId}**
- Deletes all preferences for a partner
- Validates user authentication

#### **HttpAuthPreferences.jsx** (Already existed)

Complete React component with:

**Features:**
- Responsive table layout with multi-row headers
- Two sections: Message Authentication, MDN Authentication
- Each section has: Enable checkbox, Username, Password
- Real-time checkbox toggles (auto-save)
- Manual save buttons per partner
- Delete button to remove all credentials for a partner
- Loading and saving states
- Toast notifications for success/error

**UI Elements:**
- Info box explaining the feature
- Partner name and AS2 ID display
- Disabled input fields when auth is not enabled
- Visual feedback (grayed out disabled fields)
- Responsive design with proper column widths

#### **UserPreferences.jsx** (Already existed)

Container component with:
- Tab navigation (currently only HTTP Authentication tab)
- Consistent styling
- Easy to extend with additional preference types

**Integration:**
- Routed at `/preferences` in App.jsx
- Accessible from user dropdown menu in Layout.jsx

## Data Flow

### SwingUI Flow:
```
User opens Preferences Dialog
  ↓
PreferencesPanelHttpAuth.loadData()
  ↓ sends UserHttpAuthPreferenceRequest
AS2ServerProcessing.processUserHttpAuthPreferenceRequest()
  ↓ queries UserHttpAuthPreferenceAccessDB + PartnerAccessDB
  ↓ returns UserHttpAuthPreferenceResponse
PreferencesPanelHttpAuth displays table
  ↓
User edits credentials and clicks Save
  ↓
PreferencesPanelHttpAuth.savePreferences()
  ↓ sends UserHttpAuthPreferenceSaveRequest
AS2ServerProcessing.processUserHttpAuthPreferenceSaveRequest()
  ↓ deletes old preferences
  ↓ saves new preferences via UserHttpAuthPreferenceAccessDB
  ↓ returns UserHttpAuthPreferenceSaveResponse
Success message displayed
```

### WebUI Flow:
```
User navigates to /preferences
  ↓
HttpAuthPreferences.fetchPreferences()
  ↓ GET /user-preferences/http-auth
UserHttpAuthPreferenceResource.getMyPreferences()
  ↓ queries UserHttpAuthPreferenceAccessDB
  ↓ filters by user's visible partners
  ↓ returns List<UserHttpAuthPreference>
HttpAuthPreferences displays table
  ↓
User edits credentials and clicks Save
  ↓
HttpAuthPreferences.savePreference()
  ↓ POST /user-preferences/http-auth
UserHttpAuthPreferenceResource.savePreference()
  ↓ calls UserHttpAuthPreferenceAccessDB.savePreference()
Success message displayed
```

## Database Schema

Uses existing `user_http_auth_preference` table:
- `id` - Primary key
- `user_id` - Foreign key to webui_users
- `partner_id` - Foreign key to partner
- `type` - 'message' or 'mdn'
- `field` - 'username' or 'password'
- `value` - The credential value
- `updated_at` - Timestamp (auto-updated via trigger)

## Files Modified

### Created Files:
1. `src/main/java/de/mendelson/comm/as2/usermanagement/clientserver/UserHttpAuthPreferenceRequest.java`
2. `src/main/java/de/mendelson/comm/as2/usermanagement/clientserver/UserHttpAuthPreferenceResponse.java`
3. `src/main/java/de/mendelson/comm/as2/usermanagement/clientserver/UserHttpAuthPreferenceSaveRequest.java`
4. `src/main/java/de/mendelson/comm/as2/usermanagement/clientserver/UserHttpAuthPreferenceSaveResponse.java`
5. `src/main/java/de/mendelson/comm/as2/preferences/PreferencesPanelHttpAuth.java`

### Modified Files:
1. `src/main/java/de/mendelson/comm/as2/server/AS2ServerProcessing.java`
   - Added import for UserHttpAuthPreferenceAccessDB
   - Added message handlers in processMessage() method (lines 627-632)
   - Added processUserHttpAuthPreferenceRequest() method
   - Added processUserHttpAuthPreferenceSaveRequest() method

2. `src/main/java/de/mendelson/comm/as2/client/AS2Gui.java`
   - Added PreferencesPanelHttpAuth to preferences panel list (line 1415)

3. `src/main/java/de/mendelson/comm/as2/preferences/ResourceBundlePreferences.java`
   - Added resource strings for HTTP Auth tab and columns

### Already Existing (No changes needed):
1. `src/main/java/de/mendelson/comm/as2/servlet/rest/resources/UserHttpAuthPreferenceResource.java`
2. `src/main/webapp/admin/src/features/preferences/HttpAuthPreferences.jsx`
3. `src/main/webapp/admin/src/features/preferences/UserPreferences.jsx`

## Testing Checklist

### SwingUI Testing:
- [ ] Open Preferences dialog (File → Preferences)
- [ ] Navigate to "HTTP Authentication" tab
- [ ] Verify all remote partners are listed
- [ ] Enable Message Auth for a partner and enter username/password
- [ ] Enable MDN Auth for a partner and enter username/password
- [ ] Click OK to save preferences
- [ ] Reopen Preferences and verify credentials are saved
- [ ] Disable auth and verify credentials are deleted

### WebUI Testing:
- [ ] Login as a WebUI user
- [ ] Click user dropdown → "Preferences"
- [ ] Verify HTTP Authentication tab is displayed
- [ ] Verify only visible partners are shown
- [ ] Toggle "Enable" checkbox for Message Auth
- [ ] Enter username and password
- [ ] Click "Save" button
- [ ] Verify success toast appears
- [ ] Refresh page and verify credentials persist
- [ ] Click "Delete" button to remove credentials
- [ ] Verify confirmation dialog and deletion

### Integration Testing:
- [ ] Configure partner with HTTP Auth mode = "User Preference"
- [ ] Set user's HTTP auth credentials in SwingUI
- [ ] Send a message to that partner
- [ ] Verify HTTP Basic Auth header is sent with user's credentials
- [ ] Repeat with WebUI credentials
- [ ] Verify MDN requests also use correct credentials

## Usage

### SwingUI:
1. Open **File → Preferences**
2. Select **HTTP Authentication** tab
3. For each partner:
   - Check "Message Auth" to enable message authentication
   - Enter username and password
   - Check "MDN Auth" to enable MDN authentication
   - Enter username and password
4. Click **OK** to save

### WebUI:
1. Login as a WebUI user
2. Click your username dropdown → **Preferences**
3. View **HTTP Authentication** tab
4. For each partner:
   - Check "Enable" under Message Authentication
   - Enter username and password
   - Click "Save"
   - Repeat for MDN Authentication if needed
5. Click "Delete" to remove all credentials for a partner

## Notes

- **SwingUI operates as admin user** (userId = 1) - no user login required
- **WebUI filters partners** - users only see partners they have visibility to
- **Local stations excluded** - HTTP auth preferences only apply to remote partners
- **Empty credentials deleted** - unchecking "Enable" or clearing username removes the preference
- **Auto-save on toggle** (WebUI) - clicking Enable checkbox immediately saves
- **Manual save required** (SwingUI) - must click OK to save changes
- **Password storage** - Passwords stored in plaintext (consider encryption in future)
- **Validation** - No validation on credential format (credentials sent as-is)

## Future Enhancements

1. **Password encryption** - Encrypt passwords in database
2. **Credential validation** - Test credentials before saving
3. **Bulk operations** - Copy credentials from one partner to another
4. **Import/Export** - Export/import credentials for backup
5. **Audit logging** - Log when credentials are created/modified/deleted
6. **Password strength** - Add password strength indicator
7. **Credential expiration** - Option to set expiration date for credentials
8. **Two-factor auth** - Support for 2FA/TOTP in addition to Basic Auth
