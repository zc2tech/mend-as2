# Keyboard Shortcut Utility for Swing Applications

This utility provides Mac-friendly keyboard shortcuts for Swing JDialog and JButton components.

## Overview

The `KeyboardShortcutUtil` class handles platform-specific keyboard shortcuts, automatically using:
- **Command (⌘)** key on macOS
- **Ctrl** key on Windows/Linux

## Quick Start

### 1. Simple Dialog Setup (Recommended)

The easiest way to add keyboard shortcuts to a dialog:

```java
import de.mendelson.util.KeyboardShortcutUtil;

public class MyDialog extends JDialog {
    private JButton okButton;
    private JButton cancelButton;
    
    public MyDialog(JFrame parent) {
        super(parent, "My Dialog", true);
        initComponents();
        
        // This single line adds:
        // - ESC to close dialog
        // - ENTER to trigger OK button
        // - Cmd+W/Ctrl+W to close dialog
        KeyboardShortcutUtil.setupDialogKeyBindings(this, okButton, cancelButton);
    }
}
```

### 2. Add Shortcuts to Specific Buttons

```java
// Cmd+S/Ctrl+S for Save button
KeyboardShortcutUtil.addButtonShortcut(saveButton, KeyEvent.VK_S, "SAVE_ACTION");

// Cmd+P/Ctrl+P for Print button
KeyboardShortcutUtil.addButtonShortcut(printButton, KeyEvent.VK_P, "PRINT_ACTION");

// Cmd+Shift+S/Ctrl+Shift+S for Save As
KeyboardShortcutUtil.addButtonShortcut(
    saveAsButton, 
    KeyEvent.VK_S, 
    KeyboardShortcutUtil.getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK,
    "SAVE_AS_ACTION"
);
```

### 3. Display Shortcut in Button Text (Optional)

```java
// Changes button text from "Save" to "Save (Cmd+S)" on Mac or "Save (Ctrl+S)" on Windows
KeyboardShortcutUtil.setButtonTextWithShortcut(saveButton, "Save", KeyEvent.VK_S);
```

## Common Keyboard Shortcuts

### Standard Dialog Shortcuts

| Action | Mac | Windows/Linux |
|--------|-----|---------------|
| Close dialog | ESC or Cmd+W | ESC or Ctrl+W |
| Trigger default button | ENTER | ENTER |

### Common Application Shortcuts

| Action | Mac | Windows/Linux | Key Code |
|--------|-----|---------------|----------|
| Save | Cmd+S | Ctrl+S | `KeyEvent.VK_S` |
| Open | Cmd+O | Ctrl+O | `KeyEvent.VK_O` |
| New | Cmd+N | Ctrl+N | `KeyEvent.VK_N` |
| Print | Cmd+P | Ctrl+P | `KeyEvent.VK_P` |
| Find | Cmd+F | Ctrl+F | `KeyEvent.VK_F` |
| Close | Cmd+W | Ctrl+W | `KeyEvent.VK_W` |
| Quit | Cmd+Q | Ctrl+Q | `KeyEvent.VK_Q` |

## API Reference

### Core Methods

#### `setupDialogKeyBindings(JDialog, JButton, JButton)`
Sets up common dialog shortcuts in one call.
- **ESC**: Close dialog
- **ENTER**: Trigger OK button (if provided)
- **Cmd/Ctrl+W**: Close dialog

```java
KeyboardShortcutUtil.setupDialogKeyBindings(dialog, okButton, cancelButton);
```

#### `addEscapeKeyBinding(JDialog)`
Adds ESC key to close the dialog.

```java
KeyboardShortcutUtil.addEscapeKeyBinding(dialog);
```

#### `addEnterKeyBinding(JDialog, JButton)`
Sets a button as the default button (triggered by ENTER).

```java
KeyboardShortcutUtil.addEnterKeyBinding(dialog, okButton);
```

#### `addButtonShortcut(JButton, int, String)`
Adds a platform-specific shortcut to a button.

```java
KeyboardShortcutUtil.addButtonShortcut(saveButton, KeyEvent.VK_S, "SAVE_ACTION");
```

#### `addButtonShortcut(JButton, int, int, String)`
Adds a custom shortcut with specific modifiers.

```java
// Cmd+Shift+S or Ctrl+Shift+S
KeyboardShortcutUtil.addButtonShortcut(
    saveAsButton,
    KeyEvent.VK_S,
    KeyboardShortcutUtil.getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK,
    "SAVE_AS"
);
```

### Helper Methods

#### `getMenuShortcutKeyMask()`
Returns the platform-specific menu shortcut modifier (Command on Mac, Ctrl elsewhere).

```java
int modifier = KeyboardShortcutUtil.getMenuShortcutKeyMask();
```

#### `getMenuShortcutKeyName()`
Returns "Cmd" on Mac, "Ctrl" on other platforms.

