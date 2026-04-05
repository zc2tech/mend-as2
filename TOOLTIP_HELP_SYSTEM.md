# Keyboard Shortcuts Tooltip Help System

## Overview

The AS2 application now includes a **discoverable keyboard shortcuts system** with hover tooltips. Users don't need to read a manual - they can simply hover over buttons to see available keyboard shortcuts!

## ✨ Features

### 1. **Automatic Button Tooltips**
When users hover over buttons, they see:
- **OK/Save buttons**: Shows "ENTER" 
- **Cancel buttons**: Shows "ESC"
- **Custom shortcuts**: Shows "Cmd+S", "Ctrl+P", etc.

### 2. **Platform-Aware Display**
- **Mac**: Shows "Cmd+S", "Cmd+W", etc.
- **Windows/Linux**: Shows "Ctrl+S", "Ctrl+W", etc.

### 3. **Non-Intrusive**
- Tooltips only appear on hover
- Don't clutter the UI
- Helpful for new users, invisible to experts

## 🎯 What Users Will See

### Example: Partner Configuration Dialog

```
┌─────────────────────────────────────────┐
│  Configure Partner                      │
├─────────────────────────────────────────┤
│                                         │
│  [Partner Settings...]                  │
│                                         │
├─────────────────────────────────────────┤
│           [Cancel]  [OK]                │
│              ↑       ↑                  │
│         Hover shows: │                  │
│         "Keyboard: ESC"                 │
│                  Hover shows:           │
│                  "Keyboard: ENTER"      │
└─────────────────────────────────────────┘
```

### Example: Certificate Management Dialog

```
┌─────────────────────────────────────────┐
│  Sign/Crypt Certificates                │
├─────────────────────────────────────────┤
│  [Import] [Export] [Delete]             │
│     ↑                                   │
│     Hover shows:                        │
│     "Import certificate [Cmd+I]"        │
│                                         │
│  Certificate List...                    │
│                                         │
├─────────────────────────────────────────┤
│                             [OK]        │
│                              ↑          │
│                         Hover shows:    │
│                         "Keyboard: ENTER"│
└─────────────────────────────────────────┘
```

## 📝 Implementation Details

### Enhanced KeyboardShortcutUtil Methods

#### 1. `setupDialogKeyBindingsWithTooltips()`
Automatically adds tooltips to dialog buttons.

```java
// Before (no tooltips)
KeyboardShortcutUtil.setupDialogKeyBindings(this, okButton, cancelButton);

// After (with tooltips)
KeyboardShortcutUtil.setupDialogKeyBindingsWithTooltips(this, okButton, cancelButton);
```

**Result**:
- OK button gets tooltip: "Keyboard: ENTER"
- Cancel button gets tooltip: "Keyboard: ESC"
- Dialog supports Cmd/Ctrl+W to close

#### 2. `addButtonShortcutWithTooltip()`
Adds a keyboard shortcut to a button and shows it in the tooltip.

```java
// Add Cmd+S shortcut with tooltip
KeyboardShortcutUtil.addButtonShortcutWithTooltip(
    saveButton, 
    KeyEvent.VK_S, 
    "SAVE_ACTION"
);
```

**Result on Mac**: Tooltip shows "Keyboard: Cmd+S"  
**Result on Windows**: Tooltip shows "Keyboard: Ctrl+S"

#### 3. `addTooltipWithShortcut()`
Adds a tooltip to any component.

```java
// Add custom tooltip with shortcut
KeyboardShortcutUtil.addTooltipWithShortcut(
    myButton, 
    "Cmd+P"
);
```

**Result**: Tooltip shows "Keyboard: Cmd+P"

#### 4. `addShortcutTooltip()`
Adds a platform-aware tooltip to a button.

```java
// Automatically shows Cmd or Ctrl based on platform
KeyboardShortcutUtil.addShortcutTooltip(
    printButton, 
    KeyEvent.VK_P
);
```

**Result on Mac**: "Keyboard: Cmd+P"  
**Result on Windows**: "Keyboard: Ctrl+P"

#### 5. `getShortcutDisplayText()`
Gets formatted shortcut text for display.

```java
String shortcut = KeyboardShortcutUtil.getShortcutDisplayText(KeyEvent.VK_S);
// Returns "Cmd+S" on Mac, "Ctrl+S" on Windows
```

#### 6. `getDialogShortcutsHelpText()`
Gets HTML-formatted help text for dialogs.

