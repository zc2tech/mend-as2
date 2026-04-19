# IP Whitelist SwingUI - Simplified Direct Access Architecture

## Architecture Decision

**Approach:** Direct database access (no client-server messaging)
- SwingUI dialogs directly use `IPWhitelistAccessDB` and `PreferencesAS2`
- No message classes, no message processor needed
- Simpler, faster, less code (~500 lines vs ~3000 lines)
- Consistent with localhost-only deployment after Mina removal

## Files to Create

### GUI Directory: `/src/main/java/de/mendelson/comm/as2/security/ipwhitelist/gui/`

1. **JDialogIPWhitelistManagement.java** (~400 lines)
   - Main dialog with JTabbedPane (5 tabs)
   - Directly calls IPWhitelistAccessDB and PreferencesAS2
   - Background threads for database operations
   - SwingUtilities.invokeLater for UI updates

2. **JDialogEditIPWhitelist.java** (~200 lines)
   - Modal dialog for add/edit entries
   - Direct save via IPWhitelistAccessDB
   - Validation logic

3. **TableModelGlobalWhitelist.java** (~100 lines)
   - Extends AbstractTableModel
   - Holds List<IPWhitelistEntry>

4. **TableModelPartnerWhitelist.java** (~80 lines)
5. **TableModelUserWhitelist.java** (~80 lines)
6. **TableModelBlockLog.java** (~100 lines)

7. **ResourceBundleIPWhitelist.java** (~150 lines)
   - English strings
8. **ResourceBundleIPWhitelist_de.java** (~150 lines)
   - German translations

### Integration Files (modify existing):

9. **AS2Gui.java** - Add menu item
10. **Permissions.java** - Add permission constant

**Total:** ~1260 lines of new code (vs ~3000 with client-server)

## Data Access Pattern

```java
// In JDialogIPWhitelistManagement:
private final IDBDriverManager dbDriverManager;
private final IPWhitelistAccessDB accessDB;

// Load data in background thread:
Thread loadThread = new Thread(() -> {
    try {
        List<IPWhitelistEntry> entries = accessDB.getGlobalWhitelist();
        SwingUtilities.invokeLater(() -> {
            tableModel.setData(entries);
        });
    } catch (Exception e) {
        // Show error dialog
    }
});
loadThread.setDaemon(true);
loadThread.start();
```

## Benefits

- ✅ Much simpler implementation
- ✅ Direct access to database (no serialization overhead)
- ✅ Consistent with localhost-only deployment
- ✅ Easier to maintain and debug
- ✅ Faster execution (no message passing)

## Next Steps

Create template files for:
1. Main management dialog (with 5 tabs structure)
2. Edit dialog (with validation)
3. Table models (4 files)
4. Resource bundles (2 files)
5. Integration points (2 modifications)
