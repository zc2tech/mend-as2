# VSCode Java Cache Issue - Oracle Help Dependency

## Date
2026-04-06

## Issue Description

VSCode showing error in `pom.xml`:
```
Missing artifact com.oracle:help:jar:1.0.0
Line 483, Column 10-21
```

## Root Cause

**Stale VSCode Java Language Server Cache**

The error is a **ghost error** - it references a dependency that:
1. ✅ Was already removed from pom.xml
2. ✅ Is commented out (lines 565-575)
3. ❌ Still exists in VSCode's Java Language Server cache

### What Happened:
1. Oracle JavaHelp dependencies were removed/commented out in the code
2. VSCode's Java Language Server cached the old pom.xml state
3. After branch merge, VSCode didn't refresh its cache
4. IDE shows error for non-existent dependency

### Verification:
```bash
$ grep -n "com.oracle.*help" pom.xml
# No results - dependency is gone

$ grep -n "com.oracle.ohj" pom.xml
567:            <groupId>com.oracle.ohj</groupId>
572:            <groupId>com.oracle.ohj</groupId>
# These are COMMENTED OUT (inside <!-- -->)
```

## Solution

### Quick Fix (Recommended)

**Step 1: Clean Maven Project**
```bash
cd /Users/I572958/SAPDevelop/github/mend-as2
mvn clean
```

**Step 2: Clean VSCode Java Workspace**

In VSCode:
1. Press `Cmd+Shift+P` (Mac) or `Ctrl+Shift+P` (Windows/Linux)
2. Type: `Java: Clean Java Language Server Workspace`
3. Select it and click "Restart and delete"
4. Wait for VSCode to restart and re-index

**Step 3: Reload Window (if error persists)**
1. Press `Cmd+Shift+P` / `Ctrl+Shift+P`
2. Type: `Developer: Reload Window`
3. Press Enter

### Alternative: Force Maven Update

If the above doesn't work, force Maven to re-resolve all dependencies:

```bash
# Update Maven dependencies
mvn dependency:resolve -U

# Rebuild
mvn clean compile -DskipTests
```

Then clean Java workspace in VSCode (Step 2 above).

### Nuclear Option: Delete All Caches

If errors still persist:

```bash
# Delete Maven local repository cache for this project
mvn dependency:purge-local-repository

# Delete VSCode Java caches manually
rm -rf ~/Library/Application\ Support/Code/User/workspaceStorage/*/redhat.java
rm -rf ~/.vscode/extensions/redhat.java-*/

# Restart VSCode completely (quit and reopen)
```

## Why This Happens

### VSCode Java Language Server Caching

VSCode's Java extension caches:
- POM file analysis
- Dependency resolution
- Project structure
- Classpath information

When you:
- Switch branches
- Merge branches
- Remove dependencies
- Update pom.xml

The cache may not automatically update.

### Common Triggers:
1. ✓ Branch switches (refactoring2026 → main)
2. ✓ Merge operations
3. ✓ Dependency removal
4. ✓ POM modifications while VSCode is open

## Verification After Fix

### 1. Check Maven Build
```bash
mvn clean compile -DskipTests
```
Should show: `BUILD SUCCESS` ✅

### 2. Check VSCode Problems Panel
- Press `Cmd+Shift+M` or View → Problems
- Should show: 0 errors ✅

### 3. Check pom.xml in VSCode
- Open `pom.xml`
- No red squiggly lines ✅
- No errors in Problems panel ✅

## Prevention

### Best Practices to Avoid This:

1. **After Branch Operations:**
   ```bash
   git checkout main
   mvn clean
   # Then in VSCode: Java: Clean Java Language Server Workspace
   ```

2. **After POM Changes:**
   - Save `pom.xml`
   - Wait for VSCode to show "Updating project configuration" in status bar
   - If no update happens after 10 seconds → clean workspace

3. **Regular Maintenance:**
   ```bash
   # Once a week or after major changes
   mvn clean
   # In VSCode: Java: Clean Java Language Server Workspace
   ```

4. **Settings (Optional):**
   Add to VSCode `settings.json`:
   ```json
   {
     "java.configuration.updateBuildConfiguration": "automatic",
     "java.autobuild.enabled": true
   }
   ```

## What Was in the POM

The removed dependencies (now commented out):

```xml
<!-- Lines 565-575 in pom.xml -->
<!-->
<dependency>
    <groupId>com.oracle.ohj</groupId>
    <artifactId>oracle_ice</artifactId>
    <version>12.1.3</version>
</dependency>
<dependency>
    <groupId>com.oracle.ohj</groupId>
    <artifactId>jewt</artifactId>
    <version>12.1.3</version>
</dependency>-->

<!-- Oracle JavaHelp dependencies removed - incompatible with JDK 17+ -->
```

These were removed because:
- Incompatible with JDK 17+
- Not available in Maven Central
- No longer needed (JavaHelp feature removed)

## Current Status

✅ **Code is Clean**
- No Oracle Help dependencies in pom.xml
- Dependencies properly commented out
- Maven builds successfully
- Only VSCode cache needs refresh

❌ **VSCode Cache Stale**
- Java Language Server has old state
- Shows ghost errors
- Needs workspace clean

## Action Required

**User Action Needed:**

1. In VSCode, press `Cmd+Shift+P`
2. Type: `Java: Clean Java Language Server Workspace`
3. Click "Restart and delete"
4. Wait for re-indexing to complete

**Estimated time:** 30-60 seconds

After this, all errors will disappear! ✅

## Similar Issues

If you see other "ghost errors" for:
- Missing classes
- Unresolved imports  
- Type errors that shouldn't exist

**Solution:** Same as above - clean Java Language Server workspace.

This is a common issue after:
- Git operations
- Dependency changes
- Project structure modifications

## Summary

- ✅ Your code is correct
- ✅ Maven builds successfully
- ✅ No actual dependency issues
- ❌ VSCode cache needs refresh
- 🔧 Solution: Clean Java Language Server Workspace

**This is NOT a code problem - just an IDE cache issue!**
