# System TLS UI Reorganization - COMPLETED

## Summary
Reorganized the "Sys TLS" functionality from a standalone menu item to a subtab under the "System" menu. Updated the control layout to match the style of "My Sign/Crypt/TLS" for consistency.

## Changes Made

### 1. SystemInfo.jsx - Added TLS Subtab
**File:** `src/main/webapp/admin/src/features/system/SystemInfo.jsx`

**Changes:**
- Added imports for `SystemTLS` component and `useAuth` hook
- Added permission check for TLS tab visibility (`hasTLSReadPermission`)
- Added "TLS" tab button that appears only if user has CERT_TLS_READ permission
- Added conditional rendering for SystemTLS component when TLS tab is active

**Before:**
```
System Menu Tabs: HTTP Server Configuration | Tracker Conf | System Events | Search in Server Log | Maintenance | Notification
```

**After:**
```
System Menu Tabs: HTTP Server Configuration | Tracker Conf | System Events | Search in Server Log | Maintenance | Notification | TLS
```

### 2. SystemTLS.jsx - Layout Redesign
**File:** `src/main/webapp/admin/src/features/system/SystemTLS.jsx`

**Changes - Layout Structure:**
- Removed standalone page header
- Moved info banner to top (explaining system-wide HTTPS certificates)
- Reorganized buttons to match CertificateList.jsx layout:
  - **Left side:** Import, Generate New Key (with permission checks)
  - **Right side:** Export dropdown menu, Tools dropdown menu
- Updated table styling to match CertificateList.jsx
- Improved button grouping and spacing

