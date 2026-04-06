# Visual Guide: Hover Tooltips for Keyboard Shortcuts

## 🎯 What Users Will Experience

### Before (No Tooltips)
```
User thinks: "I wonder if there's a keyboard shortcut..."
User action: Opens manual or Google search
Result: Lost productivity, frustration
```

### After (With Tooltips)
```
User thinks: "I wonder if there's a keyboard shortcut..."
User action: Hovers over button
Result: ✅ Sees "Keyboard: Cmd+S" instantly!
```

---

## 📸 Visual Examples

### Example 1: About Dialog

```
┌──────────────────────────────────────────────────┐
│  About mendelson AS2                             │
├──────────────────────────────────────────────────┤
│                                                  │
│  mendelson AS2 v2.0                              │
│  Build: 67                                       │
│                                                  │
│  [License Tab] [About Tab]                       │
│                                                  │
│  Copyright © mendelson-e-commerce GmbH           │
│                                                  │
├──────────────────────────────────────────────────┤
│                                    ┌──────────┐  │
│                                    │   OK     │  │
│                                    └──────────┘  │
│                                          ↑       │
│                      When hovering:      │       │
│                      ┌─────────────────────────┐ │
│                      │ Keyboard: ENTER         │ │
│                      └─────────────────────────┘ │
└──────────────────────────────────────────────────┘
```

### Example 2: Partner Configuration Dialog

```
┌──────────────────────────────────────────────────┐
│  Partner Configuration                           │
├──────────────────────────────────────────────────┤
│  Partner: ACME Corp                              │
│                                                  │
│  ┌─────────────┐ ┌─────────────┐                │
│  │   New       │ │   Delete    │                │
│  └─────────────┘ └─────────────┘                │
│      ↑                                           │
│      │ Hover shows:                              │
│  ┌────────────────────────────┐                  │
│  │ Create new partner [Cmd+N] │                  │
│  └────────────────────────────┘                  │
│                                                  │
│  [Settings panel...]                             │
│                                                  │
├──────────────────────────────────────────────────┤
│        ┌────────┐  ┌────────┐                    │
│        │ Cancel │  │   OK   │                    │
│        └────────┘  └────────┘                    │
│           ↑            ↑                         │
│  Hover:   │            └─ Hover:                 │
│  ┌──────────────┐     ┌──────────────┐          │
│  │Keyboard: ESC │     │Keyboard: ENTER│         │
│  └──────────────┘     └──────────────┘          │
└──────────────────────────────────────────────────┘
```

### Example 3: Certificate Management Dialog (Sign/Crypt)

```
┌──────────────────────────────────────────────────┐
│  Sign/Crypt Certificates              [X]        │
├──────────────────────────────────────────────────┤
│  ┌────────┐ ┌────────┐ ┌────────┐ ┌─────────┐   │
│  │ Import │ │ Export │ │ Rename │ │ Delete  │   │
│  └────────┘ └────────┘ └────────┘ └─────────┘   │
│      ↑                                           │
│      │ When hovering:                            │
│  ┌────────────────────────────────┐              │
│  │ Import certificate [Cmd+I]     │              │
│  └────────────────────────────────┘              │
│                                                  │
│  ┌────────────────────────────────────────────┐  │
│  │ Alias           | Issuer      | Valid To   │  │
│  ├────────────────────────────────────────────┤  │
│  │ my-cert         | CA          | 2027-01-01 │  │
│  │ partner-cert    | External CA | 2026-12-31 │  │
│  └────────────────────────────────────────────┘  │
│                                                  │
│  Certificate Details:                            │
│  Subject: CN=my-cert                             │
│  Issuer: CN=CA                                   │
│                                                  │
├──────────────────────────────────────────────────┤
│                                    ┌──────────┐  │
│                                    │   OK     │  │
│                                    └──────────┘  │
│  Tip: Press ESC or Cmd+W to close       ↑       │
│                                  Hover shows:    │
│                              ┌──────────────────┐│
│                              │ Keyboard: ENTER  ││
│                              └──────────────────┘│
└──────────────────────────────────────────────────┘
```

### Example 4: Manual Send Dialog

```
┌──────────────────────────────────────────────────┐
│  Send File Manually                              │
├──────────────────────────────────────────────────┤
│  Send to: [Partner Dropdown ▼]                   │
│                                                  │
│  File: [____________________] [Browse...]        │
│                                       ↑          │
│                          Hover:       │          │
│                      ┌──────────────────────┐    │
│                      │ Browse file [Cmd+B]  │    │
│                      └──────────────────────┘    │
│                                                  │
│  ○ Send File                                     │
│  ○ Test Connection                               │
│                                                  │
├──────────────────────────────────────────────────┤
│             ┌────────┐  ┌────────┐               │
│             │ Cancel │  │  Send  │               │
│             └────────┘  └────────┘               │
│                ↑            ↑                    │
│       Hover:   │            └─ Hover:            │
│   ┌──────────────┐      ┌──────────────┐        │
│   │Keyboard: ESC │      │Keyboard: ENTER│       │
│   └──────────────┘      └──────────────┘        │
└──────────────────────────────────────────────────┘
```

---

## 🎨 Tooltip Appearance

### Mac Style
```
┌─────────────────────────┐
│  Button Text            │
└─────────────────────────┘
         ↓
    (on hover)
         ↓
┌─────────────────────────┐
│ Keyboard: Cmd+S         │  ← Light yellow background
└─────────────────────────┘   Small font, appears after 0.5s
```

### Windows Style
```
┌─────────────────────────┐
│  Button Text            │
└─────────────────────────┘
         ↓
    (on hover)
         ↓
┌─────────────────────────┐
│ Keyboard: Ctrl+S        │  ← Light yellow background
└─────────────────────────┘   Small font, appears after 0.5s
```