```java
String keyName = KeyboardShortcutUtil.getMenuShortcutKeyName(); // "Cmd" or "Ctrl"
```

#### `createMenuShortcut(int)`
Creates a KeyStroke with the platform's menu shortcut key.

```java
KeyStroke saveShortcut = KeyboardShortcutUtil.createMenuShortcut(KeyEvent.VK_S);
```

#### `createMenuShortcut(int, int)`
Creates a KeyStroke with platform shortcut and additional modifiers.

```java
KeyStroke shortcut = KeyboardShortcutUtil.createMenuShortcut(
    KeyEvent.VK_S, 
    InputEvent.SHIFT_DOWN_MASK
);
```

## Advanced Usage

### Manual Setup Example

If you need more control, you can set up shortcuts manually:

```java
private void setupCustomShortcuts() {
    JRootPane rootPane = getRootPane();
    
    // Create platform-specific keystroke
    KeyStroke saveShortcut = KeyboardShortcutUtil.createMenuShortcut(KeyEvent.VK_S);
    
    // Add to input map
    rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
           .put(saveShortcut, "SAVE");
    
    // Add action
    rootPane.getActionMap().put("SAVE", new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (saveButton.isEnabled()) {
                saveButton.doClick();
            }
        }
    });
}
```

### Multiple Shortcuts for One Action

```java
// Add both Cmd+S and Cmd+Enter for save
KeyboardShortcutUtil.addButtonShortcut(saveButton, KeyEvent.VK_S, "SAVE");

KeyStroke cmdEnter = KeyboardShortcutUtil.createMenuShortcut(KeyEvent.VK_ENTER);
getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(cmdEnter, "SAVE");
```

### Disabling Shortcuts

```java
// Remove a specific shortcut
getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
           .remove(KeyboardShortcutUtil.createMenuShortcut(KeyEvent.VK_S));

// Clear all shortcuts
getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).clear();
```

## Best Practices

1. **Always use platform-specific shortcuts**: Never hardcode `InputEvent.CTRL_DOWN_MASK`. Use `getMenuShortcutKeyMask()` instead.

2. **Test on Mac**: Mac users expect Command key, not Control key for standard shortcuts.

3. **Standard shortcuts first**: Follow platform conventions:
   - Cmd/Ctrl+S for Save
   - Cmd/Ctrl+O for Open
   - ESC to close dialogs
   - ENTER for default action

4. **Show shortcuts in UI**: Consider using tooltips or button text to show available shortcuts:
   ```java
   saveButton.setToolTipText("Save (" + 
       KeyboardShortcutUtil.getMenuShortcutKeyName() + "+S)");
   ```

5. **Check button state**: Always check if a button is enabled before triggering it.

6. **Use WHEN_IN_FOCUSED_WINDOW**: This ensures shortcuts work regardless of focus:
   ```java
   component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
   ```

7. **Avoid conflicts**: Don't use shortcuts that conflict with system shortcuts:
   - Cmd+Q/Ctrl+Q (Quit) - usually handled by application
   - Cmd+Tab/Alt+Tab (Switch apps) - system level
   - Cmd+Space (Spotlight on Mac) - system level

## Examples

See `de.mendelson.util.examples.ExampleDialogWithShortcuts` for a complete working example.

To run the example:
```java
java -cp target/classes de.mendelson.util.examples.ExampleDialogWithShortcuts
```

## Platform Detection

The utility automatically detects macOS by checking the system property:
```java
System.getProperty("os.name").toLowerCase().contains("mac")
```

On macOS, it uses:
- Command key (⌘) - `InputEvent.META_DOWN_MASK`
- Via `Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()`

On Windows/Linux, it uses:
- Control key - `InputEvent.CTRL_DOWN_MASK`

## Migration Guide

If you have existing code using hardcoded Ctrl shortcuts:

### Before:
```java
// Old way - doesn't work properly on Mac
KeyStroke saveShortcut = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
```

### After:
```java
// New way - works on all platforms
KeyboardShortcutUtil.addButtonShortcut(saveButton, KeyEvent.VK_S, "SAVE_ACTION");
// or
KeyStroke saveShortcut = KeyboardShortcutUtil.createMenuShortcut(KeyEvent.VK_S);
```

## Troubleshooting

### Shortcuts not working on Mac
- Make sure you're using `getMenuShortcutKeyMask()` instead of `InputEvent.CTRL_DOWN_MASK`
- Check that you're using `WHEN_IN_FOCUSED_WINDOW` for the input map

### Multiple shortcuts triggering
- Ensure you're using unique action names for different shortcuts
- Check that you haven't added the same shortcut multiple times

### Button not responding
- Verify the button is enabled: `button.isEnabled()`
- Check that the action listener is properly attached
- Ensure the shortcut action calls `button.doClick()` not just the action listener

## License

Copyright (C) mendelson-e-commerce GmbH Berlin Germany
