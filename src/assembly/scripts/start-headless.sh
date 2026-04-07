#!/bin/bash

# Mend AS2 Server Startup Script (Headless Mode)
# Java 17+ required

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Check Java version
java_version=$(java -version 2>&1 | grep -i version | awk -F'"' '{print $2}' | awk -F'.' '{print $1}')
if [ "$java_version" -lt 17 ]; then
    echo "ERROR: Java 17 or higher is required. Current version: $java_version"
    exit 1
fi

# Set JVM options
JAVA_OPTS="-Xms512m -Xmx2g"

echo "Starting Mend AS2 Server (Headless Mode)..."
echo "Java Version: $(java -version 2>&1 | head -1)"
echo "WebUI will be available at: http://localhost:8080/as2/webui/"
echo ""

# Find the JAR file (try both headless and full)
JAR_FILE=$(ls mend-as2-*-headless.jar 2>/dev/null | head -1)
if [ -z "$JAR_FILE" ]; then
    JAR_FILE=$(ls mend-as2-*-full.jar 2>/dev/null | head -1)
fi

if [ -z "$JAR_FILE" ]; then
    echo "ERROR: mend-as2 JAR file not found"
    exit 1
fi

# Run the server in headless mode
exec java $JAVA_OPTS -jar "$JAR_FILE" -nogui "$@"