---

## 📊 Tooltip Content Examples

### Standard Dialog Buttons

| Button | Tooltip Text | What It Means |
|--------|--------------|---------------|
| OK | `Keyboard: ENTER` | Press Enter to confirm |
| Cancel | `Keyboard: ESC` | Press Escape to cancel |
| Save | `Keyboard: ENTER` | Press Enter to save |
| Close | `Keyboard: ESC` | Press Escape to close |

### Action Buttons with Shortcuts

| Button | Mac Tooltip | Windows Tooltip |
|--------|-------------|-----------------|
| Save | `Keyboard: Cmd+S` | `Keyboard: Ctrl+S` |
| Print | `Keyboard: Cmd+P` | `Keyboard: Ctrl+P` |
| New | `Keyboard: Cmd+N` | `Keyboard: Ctrl+N` |
| Delete | `Keyboard: Cmd+D` | `Keyboard: Ctrl+D` |
| Import | `Keyboard: Cmd+I` | `Keyboard: Ctrl+I` |
| Export | `Keyboard: Cmd+E` | `Keyboard: Ctrl+E` |

### Buttons with Existing Descriptions

| Button | Original Tooltip | Enhanced Tooltip |
|--------|------------------|------------------|
| Save | "Save document" | "Save document [Cmd+S]" |
| Print | "Print current view" | "Print current view [Cmd+P]" |
| Browse | "Select a file" | "Select a file [Cmd+B]" |

---

## 🔄 User Workflow Examples

### Scenario 1: New User Exploring

```
Step 1: User opens Partner Configuration dialog
        └─ Doesn't know any shortcuts yet

Step 2: User moves mouse around, exploring UI
        └─ Accidentally hovers over "Delete" button

Step 3: Tooltip appears: "Delete partner [Cmd+D]"
        └─ User thinks: "Oh! I can press Cmd+D to delete"

Step 4: User hovers over "OK" button
        └─ Tooltip appears: "Keyboard: ENTER"
        └─ User thinks: "And ENTER to save"

Step 5: User clicks OK this time, but...
        └─ Next time, user presses ENTER directly!
        └─ Productivity improved! ✅
```

### Scenario 2: Power User Discovering New Shortcuts

```
Step 1: Expert user has used app for years
        └─ Knows ESC and ENTER shortcuts

Step 2: New version adds Cmd+W to close
        └─ User doesn't read changelog

Step 3: User hovers over any button
        └─ Sees tooltip with shortcut info
        └─ Notices: "ESC or Cmd+W to close" in status

Step 4: User tries Cmd+W
        └─ Dialog closes!
        └─ User thinks: "This is more natural!"

Step 5: User starts using Cmd+W everywhere
        └─ Even more productive! ✅
```

### Scenario 3: Keyboard-Only User

```
Step 1: User prefers keyboard navigation
        └─ Tabs through dialog buttons

Step 2: When button gets focus, tooltip appears
        └─ Shows keyboard shortcut
        └─ User memorizes: "Cmd+S for Save"

Step 3: Next time, user skips tabbing
        └─ Presses Cmd+S directly
        └─ Much faster workflow! ✅
```

---

## 🎯 User Testing Scenarios

### Test 1: Discoverability
**Goal**: Can new users find shortcuts without documentation?

1. Give user a task: "Save a partner configuration"
2. Don't tell them about shortcuts
3. Observe: Do they hover and discover shortcuts?
4. **Success metric**: User finds shortcut within 30 seconds

### Test 2: Platform Awareness
**Goal**: Do users see correct shortcuts for their platform?

1. Test on Mac: User should see "Cmd+..."
2. Test on Windows: User should see "Ctrl+..."
3. **Success metric**: 100% correct platform display

### Test 3: Learning Curve
**Goal**: Do users learn and remember shortcuts?

1. First dialog: User clicks button
2. Sees tooltip with shortcut
3. Second dialog: Does user try the shortcut?
4. **Success metric**: 70%+ users try shortcut on second use

---

## 💡 Best Practices for Tooltips

### ✅ DO:
- Keep tooltips concise
- Show actual key names (Cmd, Ctrl, S, P)
- Use consistent format: "Keyboard: Cmd+S"
- Append to existing tooltips: "Save document [Cmd+S]"
- Show platform-appropriate shortcuts

### ❌ DON'T:
- Make tooltips too long (max 2 lines)
- Use technical jargon
- Show shortcuts that don't work
- Hide tooltips on disabled buttons (Swing does this automatically)
- Use different formats in different dialogs

---

## 📈 Expected Impact

### Metrics to Track

1. **Discovery Time**
   - Before: Users need manual/training
   - After: Self-discovery through tooltips
   - **Expected improvement**: 80% reduction in discovery time

2. **Keyboard Usage**
   - Before: 20% of users use keyboard shortcuts
   - After: 60% of users use keyboard shortcuts
   - **Expected improvement**: 3x increase

3. **User Satisfaction**
   - Before: "How do I...?" support requests
   - After: "This is so easy to use!" feedback
   - **Expected improvement**: Reduced support requests

4. **Productivity**
   - Before: 3-5 clicks per action
   - After: 1 keystroke per action
   - **Expected improvement**: 3-5x faster workflow

---

## 🎊 Conclusion

The tooltip help system transforms keyboard shortcuts from **hidden features** into **discoverable tools**.

### Key Benefits:
✅ **No training needed** - Users learn by exploring  
✅ **Immediate feedback** - Hover and see  
✅ **Platform-aware** - Shows correct shortcuts  
✅ **Non-intrusive** - Only visible when needed  
✅ **Increases adoption** - More users will use shortcuts  

This is a **win-win**: New users learn faster, experienced users discover more features!
