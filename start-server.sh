#!/bin/bash

# Startup script for AS2 Server with Web UI

echo "Starting mendelson AS2 server..."
echo ""

# Build if needed
if [ ! -f "target/as2-1.1b67-dist.tar.gz" ]; then
    echo "Building project..."
    mvn clean package -DskipTests
    echo ""
fi

# Extract distribution
echo "Extracting distribution..."
rm -rf target/as2-1.1b67
tar -xzf target/as2-1.1b67-dist.tar.gz -C target/

# Start server
echo "Starting server..."
echo ""
java -cp "target/as2-1.1b67/as2-1.1b67.jar:target/as2-1.1b67/lib/*" de.mendelson.comm.as2.AS2

echo ""
echo "Server stopped."
