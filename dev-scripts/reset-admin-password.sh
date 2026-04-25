#!/bin/bash

# Admin Password Reset Tool
# Use this script to reset the admin user's password

echo "==================================================="
echo "  AS2 Admin Password Reset Tool"
echo "==================================================="
echo ""

# Get the project root directory (parent of dev-scripts)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Change to project root
cd "$PROJECT_ROOT"

# Detect environment (development vs production)
if [ -f "pom.xml" ]; then
    # Development environment - use Maven
    ENV_TYPE="development"
    echo "Environment: Development (Maven available)"
else
    # Production environment - use JAR
    ENV_TYPE="production"
    echo "Environment: Production (deployed)"

    # Find the main JAR file
    MAIN_JAR=$(ls mend-as2-*.jar 2>/dev/null | head -1)
    if [ -z "$MAIN_JAR" ]; then
        echo "ERROR: Cannot find mend-as2-*.jar in current directory"
        echo "Current directory: $PROJECT_ROOT"
        echo "Please run this script from the AS2 installation directory."
        exit 1
    fi
    echo "Found JAR: $MAIN_JAR"
fi

echo ""

# Check if server is running
SERVER_PID=$(ps aux | grep "[m]end-as2" | grep -v grep | awk '{print $2}')
if [ ! -z "$SERVER_PID" ]; then
    echo "WARNING: AS2 server appears to be running (PID: $SERVER_PID)"
    echo "You should stop the server before resetting the password."
    echo ""
    read -p "Stop the server now? (yes/no): " STOP_SERVER
    if [ "$STOP_SERVER" = "yes" ] || [ "$STOP_SERVER" = "y" ]; then
        echo "Stopping server..."
        kill $SERVER_PID
        sleep 2
        echo "Server stopped."
    else
        echo "Please stop the server manually and run this script again."
        exit 1
    fi
fi

echo ""
echo "Starting password reset tool..."
echo ""

# Run the password reset tool based on environment
if [ "$ENV_TYPE" = "development" ]; then
    # Development: Compile first, then run with java -cp
    echo "Compiling project..."
    mvn compile -q -DskipTests

    if [ $? -ne 0 ]; then
        echo "ERROR: Maven compilation failed"
        exit 1
    fi

    echo "Building classpath..."
    # Get Maven's full classpath including all dependencies
    # Filter out Maven INFO/WARNING messages, keep only the classpath line
    MAVEN_CP=$(mvn dependency:build-classpath -DincludeScope=runtime 2>&1 | grep -v "^\[INFO\]" | grep -v "^\[WARNING\]" | grep "\.jar" | tail -1)

    if [ -z "$MAVEN_CP" ]; then
        echo "ERROR: Failed to build Maven classpath"
        exit 1
    fi

    # Build full classpath: compiled classes + Maven dependencies
    CLASSPATH="target/classes:$MAVEN_CP"

    echo "Running password reset tool..."
    echo ""

    # Run the tool
    java -cp "$CLASSPATH" de.mendelson.comm.as2.usermanagement.AdminPasswordResetTool
else
    # Production: Use java -jar with classpath
    # Build classpath including main JAR and all lib/*.jar files
    CLASSPATH="$MAIN_JAR"
    if [ -d "lib" ]; then
        for jar in lib/*.jar; do
            CLASSPATH="$CLASSPATH:$jar"
        done
    fi

    # Run the tool
    java -cp "$CLASSPATH" de.mendelson.comm.as2.usermanagement.AdminPasswordResetTool
fi

echo ""
echo "Done."
