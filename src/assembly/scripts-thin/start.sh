#!/bin/bash
# Start mend-as2 with GUI (thin JAR version)
# This script will download dependencies from Maven Central on first run

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Create lib directory if it doesn't exist
mkdir -p "$SCRIPT_DIR/lib"

# Check if dependencies need to be downloaded
if [ ! -f "$SCRIPT_DIR/lib/.dependencies_complete" ]; then
    echo "First run detected - downloading dependencies from Maven Central..."
    echo "This may take a few minutes..."
    echo

    # Check if Maven is installed
    if ! command -v mvn &> /dev/null; then
        echo "ERROR: Maven is not installed or not in PATH!"
        echo
        echo "Please install Maven first:"
        echo "  Ubuntu/Debian: sudo apt install maven"
        echo "  macOS:         brew install maven"
        echo "  RHEL/CentOS:   sudo yum install maven"
        echo
        echo "Or download from: https://maven.apache.org/download.cgi"
        echo
        echo "Alternative: Download the fat JAR distribution instead:"
        echo "  mend-as2-dist.tar.gz (includes all dependencies)"
        echo
        exit 1
    fi

    echo "Maven found, downloading dependencies..."
    echo

    # Create temporary pom.xml for dependency download
    cat > "$SCRIPT_DIR/lib/temp-pom.xml" << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <groupId>temp</groupId>
    <artifactId>dependency-downloader</artifactId>
    <version>1.0</version>

    <dependencies>
EOF

    # Read dependencies-downloadable.txt and add each to pom
    while IFS=: read -r group_id artifact_id version; do
        cat >> "$SCRIPT_DIR/lib/temp-pom.xml" << EOF
        <dependency>
            <groupId>$group_id</groupId>
            <artifactId>$artifact_id</artifactId>
            <version>$version</version>
        </dependency>
EOF
    done < "$SCRIPT_DIR/dependencies-downloadable.txt"

    cat >> "$SCRIPT_DIR/lib/temp-pom.xml" << 'EOF'
    </dependencies>
</project>
EOF

    # Use Maven to download all dependencies from Maven Central
    if mvn dependency:copy-dependencies -f "$SCRIPT_DIR/lib/temp-pom.xml" -DoutputDirectory="$SCRIPT_DIR/lib" -DincludeScope=runtime -q; then
        echo "All dependencies downloaded successfully!"
        # Clean up temporary pom
        rm -f "$SCRIPT_DIR/lib/temp-pom.xml"
        # Create marker file
        touch "$SCRIPT_DIR/lib/.dependencies_complete"
        echo
    else
        echo "ERROR: Failed to download dependencies"
        echo "Check your internet connection and try again"
        rm -f "$SCRIPT_DIR/lib/temp-pom.xml"
        exit 1
    fi
fi

# Find the thin JAR file
THIN_JAR=$(ls "$SCRIPT_DIR"/mend-as2-*-thin.jar 2>/dev/null | head -1)

if [ -z "$THIN_JAR" ]; then
    echo "ERROR: mend-as2-*-thin.jar not found in $SCRIPT_DIR"
    exit 1
fi

# Set classpath with thin JAR and all libs
CLASSPATH="$THIN_JAR:$SCRIPT_DIR/lib/*"

# Start the application
java -cp "$CLASSPATH" de.mendelson.comm.as2.AS2 "$@"
