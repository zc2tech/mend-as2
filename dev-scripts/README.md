# Development Scripts

This folder contains utility scripts for development and administrative tasks.

## Available Scripts

### 🚀 Release Preparation

**Purpose**: Update version numbers across all documentation and source files for new releases.

**Features**:
- 🎯 **Automatic version detection** - Reads current version from pom.xml
- 🔄 **Multi-file updates** - Updates pom.xml, AS2ServerVersion.java, and RELEASE.md
- 📋 **Validation** - Checks version format (X.Y.Z)
- 🛡️ **Safe operation** - Shows changes before confirming
- 📝 **Post-update guidance** - Shows next steps after version update

**Files**:
- `prepare-release.sh` - Linux/Mac version
- `prepare-release.bat` - Windows version

**Quick Start**:
```bash
# Linux/Mac
./dev-scripts/prepare-release.sh 1.2.0

# Windows
dev-scripts\prepare-release.bat 1.2.0

# Review changes
git diff

# Commit and tag
git add .
git commit -m "Bump version to 1.2.0"
git tag -a v1.2.0 -m "Release version 1.2.0"
git push origin main && git push origin v1.2.0
```

**How It Works**:
- Extracts current version from `pom.xml`
- Updates version in:
  - `pom.xml` - Maven project version
  - `src/main/java/de/mendelson/comm/as2/AS2ServerVersion.java` - Java constants
  - `RELEASE.md` - Version-specific build examples
- Shows post-update steps (commit, tag, build, release)

**Important**: This script only updates version numbers in files that NEED version-specific references. Most documentation (README.md, INSTALL.md) has been refactored to be version-agnostic.

📖 See [RELEASE.md](../RELEASE.md) for full release process.

---

### 🔐 Admin Password Reset

**Purpose**: Reset the admin user's password when locked out of the system.

**Features**:
- 🎯 **Smart environment detection** - Automatically works in development or production
- 🔄 **Multi-database support** - MySQL, PostgreSQL, H2
- 📦 **Complete classpath handling** - Includes all dependencies automatically
- 🛡️ **Standalone tool** - No heavy dependencies (no AS2Server or BouncyCastle required)
- 🚀 **Production ready** - Works in deployed installations

**Files**:
- `reset-admin-password.sh` - Linux/Mac version
- `reset-admin-password.bat` - Windows version
- `ADMIN_PASSWORD_RESET.md` - Complete documentation

**Quick Start**:
```bash
# Stop the server first!

# Linux/Mac
./dev-scripts/reset-admin-password.sh

# Windows
dev-scripts\reset-admin-password.bat
```

**How It Works**:
- **Development**: Uses Maven to compile and build full classpath with all dependencies
- **Production**: Uses deployed JAR and lib/*.jar dependencies directly
- **Auto-detection**: Reads database type from `config/as2.properties`

**Important**: Server must be stopped before running this tool.

📖 See [ADMIN_PASSWORD_RESET.md](./ADMIN_PASSWORD_RESET.md) for full documentation.

---

### 💾 Database Backup & Restore

**Purpose**: Backup and restore AS2 configuration database.

**Files**:
- `backup.sh` / `backup.bat` - Create database backups
- `restore.sh` / `restore.bat` - Restore from backups
- `BACKUP_RESTORE_IMPLEMENTATION.md` - Documentation

---

### 🌐 IP Whitelist Import

**Purpose**: Import IP whitelist configurations.

**Files**:
- `import-whitelist.sh` / `import-whitelist.bat` - Import whitelist data
- `IMPORT_WHITELIST.md` - Documentation

---

## Adding New Scripts

When adding new development scripts to this folder:

1. Place the script files here (`*.sh` for Unix, `*.bat` for Windows)
2. Make Unix scripts executable: `chmod +x script-name.sh`
3. Scripts should auto-detect project root: `cd "$(dirname "$0")/.."`
4. Handle both development and production environments when applicable
5. Add documentation in this README
6. Consider creating a detailed `.md` file for complex scripts

## Guidelines

### Script Structure
- ✅ Scripts should work when run from any directory
- ✅ Auto-detect environment (development vs production)
- ✅ Build proper classpaths with all dependencies
- ✅ Include clear error messages
- ✅ Show progress indicators for long operations
- ✅ Validate prerequisites before executing

### Naming & Documentation
- ✅ Use meaningful names: `verb-noun.sh` (e.g., `reset-admin-password.sh`)
- ✅ Add usage instructions at the top of each script
- ✅ Document prerequisites and side effects
- ✅ List supported databases/configurations

### Environment Detection Pattern
```bash
# Detect environment
if [ -f "pom.xml" ]; then
    ENV_TYPE="development"
    # Use Maven: compile + dependency:build-classpath
else
    ENV_TYPE="production"
    # Use JAR files: find main.jar + lib/*.jar
fi
```

### Classpath Building (Development)
```bash
# Get all Maven dependencies
MAVEN_CP=$(mvn dependency:build-classpath -DincludeScope=runtime 2>&1 | \
           grep -v "^\[INFO\]" | grep -v "^\[WARNING\]" | \
           grep "\.jar" | tail -1)

# Build full classpath
CLASSPATH="target/classes:$MAVEN_CP"
```

### Classpath Building (Production)
```bash
# Find main JAR
MAIN_JAR=$(ls mend-as2-*.jar | head -1)

# Build classpath with all lib JARs
CLASSPATH="$MAIN_JAR"
for jar in lib/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
done
```

---

## Troubleshooting

### Script fails to build classpath
**Solution**: Ensure Maven is installed and `mvn --version` works

### "Database driver not found" error
**Solution**: 
- Development: Run `mvn dependency:build-classpath` to verify dependencies
- Production: Check that `lib/` folder contains database driver JARs

### "Class not found" errors
**Solution**: The classpath is incomplete. Check that:
- Development: `mvn compile` succeeded
- Production: All JARs from `lib/` are included

### Script works in dev but not production
**Solution**: Test the JAR finding logic:
```bash
cd /path/to/installation
ls mend-as2-*.jar  # Should find the main JAR
ls lib/*.jar       # Should list all dependencies
```

