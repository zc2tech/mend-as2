#!/bin/bash

# Startup script for AS2 Server with Web UI

echo "Starting mendelson AS2 server..."
echo ""

# Build if needed
if [ ! -f "target/mend-as2-1.0b0-dist.tar.gz" ]; then
    echo "Building project..."
    mvn clean package -DskipTests
    echo ""
fi

# Extract distribution
echo "Extracting distribution..."
rm -rf target/mend-as2-1.0b0
tar -xzf target/mend-as2-1.0b0-dist.tar.gz -C target/

# Start server
echo "Starting server..."
echo ""
java -cp "target/mend-as2-1.0b0/mend-as2-1.0b0.jar:target/mend-as2-1.0b0/lib/*" de.mendelson.comm.as2.AS2

echo ""
echo "Server stopped."
