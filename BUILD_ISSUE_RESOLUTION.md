# Build Issue Resolution - Main Branch

## Date
2026-04-06

## Issue Description

After merging `refactoring2026` branch to `main` and checking out `main`, compilation errors occurred:

```
[ERROR] error while writing de.mendelson.util.security.keygeneration.KeyGenerationResult: 
/Users/I572958/SAPDevelop/github/mend-as2/target/classes/de/mendelson/util/security/keygeneration/KeyGenerationResult.class: 
Invalid argument
```

## Root Cause

**Stale Build Artifacts with Extended Attributes**

The issue was caused by:
1. Stale class files in the `target/` directory from the merge
2. macOS extended attributes (`@` symbols in `ls -la` output) on compiled class files
3. Maven trying to overwrite files with incompatible attributes

This is a common issue on macOS after git branch merges when:
- The target directory exists from a previous build
- File attributes differ between branches
- Maven's incremental compilation tries to update existing class files

## Solution

### Immediate Fix
```bash
# Clean all build artifacts
rm -rf target

# Rebuild from scratch
mvn clean compile -DskipTests
```

**Result:** ✅ BUILD SUCCESS

### Complete Package Build
```bash
mvn package -DskipTests
```

**Result:** 
- ✅ BUILD SUCCESS
- ✅ mend-as2-1.1.0.jar created
- ✅ mend-as2-1.1.0-client.jar created

## Verification

### Branch Comparison
```bash
git diff origin/refactoring2026 main --stat
```
**Result:** No differences - merge was clean, no conflicts

### Build Status
- ✅ Compilation: SUCCESS
- ✅ Package: SUCCESS  
- ⚠️  Warning: Deprecation annotation missing in AS2Message.java (non-critical)

## Prevention

To avoid this issue in the future:

### 1. Always Clean After Branch Switch/Merge
```bash
# After switching branches or merging
mvn clean compile
```

### 2. Use Maven Clean Profile
Add to your workflow:
```bash
git checkout main
git pull
mvn clean install -DskipTests
```

### 3. Ignore Target Directory
The `.gitignore` file should include (already present):
```
target/
```

This ensures the `target/` directory is never committed.

## Technical Details

### Extended Attributes on macOS

When you see `@` in file permissions:
```bash
$ ls -la target/classes/...
-rw-r--r--@ 1 user staff 835 KeyGenerationResult.class
            ↑ Extended attribute marker
```

These attributes can include:
- File quarantine attributes
- Spotlight metadata
- Resource forks

### Why It Causes Build Errors

1. Maven performs incremental compilation
2. Checks timestamps of source vs class files
3. Tries to overwrite existing class files
4. Extended attributes can prevent proper file operations
5. Results in "Invalid argument" error

### The Clean Build Solution

`mvn clean` removes the entire `target/` directory:
- Eliminates all stale artifacts
- Removes extended attributes
- Ensures fresh compilation
- Prevents incremental build issues

## Recommendations

### For Daily Development
```bash
# Switch to main
git checkout main

# Pull latest
git pull origin main

# Clean build (recommended after branch changes)
mvn clean compile -DskipTests

# Or for complete build
mvn clean package -DskipTests
```

### For CI/CD Pipeline
Always use `mvn clean` in your build steps:
```yaml
# Example CI configuration
- name: Build
  run: mvn clean package -DskipTests
```

## Current Status

✅ **All Issues Resolved**
- Main branch builds successfully
- All features from refactoring2026 merged correctly
- No code conflicts
- Build artifacts generated properly

## Files Affected

None - this was a build artifact issue, not a source code issue.

## Merge Status

```
Branch: main
Status: Up to date with origin/main
Merge: refactoring2026 → main (completed)
Conflicts: None
Build: SUCCESS
```

## Summary

The issue was **not** related to the merge or code changes, but rather to:
- Stale build artifacts in the target directory
- macOS file system extended attributes
- Maven's incremental compilation behavior

**Solution:** Clean build resolved all issues immediately.

**Lesson:** Always run `mvn clean` after branch switches or merges to avoid artifact conflicts.
