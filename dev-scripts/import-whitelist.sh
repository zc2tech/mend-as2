#!/bin/bash

# Import IP CIDR whitelist from JSON file to Global Whitelist
# This script is a wrapper around the Java import utility
# Usage: ./import-whitelist.sh <json_file> [target_type]
#   json_file: Path to JSON file with CIDR entries
#   target_type: AS2|TRACKER|WEBUI|API (default: ALL - imports for all types)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check arguments
if [ $# -lt 1 ]; then
    echo "Usage: $0 <json_file> [target_type]"
    echo ""
    echo "Arguments:"
    echo "  json_file    Path to JSON file with CIDR entries"
    echo "  target_type  AS2|TRACKER|WEBUI|API|ALL (default: ALL)"
    echo ""
    echo "Example:"
    echo "  $0 ../private/public_ip_cidr.json"
    echo "  $0 ../private/public_ip_cidr.json AS2"
    exit 1
fi

JSON_FILE="$1"
TARGET_TYPE="${2:-ALL}"

# Convert JSON file path to absolute path (before we change directory)
if [ ! -f "$JSON_FILE" ]; then
    echo -e "${RED}Error: JSON file not found: $JSON_FILE${NC}"
    exit 1
fi
JSON_FILE=$(cd "$(dirname "$JSON_FILE")" && pwd)/$(basename "$JSON_FILE")

# Find the JAR file - search in multiple locations
# Priority:
# 1. Parent directory (release structure: mend-as2-x.x.x/)
# 2. Parent/target directory (Maven build output for development)
JAR_FILE=""

# Check in parent directory first (most common for releases)
if [ -f "$PROJECT_ROOT/mend-as2.jar" ]; then
    JAR_FILE="$PROJECT_ROOT/mend-as2.jar"
else
    # Try to find versioned JAR in parent directory (e.g., mend-as2-1.1.0.jar)
    JAR_FILE=$(find "$PROJECT_ROOT" -maxdepth 1 -name "mend-as2*.jar" -type f ! -name "*sources*" ! -name "*javadoc*" | head -1)
fi

# If not found, try target directory (development)
if [ -z "$JAR_FILE" ] || [ ! -f "$JAR_FILE" ]; then
    if [ -f "$PROJECT_ROOT/target/mend-as2.jar" ]; then
        JAR_FILE="$PROJECT_ROOT/target/mend-as2.jar"
    else
        JAR_FILE=$(find "$PROJECT_ROOT/target" -maxdepth 1 -name "mend-as2*.jar" -type f ! -name "*sources*" ! -name "*javadoc*" 2>/dev/null | head -1)
    fi
fi

if [ -z "$JAR_FILE" ] || [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}Error: Could not find mend-as2.jar${NC}"
    echo "Please ensure the JAR file exists in one of these locations:"
    echo "  - $PROJECT_ROOT/mend-as2.jar (release)"
    echo "  - $PROJECT_ROOT/mend-as2-x.x.x.jar (versioned release)"
    echo "  - $PROJECT_ROOT/target/mend-as2-x.x.x.jar (development build)"
    exit 1
fi

echo -e "${GREEN}Using JAR: $JAR_FILE${NC}"
echo ""

# Build classpath
# For development (thin JAR), need to include Maven dependencies
# For release (fat JAR), JAR is self-contained
CLASSPATH="$JAR_FILE"

# Check if this is a thin JAR (development) - add Maven dependencies
JAR_SIZE=$(stat -f%z "$JAR_FILE" 2>/dev/null || stat -c%s "$JAR_FILE" 2>/dev/null)
if [ "$JAR_SIZE" -lt 50000000 ]; then
    # Thin JAR detected (< 50MB), need dependencies
    echo -e "${YELLOW}Thin JAR detected - checking for dependencies...${NC}"

    # Check if dependencies are already copied
    if [ ! -d "$PROJECT_ROOT/target/dependency" ]; then
        echo "Copying Maven dependencies (one-time setup)..."
        cd "$PROJECT_ROOT"
        mvn dependency:copy-dependencies -DoutputDirectory=target/dependency -Dsilent=true >/dev/null 2>&1
        if [ $? -ne 0 ]; then
            echo -e "${RED}Warning: Failed to copy dependencies. Trying Maven repository...${NC}"
        fi
        cd "$SCRIPT_DIR"
    fi

    if [ -d "$PROJECT_ROOT/target/dependency" ]; then
        # Maven dependency:copy-dependencies output
        CLASSPATH="$CLASSPATH:$PROJECT_ROOT/target/dependency/*"
        echo -e "${GREEN}Using dependencies from target/dependency${NC}"
    elif [ -d "$HOME/.m2/repository" ]; then
        # Fallback: Add common Maven dependencies directly
        echo -e "${YELLOW}Using dependencies from Maven repository${NC}"
        CLASSPATH="$CLASSPATH:$HOME/.m2/repository/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar"
        CLASSPATH="$CLASSPATH:$HOME/.m2/repository/com/mysql/mysql-connector-j/9.0.0/mysql-connector-j-9.0.0.jar"
        CLASSPATH="$CLASSPATH:$HOME/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.17.2/jackson-databind-2.17.2.jar"
        CLASSPATH="$CLASSPATH:$HOME/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.17.2/jackson-core-2.17.2.jar"
        CLASSPATH="$CLASSPATH:$HOME/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.17.2/jackson-annotations-2.17.2.jar"
    else
        echo -e "${RED}Error: Dependencies not found. Please run: mvn package${NC}"
        exit 1
    fi
    echo ""
fi

# Change to project root directory so Java can find config folder
cd "$PROJECT_ROOT"

# Run Java import utility (now running from project root)
# Suppress SLF4J warnings
java -Dorg.slf4j.simpleLogger.defaultLogLevel=off -cp "$CLASSPATH" de.mendelson.comm.as2.tools.IPWhitelistImporter "$JSON_FILE" "$TARGET_TYPE"