**Changes - Export Menu:**
- Converted to dropdown menu (Export ▼) matching CertificateList.jsx
- Two options: Export Keystore (PKCS#12) and Export Keystore (JKS)
- Dropdown appears on right side of screen
- Improved modal dialog for password input

**Changes - Tools Menu:**
- Converted to dropdown menu (Tools ▼) matching CertificateList.jsx
- Contains: Verify Certificates (CRL)
- Consistent styling with Export menu

**Changes - Table:**
- Applied consistent table styling with CertificateList.jsx
- Fixed column widths for better layout
- Improved button styling in Actions column
- Better responsive design with word wrapping

**Changes - Permission Handling:**
- Import and Generate buttons disabled for read-only users
- Delete button disabled for read-only users
- Export and Tools menus always available (read operations)
- Clear visual feedback with opacity and cursor changes

### 3. App.jsx - Routing Update
**File:** `src/main/webapp/admin/src/App.jsx`

**Changes:**
- Removed SystemTLS import
- Removed standalone `/system-tls` route
- TLS functionality now accessible via `/system?tab=tls` or System page TLS tab

**Before:**
```javascript
import SystemTLS from './features/system/SystemTLS';

<Route path="system-tls" element={...} />
```

**After:**
```javascript
// SystemTLS import removed
// Route removed - TLS is now a subtab of System
```

### 4. Layout.jsx - Menu Update
**File:** `src/main/webapp/admin/src/components/Layout.jsx`

**Changes:**
- Removed `showSystemTLS` permission check variable
- Removed "Sys TLS" standalone menu link
- TLS functionality now accessed through System menu

**Before:**
```
Menu: Dashboard | My Partners | My Sign/Crypt/TLS | AS2 Messages | Tracker Messages | System | Sys TLS | Users
```

**After:**
```
Menu: Dashboard | My Partners | My Sign/Crypt/TLS | AS2 Messages | Tracker Messages | System | Users
```

## UI Layout Comparison

### Old Layout (Standalone Page)
```
Top Nav: [...] | System | Sys TLS | [...]

Sys TLS Page:
┌─────────────────────────────────────┐
│ System TLS Certificates             │
├─────────────────────────────────────┤
│ [Info Banner]                       │
│ [Warning Banner if read-only]       │
│                                     │
│ [TLS Certificates] (single tab)    │
│                                     │
│ [Import] [Generate] [Export] [CRL] │
│                                     │
│ [Table with certificates]           │
└─────────────────────────────────────┘
```

### New Layout (Subtab)
```
Top Nav: [...] | System | [...]

System Page → TLS Tab:
┌─────────────────────────────────────┐
│ System                              │
├─────────────────────────────────────┤
│ [HTTP Conf] [Tracker] [...] [TLS]  │
├─────────────────────────────────────┤
│ [Info Banner]                       │
│ [Warning Banner if read-only]       │
│                                     │
│ [Import] [Generate]  [Export▼] [Tools▼] │
│                                     │
│ [Table with certificates]           │
└─────────────────────────────────────┘
```

## Style Matching with "My Sign/Crypt/TLS"

Both pages now share consistent:
- **Button Layout:** Action buttons (Import, Generate) on left, menus (Export, Tools) on right
- **Dropdown Menus:** Same styling for Export and Tools dropdown menus
- **Table Styling:** Matching table header, row, and cell styles
- **Button Styling:** Consistent colors (green for actions, cyan for export, gray for tools, red for delete)
- **Permission Handling:** Same visual feedback for disabled buttons
- **Modal Dialogs:** Consistent password input dialog for keystore export

## Benefits

1. **Better Navigation:** TLS functionality logically grouped with other system settings
2. **Consistent UI:** Matches the layout pattern used in "My Sign/Crypt/TLS"
3. **Less Menu Clutter:** One less top-level menu item
4. **Improved UX:** Users expect TLS certificates under System configuration
5. **Responsive Design:** Better button grouping for different screen sizes

## Access Control

TLS tab visibility is controlled by permissions:
- **CERT_TLS_READ:** Required to see TLS tab in System menu
- **CERT_TLS_WRITE:** Required to Import, Generate, or Delete certificates
- **No permissions:** TLS tab not shown, user cannot access page

## URL Access

Users can access TLS page via:
1. Navigate to System menu → Click TLS tab
2. Direct URL: `/system?tab=tls` (will auto-select TLS tab)

## Compilation Status
✅ **BUILD SUCCESS** - Frontend built successfully in 644ms

## Files Modified
1. `src/main/webapp/admin/src/features/system/SystemInfo.jsx`
2. `src/main/webapp/admin/src/features/system/SystemTLS.jsx`
3. `src/main/webapp/admin/src/App.jsx`
4. `src/main/webapp/admin/src/components/Layout.jsx`

## Testing Checklist

### Navigation Tests
- [  ] Navigate to System menu
- [  ] Verify TLS tab appears for users with CERT_TLS_READ permission
- [  ] Verify TLS tab does NOT appear for users without CERT_TLS_READ
- [  ] Click TLS tab and verify SystemTLS component loads
- [  ] Test direct URL access: `/system?tab=tls`

### UI Layout Tests
- [  ] Verify info banner appears at top
- [  ] Verify Import and Generate buttons on left side
- [  ] Verify Export and Tools dropdown menus on right side
- [  ] Verify table displays certificates correctly
- [  ] Verify button styling matches "My Sign/Crypt/TLS"

### Permission Tests
- [  ] Read-only user: Import, Generate, Delete buttons disabled
- [  ] Read-only user: Export and Tools menus still functional
- [  ] Write permission user: All buttons enabled
- [  ] Verify visual feedback (opacity, cursor) for disabled buttons

### Functional Tests
- [  ] Import certificate via Import button
- [  ] Generate new key via Generate button
- [  ] Export keystore via Export menu (PKCS#12 and JKS)
- [  ] Verify certificates via Tools menu
- [  ] Generate CSR for key pair certificate
- [  ] Delete certificate (with and without force)
- [  ] Verify permission checks work correctly

### Responsive Design
- [  ] Test on different screen widths
- [  ] Verify dropdown menus position correctly
- [  ] Verify table columns wrap text properly
- [  ] Verify button groups don't overflow
