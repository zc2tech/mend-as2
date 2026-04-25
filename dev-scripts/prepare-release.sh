#!/bin/bash

# Release Preparation Script
# Updates version numbers across all documentation and source files

set -e

echo "==================================================="
echo "  Mend AS2 Release Preparation Tool"
echo "==================================================="
echo ""

# Get the project root directory (parent of dev-scripts)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Change to project root
cd "$PROJECT_ROOT"

# Check if version argument provided
if [ -z "$1" ]; then
    echo "Usage: $0 <new-version>"
    echo ""
    echo "Example:"
    echo "  $0 1.2.0"
    echo ""
    echo "This will update version numbers in:"
    echo "  - pom.xml"
    echo "  - AS2ServerVersion.java"
    echo "  - RELEASE.md (version-specific examples only)"
    echo ""
    exit 1
fi

NEW_VERSION="$1"

# Validate version format (X.Y.Z)
if ! echo "$NEW_VERSION" | grep -qE '^[0-9]+\.[0-9]+\.[0-9]+$'; then
    echo "ERROR: Invalid version format. Expected X.Y.Z (e.g., 1.2.0)"
    exit 1
fi

# Extract current version from pom.xml
CURRENT_VERSION=$(grep -m 1 '<version>' pom.xml | sed 's/.*<version>\(.*\)<\/version>.*/\1/')

echo "Current version: $CURRENT_VERSION"
echo "New version:     $NEW_VERSION"
echo ""

if [ "$CURRENT_VERSION" = "$NEW_VERSION" ]; then
    echo "WARNING: New version is same as current version"
fi

read -p "Continue with version update? (yes/no): " CONFIRM
if [ "$CONFIRM" != "yes" ] && [ "$CONFIRM" != "y" ]; then
    echo "Operation cancelled."
    exit 0
fi

echo ""
echo "Updating version numbers..."
echo ""

# 1. Update pom.xml
echo "1. Updating pom.xml..."
sed -i.bak "s|<version>${CURRENT_VERSION}</version>|<version>${NEW_VERSION}</version>|" pom.xml
rm -f pom.xml.bak

# 2. Update AS2ServerVersion.java
echo "2. Updating AS2ServerVersion.java..."
VERSION_FILE="src/main/java/de/mendelson/comm/as2/AS2ServerVersion.java"

# Extract version components
MAJOR=$(echo "$NEW_VERSION" | cut -d. -f1)
MINOR=$(echo "$NEW_VERSION" | cut -d. -f2)
BUILD=$(echo "$NEW_VERSION" | cut -d. -f3)

# Update version constants
sed -i.bak "s/VERSION_MAJOR = [0-9]\+;/VERSION_MAJOR = ${MAJOR};/" "$VERSION_FILE"
sed -i.bak "s/VERSION_MINOR = [0-9]\+;/VERSION_MINOR = ${MINOR};/" "$VERSION_FILE"
sed -i.bak "s/VERSION_BUILD = [0-9]\+;/VERSION_BUILD = ${BUILD};/" "$VERSION_FILE"
rm -f "${VERSION_FILE}.bak"

# 3. Update RELEASE.md (only version-specific examples)
echo "3. Updating RELEASE.md..."
sed -i.bak "s/${CURRENT_VERSION}/${NEW_VERSION}/g" RELEASE.md
rm -f RELEASE.md.bak

echo ""
echo "==================================================="
echo "  Version Update Complete!"
echo "==================================================="
echo ""
echo "Updated files:"
echo "  ✓ pom.xml"
echo "  ✓ src/main/java/de/mendelson/comm/as2/AS2ServerVersion.java"
echo "  ✓ RELEASE.md"
echo ""
echo "Note: Start scripts are already version-agnostic (use wildcards)"
echo ""
echo "Next steps:"
echo "  1. Update CHANGELOG.md with release notes"
echo "  2. Review changes: git diff"
echo "  3. Commit changes: git add . && git commit -m \"Bump version to ${NEW_VERSION}\""
echo "  4. Create tag: git tag -a v${NEW_VERSION} -m \"Release version ${NEW_VERSION}\""
echo "  5. Push: git push origin main && git push origin v${NEW_VERSION}"
echo "  6. Build distributions: mvn clean package -Pfull && mvn package -Pheadless"
echo "  7. Create GitHub release with artifacts"
echo ""
