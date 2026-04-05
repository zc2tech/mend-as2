#!/bin/bash
# AS2 Server Startup Script

cd "$(dirname "$0")"

# Set Java options
JAVA_OPTS="-Xmx1024m -Xms256m"

# Build classpath
CLASSPATH="target/mend-as2-1.1.0.jar"
for jar in target/lib/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
done

# Start the AS2 server
java $JAVA_OPTS -cp "$CLASSPATH" de.mendelson.comm.as2.AS2 "$@"
