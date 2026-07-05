# Save Timing - Option 2 Implementation

## Summary

Implemented **Option 2: Explicit Save-All** with unsaved changes warning for REST API Response Rules configuration.

## User Experience

### **Before (Problematic)**
- ❌ Each rule had individual "Save" button
- ❌ Inconsistent: some changes saved immediately, others didn't
- ❌ Easy to forget which rules were saved
- ❌ Tedious to save many rules individually

### **After (Option 2)**
- ✅ Single "Save All Rules" button at the top
- ✅ All edits stay in local state (UI only)
- ✅ **Warning banner appears** when there are unsaved changes
- ✅ **Browser warning** if user tries to leave/close page with unsaved changes
- ✅ Explicit "Discard Changes" button to reset
- ✅ Save button disabled/grayed when no changes

## Features

### 1. **Unsaved Changes Warning Banner**
```
⚠️ You have unsaved changes! Click "Save All Rules" to persist your changes.
[Save All Rules] [Discard Changes]
```

- **Yellow warning bar** appears at the top when changes are made
- Shows immediately when user:
  - Edits any field (method, path, status, body, etc.)
  - Enables/disables a rule
  - Moves rules up/down (changes priority)
- Disappears after saving or discarding

### 2. **Browser Leave Warning**
```javascript
window.addEventListener('beforeunload', (e) => {
  if (hasUnsavedChanges) {
    e.preventDefault();
    e.returnValue = ''; // Shows browser's default warning
  }
});
```

- Browser shows: **"Changes you made may not be saved"**
- Prevents accidental data loss
- Standard browser behavior (can't customize message text)

### 3. **Save All Rules Button**
- Located in header (always visible)
- **Blue** when changes exist → **Gray** when no changes
- **Disabled** when no unsaved changes (can't click)
- Saves ALL rules in one transaction
- Updates priorities if rules were reordered

### 4. **Discard Changes Button**
- Shows confirmation: "Discard all unsaved changes?"
- Reloads rules from database (fresh query)
- Resets `hasUnsavedChanges` flag
- Useful for "undo all" functionality

### 5. **Add New Rule**
- Immediately creates rule in database (no unsaved state)
- Resets `hasUnsavedChanges` to `false` after creation
- User can edit it immediately without warning

### 6. **Delete Rule**
- Shows confirmation: "Are you sure you want to delete this rule?"
- Immediately deletes from database
- No unsaved state for deletions

## State Management

### **hasUnsavedChanges Flag**

**Set to `true` when:**
- ✅ User edits any field in a rule
- ✅ User toggles enabled/disabled
- ✅ User moves rule up/down (priority change)

**Set to `false` when:**
- ✅ Rules loaded from database
- ✅ User clicks "Save All Rules"
- ✅ User clicks "Discard Changes"
- ✅ User creates new rule (immediate save)
- ✅ User deletes a rule (immediate delete)

## Code Changes

### Frontend (`MyApiConfig.jsx`)

**Added:**
1. `hasUnsavedChanges` state variable
2. `useEffect` hook for `beforeunload` event
3. `saveAllMutation` - saves all rules at once
4. `discardChanges()` - resets to database state
5. Warning banner component
6. Modified `updateRule()` to set `hasUnsavedChanges = true`
7. Modified `moveRuleUp/Down()` to set `hasUnsavedChanges = true`

**Removed:**
1. Individual "Save" buttons from each rule card
2. `updateMutation` (no longer needed)
3. `saveRule()` function (replaced by saveAllMutation)

**Kept:**
1. `createMutation` - for "Add New Rule"
2. `deleteMutation` - for immediate deletion

### Backend (No Changes Needed)
- Existing endpoints work perfectly:
  - `PUT /user/api-response/rules/{id}` - updates single rule
  - `PUT /user/api-response/rules/reorder` - updates priorities
- Save-all calls these endpoints sequentially

## User Workflow

### **Typical Usage**
1. User opens "My REST API Response" tab
2. Edits multiple rules (method, path, body, etc.)
3. ⚠️ **Warning banner appears**: "You have unsaved changes!"
4. User clicks **"Save All Rules"**
5. Toast notification: ✅ "All rules saved successfully"
6. Warning banner disappears

### **Discard Changes**
1. User edits some rules
2. ⚠️ Warning appears
3. User realizes mistake, clicks **"Discard Changes"**
4. Browser asks: "Discard all unsaved changes?"
5. Confirms → rules reload from database
6. Warning disappears

### **Accidental Page Close**
1. User edits rules but forgets to save
2. User tries to close tab/navigate away
3. 🛑 **Browser shows**: "Changes you made may not be saved"
4. User can:
   - **Stay on page** → save changes
   - **Leave** → lose changes

### **Priority Reordering**
1. User moves Rule #3 to position #1 (click ↑↑)
2. ⚠️ Warning appears (priorities changed)
3. User clicks "Save All Rules"
4. Priorities updated in database

## Visual Indicators

### Warning Banner (Shown when `hasUnsavedChanges === true`)
```
┌─────────────────────────────────────────────────────────────┐
│ ⚠️ You have unsaved changes! Click "Save All Rules" to...  │
│                              [Save All Rules] [Discard...]  │
└─────────────────────────────────────────────────────────────┘
```
- Background: `#fff3cd` (yellow)
- Border: `#ffc107` (amber)

### Save Button States

**Has Changes (Active)**
```
┌─────────────────┐
│ Save All Rules  │  ← Blue background (#007bff)
└─────────────────┘
```

**No Changes (Disabled)**
```
┌─────────────────┐
│ Save All Rules  │  ← Gray background (#6c757d), opacity 0.6
└─────────────────┘    Can't click
```

## Benefits

1. ✅ **Clear Intent**: User explicitly decides when to save
2. ✅ **No Surprises**: Changes don't auto-save unexpectedly
3. ✅ **Batch Operation**: One save for all changes (efficient)
4. ✅ **Data Loss Prevention**: Warning before leaving page
5. ✅ **Undo Capability**: Discard all changes at once
6. ✅ **Visual Feedback**: Clear indication of unsaved state
7. ✅ **Consistent UX**: Matches "My REST API Auth" tab pattern

## Edge Cases Handled

### User creates rule then edits it
- Rule created in DB immediately
- Edits are local until "Save All"
- Warning appears for edits

### User deletes rule with unsaved changes
- Deletion happens immediately
- Unsaved changes in other rules preserved
- Warning still shown for other rules

### User moves rules then navigates away
- Browser warning prevents accidental loss
- User can return and save

### Save fails (network error)
- Toast error shown
- `hasUnsavedChanges` stays `true`
- User can retry save

## Testing Checklist

- [ ] Edit a rule field → warning appears
- [ ] Click "Save All Rules" → warning disappears
- [ ] Click "Discard Changes" → rules reset, warning gone
- [ ] Move rule up/down → warning appears
- [ ] Try to close browser tab → browser shows warning
- [ ] Create new rule → no warning (saved immediately)
- [ ] Delete rule → immediate deletion, warning stays for other changes
- [ ] Save button disabled when no changes
- [ ] Save button enabled when changes exist

## Comparison with Auth Config

The "My REST API Auth" tab uses the **same pattern**:
- Edits stay in local state
- Single "Save Configuration" button
- No unsaved changes warning (we added this improvement!)

This implementation is **consistent** with existing UX patterns in the application.