```java
String helpText = KeyboardShortcutUtil.getDialogShortcutsHelpText();
// Returns:
// "<html><b>Keyboard Shortcuts:</b><br>
//  • ENTER - Confirm/OK<br>
//  • ESC - Cancel/Close<br>
//  • Cmd+W - Close dialog</html>"
```

#### 7. `createShortcutsHelpLabel()`
Creates a pre-formatted help label.

```java
JLabel helpLabel = KeyboardShortcutUtil.createShortcutsHelpLabel();
// Add to dialog to show available shortcuts
```

## 🔄 Tooltip Behavior

### Appending to Existing Tooltips

If a button already has a tooltip, the shortcut is appended:

```java
button.setToolTipText("Save the document");
KeyboardShortcutUtil.addShortcutTooltip(button, KeyEvent.VK_S);
// Result: "Save the document [Cmd+S]"
```

### Creating New Tooltips

If no tooltip exists, one is created:

```java
// Button has no tooltip
KeyboardShortcutUtil.addShortcutTooltip(button, KeyEvent.VK_S);
// Result: "Keyboard: Cmd+S"
```

## 📊 Updated Dialogs

All **21 dialogs** now have tooltip support:

### Main Application Dialogs (11)
1. ✅ AboutDialog
2. ✅ JDialogPartnerConfig
3. ✅ JDialogPreferences
4. ✅ JDialogManualSend
5. ✅ JDialogCreateDataSheet
6. ✅ DialogSendCEM
7. ✅ DialogMessageDetails
8. ✅ DialogCEMOverview
9. ✅ JDialogConfigureEventShell
10. ✅ JDialogConfigureEventMoveToDir
11. ✅ JDialogConfigureEventMoveToPartner

### Certificate Management Dialogs (10)
12. ✅ JDialogCertificates (Sign/Crypt & TLS)
13. ✅ JDialogRenameEntry
14. ✅ JDialogExport
15. ✅ JDialogExportPrivateKey
16. ✅ JDialogExportCertificate
17. ✅ JDialogImport
18. ✅ JDialogImportKeyFromKeystore
19. ✅ JDialogExportKeystore
20. ✅ JDialogGenerateKey
21. ✅ JDialogEditSubjectAlternativeNames

## 🧪 Testing the Tooltip System

### Test 1: Basic Dialog
1. Open any dialog (e.g., About dialog)
2. Hover over the **OK** button
3. **Expected**: Tooltip appears showing "Keyboard: ENTER"
4. Hover over the **Cancel** button (if present)
5. **Expected**: Tooltip appears showing "Keyboard: ESC"

### Test 2: Certificate Dialog
1. Open **Sign/Crypt** or **TLS** dialog
2. Hover over the **OK** button
3. **Expected**: Tooltip shows "Keyboard: ENTER"
4. Press **ENTER** → Dialog should confirm/close
5. Press **ESC** → Dialog should close

### Test 3: Platform Detection
**On Mac:**
1. Hover over any button
2. **Expected**: Tooltips show "Cmd+..." not "Ctrl+..."

**On Windows:**
1. Hover over any button
2. **Expected**: Tooltips show "Ctrl+..." not "Cmd+..."

### Test 4: Tooltip Timing
1. Hover over a button
2. **Expected**: Tooltip appears after ~500ms (default delay)
3. Move mouse away
4. Hover again
5. **Expected**: Tooltip appears immediately (repeat delay is shorter)

## 💡 User Benefits

### For New Users
- 🎓 **Self-documenting**: Learn shortcuts by exploring
- 🚀 **No manual needed**: Discover features naturally
- 🎯 **Context-aware**: See shortcuts for current action

### For Experienced Users
- ⚡ **Quick reference**: Reminder when needed
- 🔍 **Discoverable**: Find shortcuts they didn't know existed
- 🎨 **Non-intrusive**: Only visible on hover

### For All Users
- ♿ **Accessibility**: Better keyboard navigation
- 🌍 **Platform-aware**: Correct shortcuts for their OS
- 💪 **Productivity**: Faster workflow once learned

## 🎨 Customization Options

### Tooltip Delay (Optional)
You can adjust tooltip timing globally:

```java
// Make tooltips appear faster
ToolTipManager.sharedInstance().setInitialDelay(300); // default: 750ms

// Make tooltips stay longer
ToolTipManager.sharedInstance().setDismissDelay(20000); // default: 4000ms
```

### Custom Tooltip Styles
You can customize tooltip text for specific buttons:

```java
// Custom tooltip with description AND shortcut
button.setToolTipText("Save your work and continue");
KeyboardShortcutUtil.addShortcutTooltip(button, KeyEvent.VK_S);
// Result: "Save your work and continue [Cmd+S]"
```

