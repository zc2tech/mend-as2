# Mend AS2 Release Guide

This guide is for **maintainers and developers** who create official releases of Mend AS2.

> **For End Users**: See [INSTALL.md](INSTALL.md) for installation instructions.  
> **For Contributors**: See [BUILD.md](md-memo/BUILD.md) for development setup.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Release Checklist](#release-checklist)
- [Building Distributions](#building-distributions)
- [Version Updates](#version-updates)
- [Testing Release](#testing-release)
- [Creating GitHub Release](#creating-github-release)
- [Post-Release Tasks](#post-release-tasks)

## Prerequisites

Before creating a release, ensure you have:

- **Java 17+** JDK installed
- **Maven 3.9+** installed
- **Node.js 18+** and npm (for WebUI build)
- **PostgreSQL 14+** (for testing)
- **Git** with write access to the repository
- **GitHub CLI** (`gh`) for creating releases (optional but recommended)

## Release Checklist

Use this checklist for every release:

- [ ] All tests passing (`mvn test`)
- [ ] Code committed and pushed to `main` branch
- [ ] Version updated in `pom.xml`
- [ ] Version updated in `AS2ServerVersion.java`
- [ ] CHANGELOG.md updated with release notes
- [ ] Documentation reviewed and updated
- [ ] Both distributions built and tested
- [ ] PostgreSQL databases tested (fresh install)
- [ ] WebUI tested in both distributions
- [ ] SwingUI tested in full distribution
- [ ] GitHub release created with artifacts
- [ ] Release announcement prepared

## Building Distributions

### 1. Clean Build Environment

```bash
# Clean previous builds
mvn clean

# Remove target directory
rm -rf target/

# Verify no local changes
git status
```

### 2. Build Full Distribution

```bash
# Build with full profile (SwingUI + WebUI)
mvn clean package -Pfull -DskipTests

# Expected output files:
# - target/mend-as2-1.1.2-full.jar (~120 MB)
# - target/mend-as2-1.1.2-full-full.tar.gz (~2.6 MB)
# - target/mend-as2-1.1.2-full-full.zip (~2.6 MB)
```

**Verify full distribution:**
```bash
# Extract and check contents
tar -tzf target/mend-as2-1.1.2-full-full.tar.gz | head -20

# Should include:
# - mend-as2-1.1.2-full.jar
# - start.sh, start-headless.sh (both modes)
# - config/, sqlscript/, docs/
# - README.md, INSTALL.md, LICENSE.gpl.txt
```

### 3. Build Headless Distribution

```bash
# Build with headless profile (WebUI only)
mvn clean package -Pheadless -DskipTests

# Expected output files:
# - target/mend-as2-1.1.2-headless.jar (~100-105 MB)
# - target/mend-as2-1.1.2-headless-headless.tar.gz (~2.6 MB)
# - target/mend-as2-1.1.2-headless-headless.zip (~2.6 MB)
```

**Verify headless distribution:**
```bash
# Extract and check contents
tar -tzf target/mend-as2-1.1.2-headless-headless.tar.gz | head -20

# Should include:
# - mend-as2-1.1.2-headless.jar
# - start-headless.sh only (no GUI scripts)
# - config/, sqlscript/, docs/
# - README.md, INSTALL.md, LICENSE.gpl.txt
```

### 4. Build Both at Once

```bash
# Clean and build both profiles
mvn clean package -Pfull -DskipTests && \
mvn package -Pheadless -DskipTests

# List all distribution files
ls -lh target/*.tar.gz target/*.zip
```

Expected files:
```
mend-as2-1.1.2-full-full.tar.gz         (~2.6 MB)
mend-as2-1.1.2-full-full.zip            (~2.6 MB)
mend-as2-1.1.2-headless-headless.tar.gz (~2.6 MB)
mend-as2-1.1.2-headless-headless.zip    (~2.6 MB)
```

## Version Updates

Before building, update version numbers in these files:

### 1. Update pom.xml

```xml
<groupId>com.zc2tech</groupId>
<artifactId>mend-as2</artifactId>
<version>1.1.2</version>  <!-- UPDATE THIS -->
<name>Mend-AS2 Server</name>
```

### 2. Update AS2ServerVersion.java

File: `src/main/java/de/mendelson/comm/as2/AS2ServerVersion.java`

```java
public static final int VERSION_MAJOR = 1;
public static final int VERSION_MINOR = 1;
public static final int VERSION_BUILD = 0;  // UPDATE THESE

// Also update database versions if schema changed:
public static final int REQUIRED_DB_VERSION_CONFIG = 6;
public static final int REQUIRED_DB_VERSION_RUNTIME = 6;
```

### 3. Update CHANGELOG.md

Add release entry at the top:

```markdown
## [1.1.2] - 2026-04-07

### Added
- New feature X
- Enhancement Y

### Fixed
- Bug fix Z

### Changed
- Updated dependency A to version B

### Security
- Security improvement C
```

### 4. Commit Version Changes

```bash
git add pom.xml src/main/java/de/mendelson/comm/as2/AS2ServerVersion.java CHANGELOG.md
git commit -m "Bump version to 1.1.2"
git push origin main
```

### 5. Create Git Tag

```bash
# Create annotated tag
git tag -a v1.1.2 -m "Release version 1.1.2"

# Push tag to remote
git push origin v1.1.2
```

## Testing Release

Before publishing, test both distributions:

### Test Full Distribution

```bash
# Extract
tar -xzf target/mend-as2-1.1.2-full-full.tar.gz
cd mend-as2-1.1.2-full

# Configure database
cp config/database-postgresql.properties.example config/database-postgresql.properties
nano config/database-postgresql.properties

# Test GUI mode
./start.sh
# Verify: SwingUI login appears, can login with admin/admin

# Test headless mode
./start-headless.sh
# Verify: http://localhost:8080/as2/webui/ accessible

# Test database auto-creation
# Verify: Tables created automatically on first run
# Verify: Default admin user created
```

### Test Headless Distribution

```bash
# Extract
tar -xzf target/mend-as2-1.1.2-headless-headless.tar.gz
cd mend-as2-1.1.2-headless

# Configure database
nano config/database-postgresql.properties

# Start server
./start-headless.sh

# Test WebUI
# Open: http://localhost:8080/as2/webui/
# Login: admin / admin
# Verify: Forced password change works
# Verify: Can access all sections
```

### Test Checklist

For each distribution:

- [ ] Startup script runs without errors
- [ ] Java version check works (try with Java 11 - should fail)
- [ ] Database auto-creation works
- [ ] Default admin user created
- [ ] WebUI accessible at port 8080
- [ ] Login with admin/admin works
- [ ] Forced password change works
- [ ] Can create partner
- [ ] Can import certificate
- [ ] Can send test message (if partners configured)
- [ ] SwingUI works (full distribution only)
- [ ] Logs written to log/ directory
- [ ] No errors in log/as2.log

## Creating GitHub Release

### Option 1: Using GitHub Web Interface

1. Go to https://github.com/zc2tech/mend-as2/releases
2. Click **"Draft a new release"**
3. Choose tag: `v1.1.2`
4. Release title: `Mend AS2 v1.1.2`
5. Description: Copy from CHANGELOG.md
6. Upload artifacts:
   - `mend-as2-1.1.2-full-full.tar.gz`
   - `mend-as2-1.1.2-full-full.zip`
   - `mend-as2-1.1.2-headless-headless.tar.gz`
   - `mend-as2-1.1.2-headless-headless.zip`
7. Check **"Set as the latest release"**
8. Click **"Publish release"**

### Option 2: Using GitHub CLI

```bash
# Install gh CLI if needed
# brew install gh  (macOS)
# See: https://cli.github.com/

# Authenticate
gh auth login

# Create release with artifacts
gh release create v1.1.2 \
  target/mend-as2-1.1.2-full-full.tar.gz \
  target/mend-as2-1.1.2-full-full.zip \
  target/mend-as2-1.1.2-headless-headless.tar.gz \
  target/mend-as2-1.1.2-headless-headless.zip \
  --title "Mend AS2 v1.1.2" \
  --notes-file CHANGELOG.md \
  --latest

# Verify release created
gh release view v1.1.2
```

### Release Description Template

```markdown
# Mend AS2 v1.1.2

## 🚀 What's New

[Highlight major features/changes]

## 📦 Downloads

Choose the distribution that fits your needs:

### Full Distribution (SwingUI + WebUI)
- **Recommended for**: Desktop workstations, development environments
- **Size**: ~120 MB (JAR), ~2.6 MB (archive)
- **Includes**: Desktop client (SwingUI) + Web interface

**Download:**
- [mend-as2-1.1.2-full-full.tar.gz](URL) (Linux/macOS)
- [mend-as2-1.1.2-full-full.zip](URL) (Windows)

### Headless Distribution (WebUI only)
- **Recommended for**: Servers, containers, cloud deployments
- **Size**: ~100 MB (JAR), ~2.6 MB (archive)
- **Includes**: Web interface only, smaller size, better security

**Download:**
- [mend-as2-1.1.2-headless-headless.tar.gz](URL) (Linux/macOS)
- [mend-as2-1.1.2-headless-headless.zip](URL) (Windows)

## 📋 Requirements

- Java 17 or higher
- PostgreSQL 14 or higher
- 2GB+ RAM (4GB+ recommended)

## 📖 Installation

See [INSTALL.md](INSTALL.md) for complete installation instructions.

**Quick start:**
```bash
# Extract distribution
tar -xzf mend-as2-1.1.2-headless.tar.gz
cd mend-as2-1.1.2-headless

# Configure database
nano config/database-postgresql.properties

# Start server
./start-headless.sh

# Open browser: http://localhost:8080/as2/webui/
# Login: admin / admin
```

## 📝 Changelog

[Copy from CHANGELOG.md]

## 🐛 Known Issues

[List any known issues]

## 📚 Documentation

- [Installation Guide](INSTALL.md)
- [User Manual](README.md)
- [Build Guide](md-memo/BUILD.md)
- [Build Profiles](md-memo/BUILD_PROFILES.md)

## 🤝 Support

- Report issues: https://github.com/zc2tech/mend-as2/issues
- Email: julian.xu@aliyun.com

---

**Full Changelog**: https://github.com/zc2tech/mend-as2/compare/v1.0.0...v1.1.2
```

## Post-Release Tasks

After publishing the release:

### 1. Verify Release Artifacts

```bash
# Download and verify each artifact
wget https://github.com/zc2tech/mend-as2/releases/download/v1.1.2/mend-as2-1.1.2-full-full.tar.gz
tar -tzf mend-as2-1.1.2-full-full.tar.gz | head -20

wget https://github.com/zc2tech/mend-as2/releases/download/v1.1.2/mend-as2-1.1.2-headless-headless.tar.gz
tar -tzf mend-as2-1.1.2-headless-headless.tar.gz | head -20
```

### 2. Update README.md

Update badges and links:

```markdown
![Release](https://img.shields.io/github/v/release/zc2tech/mend-as2)
![Downloads](https://img.shields.io/github/downloads/zc2tech/mend-as2/total)
```

### 3. Announce Release

- Update repository README with release link
- Post announcement in discussions/community forums
- Send email to users mailing list (if applicable)
- Update documentation site (if applicable)

### 4. Prepare Next Development Cycle

```bash
# Bump to next development version
# Update pom.xml to 1.2.0-SNAPSHOT
git checkout -b prepare-v1.2.0
nano pom.xml  # Change <version>1.1.2</version> to <version>1.2.0-SNAPSHOT</version>
git add pom.xml
git commit -m "Prepare for v1.2.0 development"
git push origin prepare-v1.2.0

# Create PR for version bump
gh pr create --title "Prepare for v1.2.0 development" --body "Bump version to 1.2.0-SNAPSHOT"
```

## Hotfix Releases

For critical bug fixes between major releases:

### 1. Create Hotfix Branch

```bash
# Branch from release tag
git checkout v1.1.2
git checkout -b hotfix-v1.1.1

# Apply fix
git commit -m "Fix critical bug X"

# Bump version to 1.1.1
nano pom.xml
nano src/main/java/de/mendelson/comm/as2/AS2ServerVersion.java
git commit -m "Bump version to 1.1.1"

# Push hotfix branch
git push origin hotfix-v1.1.1
```

### 2. Build and Test Hotfix

```bash
# Build both distributions
mvn clean package -Pfull -DskipTests
mvn package -Pheadless -DskipTests

# Test thoroughly
# ...
```

### 3. Release Hotfix

```bash
# Tag hotfix
git tag -a v1.1.1 -m "Hotfix release 1.1.1"
git push origin v1.1.1

# Create GitHub release (follow same process)
gh release create v1.1.1 ...

# Merge back to main
git checkout main
git merge hotfix-v1.1.1
git push origin main
```

## Release Automation (Future)

Consider automating releases with GitHub Actions:

```yaml
# .github/workflows/release.yml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  build-and-release:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
      - name: Build distributions
        run: |
          mvn clean package -Pfull -DskipTests
          mvn package -Pheadless -DskipTests
      - name: Create GitHub Release
        uses: softprops/action-gh-release@v1
        with:
          files: |
            target/mend-as2-*-full.tar.gz
            target/mend-as2-*-full.zip
            target/mend-as2-*-headless.tar.gz
            target/mend-as2-*-headless.zip
```

## Troubleshooting

### Build Fails

**Issue**: Maven build fails with compilation errors

**Solution:**
1. Check Java version: `java -version` (must be 17+)
2. Clean build cache: `mvn clean`
3. Update dependencies: `mvn dependency:purge-local-repository`
4. Check for local changes: `git status`

### Distribution Files Too Large

**Issue**: Distribution archives are larger than expected

**Solution:**
1. Verify no extra files in target/: `ls -lh target/`
2. Check assembly descriptors don't include unnecessary files
3. Ensure `-DskipTests` is used (test files not included)

### WebUI Not Included

**Issue**: WebUI files missing from distribution

**Solution:**
1. Build WebUI first: `cd src/main/webapp/admin && npm run build`
2. Or use full Maven build: `mvn package` (includes frontend build)
3. Verify `target/classes/webapp/admin/` contains built WebUI

### GitHub Release Upload Fails

**Issue**: Cannot upload large files to GitHub

**Solution:**
1. Files over 2GB not supported - verify sizes
2. Use `gh` CLI instead of web interface
3. Compress archives better: `tar -czf` (already used)
4. Split into multiple archives if needed

## Support

Questions about the release process?

- **Documentation**: This file and [BUILD.md](md-memo/BUILD.md)
- **Issues**: [GitHub Issues](https://github.com/zc2tech/mend-as2/issues)
- **Email**: julian.xu@aliyun.com

---

**Happy releasing!** 🚀
