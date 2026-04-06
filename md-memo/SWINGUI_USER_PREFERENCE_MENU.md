# SwingUI User Preference Menu - HTTP Authentication

## Implementation Date
2026-04-05

## Overview
Created a separate "User Preference" menu in SwingUI with "HTTP Authentication" as a submenu item, providing dedicated access to HTTP authentication preferences.

## Changes Made

### 1. Menu Structure

Created new menu hierarchy:
```
Menu Bar
├── File
├── User Preference (NEW)
│   └── HTTP Authentication (NEW)
└── Help
```

### 2. Files Modified

#### **AS2Gui.java**

**Variable Declarations (around line 2519):**
```java
private javax.swing.JMenu jMenuUserPreference;
private javax.swing.JMenuItem jMenuItemUserPrefHttpAuth;
```

**Menu Initialization (around line 1600):**
```java
jMenuUserPreference = new javax.swing.JMenu();
jMenuItemUserPrefHttpAuth = new javax.swing.JMenuItem();
```

**Menu Setup (after jMenuBar.add(jMenuFile), around line 2270):**
```java
// User Preference menu
jMenuUserPreference.setText(this.rb.getResourceString("menu.userpreference"));

jMenuItemUserPrefHttpAuth.setText(this.rb.getResourceString("menu.userpreference.httpauth"));
jMenuItemUserPrefHttpAuth.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItemUserPrefHttpAuthActionPerformed(evt);
    }
});
jMenuUserPreference.add(jMenuItemUserPrefHttpAuth);

jMenuBar.add(jMenuUserPreference);
```

**Action Method (around line 2370):**
```java
private void jMenuItemUserPrefHttpAuthActionPerformed(java.awt.event.ActionEvent evt) {
    this.displayHttpAuthPreferences();
}
```

**Display Method (after displayPreferences(), around line 1432):**
```java
/**
 * Display HTTP Authentication preferences dialog
 */
public void displayHttpAuthPreferences() {
    final String uniqueId = this.getClass().getName() + ".displayHttpAuthPreferences." + System.currentTimeMillis();
    Runnable prefRunner = new Runnable() {
        @Override
        public void run() {
            JDialogPreferences dialog = null;
            // display wait indicator
            AS2Gui.this.as2StatusBar.startProgressIndeterminate(
                    AS2Gui.this.rb.getResourceString("menu.userpreference.httpauth"), uniqueId);
            try {
                AS2Gui.this.jMenuItemUserPrefHttpAuth.setEnabled(false);
                List<PreferencesPanel> panelList = new ArrayList<PreferencesPanel>();
                // Add only HTTP Auth panel
                panelList.add(new PreferencesPanelHttpAuth(AS2Gui.this.getBaseClient()));
                dialog = new JDialogPreferences(AS2Gui.this, panelList, "httpauth", "");
            } catch (Exception e) {
                UINotification.instance().addNotification(e);
                e.printStackTrace();
            } finally {
                AS2Gui.this.as2StatusBar.stopProgressIfExists(uniqueId);
                if (dialog != null) {
                    dialog.setVisible(true);
                }
                AS2Gui.this.jMenuItemUserPrefHttpAuth.setEnabled(true);
            }
        }
    };
    GUIClient.submit(prefRunner);
}
```

**Removed from General Preferences (around line 1416):**
- Removed `PreferencesPanelHttpAuth` from the general File → Preferences dialog
- It now has its own dedicated menu entry

#### **ResourceBundleAS2Gui.java**

Added resource strings:
```java
{"menu.userpreference", "User Preference"},
{"menu.userpreference.httpauth", "HTTP Authentication"},
```

## How to Use

### Accessing HTTP Authentication Preferences:

1. **Start AS2 Server** with SwingUI
2. Click **"User Preference"** menu in the menu bar (new menu between File and Help)
3. Click **"HTTP Authentication"** submenu
4. A dedicated dialog opens showing only the HTTP Authentication preferences
5. Configure credentials for each remote partner:
   - Check "Message Auth" and enter username/password for message sending
   - Check "MDN Auth" and enter username/password for MDN requests
6. Click **OK** to save

### Difference from Previous Implementation:

**Before:**
- HTTP Auth preferences were a tab in File → Preferences dialog
- Mixed with system-wide server preferences
- Less intuitive for user-specific settings

**After:**
- HTTP Auth preferences have dedicated menu: User Preference → HTTP Authentication
- Separate dialog showing only HTTP Auth settings
- Clear separation between server preferences and user preferences
- Easier to find and access

## Benefits

1. **Clear Separation**: User preferences are now separate from system preferences
2. **Better Organization**: User-specific settings have their own menu
3. **Easier Access**: Direct menu item instead of navigating through tabs
4. **Extensibility**: Can easily add more user preference items to this menu in future
5. **User-Friendly**: More intuitive for users looking for authentication settings

## Technical Details

### Dialog Behavior:
- Uses the same `JDialogPreferences` class as File → Preferences
- Only includes `PreferencesPanelHttpAuth` in the panel list
- Automatically selects the "httpauth" tab (which is the only tab)
- Runs in background thread with progress indicator
- Disables menu item while dialog is open

### Menu Positioning:
- Inserted between "File" and "Help" menus
- Follows standard Swing JMenu/JMenuItem patterns
- Uses resource bundle for localization support

## Future Enhancements

The "User Preference" menu can be extended with additional user-specific settings:
- User notification preferences
- User interface customization
- User-specific filters and views
- Personal shortcuts and favorites

## Files Modified Summary

1. `src/main/java/de/mendelson/comm/as2/client/AS2Gui.java`
   - Added menu variables
   - Added menu initialization
   - Added menu setup and action listener
   - Added displayHttpAuthPreferences() method
   - Removed HTTP Auth panel from general preferences
   - Added action method

2. `src/main/java/de/mendelson/comm/as2/client/ResourceBundleAS2Gui.java`
   - Added "menu.userpreference" resource string
   - Added "menu.userpreference.httpauth" resource string

## Testing Checklist

- [ ] Menu bar shows "User Preference" menu between File and Help
- [ ] Clicking "User Preference" shows submenu with "HTTP Authentication"
- [ ] Clicking "HTTP Authentication" opens dedicated preferences dialog
- [ ] Dialog shows only HTTP Auth preferences table
- [ ] Can configure credentials for remote partners
- [ ] Click OK saves preferences successfully
- [ ] Click Cancel discards changes
- [ ] Preferences persist after reopening dialog
- [ ] Menu item disables while dialog is open
- [ ] Progress indicator shows during dialog loading