### Multi-Line Tooltips
For complex operations:

```java
String tooltip = "<html>" +
    "Save Document<br>" +
    "• Saves all changes<br>" +
    "• Creates backup<br>" +
    "Shortcut: " + KeyboardShortcutUtil.getShortcutDisplayText(KeyEvent.VK_S) +
    "</html>";
button.setToolTipText(tooltip);
```

## 📖 Usage Examples

### Example 1: Simple Dialog with Tooltips

```java
public class MyDialog extends JDialog {
    private JButton okButton;
    private JButton cancelButton;
    
    public MyDialog() {
        initComponents();
        setupKeyboardShortcuts();
    }
    
    private void setupKeyboardShortcuts() {
        // This ONE line adds shortcuts AND tooltips
        KeyboardShortcutUtil.setupDialogKeyBindingsWithTooltips(
            this, okButton, cancelButton
        );
    }
}
```

**User Experience**:
- Hovers over OK → sees "Keyboard: ENTER"
- Hovers over Cancel → sees "Keyboard: ESC"
- Can press Cmd/Ctrl+W to close

### Example 2: Dialog with Custom Button Shortcuts

```java
public class MyDialog extends JDialog {
    private JButton saveButton;
    private JButton printButton;
    
    private void setupKeyboardShortcuts() {
        // Add Save shortcut with tooltip
        KeyboardShortcutUtil.addButtonShortcutWithTooltip(
            saveButton, 
            KeyEvent.VK_S, 
            "SAVE"
        );
        
        // Add Print shortcut with tooltip
        KeyboardShortcutUtil.addButtonShortcutWithTooltip(
            printButton, 
            KeyEvent.VK_P, 
            "PRINT"
        );
    }
}
```

**User Experience**:
- Hovers over Save → sees "Keyboard: Cmd+S" (Mac) or "Ctrl+S" (Windows)
- Hovers over Print → sees "Keyboard: Cmd+P" (Mac) or "Ctrl+P" (Windows)
- Can use keyboard shortcuts immediately

### Example 3: Adding Help Label to Dialog

```java
public class MyDialog extends JDialog {
    
    private void initComponents() {
        // ... other components ...
        
        // Add a help label at the bottom
        JLabel helpLabel = KeyboardShortcutUtil.createShortcutsHelpLabel();
        bottomPanel.add(helpLabel);
    }
}
```

**User Experience**:
- See a small gray label at bottom showing all dialog shortcuts
- Permanent reminder of available keyboard shortcuts

## 🔍 Troubleshooting

### Tooltips Not Appearing?

**Check 1**: Verify tooltip is set
```java
System.out.println(button.getToolTipText()); // Should not be null
```

**Check 2**: Check tooltip manager settings
```java
ToolTipManager manager = ToolTipManager.sharedInstance();
System.out.println("Enabled: " + manager.isEnabled()); // Should be true
System.out.println("Initial delay: " + manager.getInitialDelay()); // Usually 750ms
```

**Check 3**: Ensure button is enabled
```java
button.setEnabled(true); // Tooltips don't show on disabled components
```

### Wrong Shortcut Keys Displayed?

**Issue**: Shows "Ctrl" on Mac or "Cmd" on Windows

**Solution**: Check platform detection
```java
System.out.println("OS: " + System.getProperty("os.name"));
System.out.println("Key: " + KeyboardShortcutUtil.getMenuShortcutKeyName());
// Should show "Cmd" on Mac, "Ctrl" elsewhere
```

## 📚 Related Files

### Core Implementation
- `src/main/java/de/mendelson/util/KeyboardShortcutUtil.java` - Main utility class

### Examples
- `src/main/java/de/mendelson/util/examples/ExampleDialogWithTooltips.java` - Demo with tooltips
- `src/main/java/de/mendelson/util/examples/ExampleDialogWithShortcuts.java` - Basic demo

### Documentation
- `KEYBOARD_SHORTCUTS.md` - Complete API reference
- `KEYBOARD_SHORTCUTS_IMPLEMENTATION.md` - Implementation details
- `TOOLTIP_HELP_SYSTEM.md` - This file

## ✅ Conclusion

The tooltip help system makes keyboard shortcuts **discoverable without reading documentation**. Users can:

1. ✅ Hover over buttons to see shortcuts
2. ✅ Learn shortcuts naturally while using the app
3. ✅ See platform-appropriate shortcuts (Cmd on Mac, Ctrl on Windows)
4. ✅ Use the app more efficiently without training

This improves usability for both new and experienced users!
