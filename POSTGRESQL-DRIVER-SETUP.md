# PostgreSQL JDBC Driver Setup

## The Error

```
Caused by: java.lang.RuntimeException: Unable to register database driver for PostgreSQL database - [ClassNotFoundException] org.postgresql.Driver
```

This error means the PostgreSQL JDBC driver is not in the classpath.

## Solution

### For Maven Projects (Recommended)

The PostgreSQL driver is already added to `pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.4</version>
</dependency>
```

**Build the project to download dependencies:**

```bash
# Full build
mvn clean install

# Or just resolve dependencies
mvn dependency:resolve

# Or package
mvn package
```

### For Running from IDE

**IntelliJ IDEA:**
1. Right-click on `pom.xml`
2. Select "Maven" → "Reload Project"
3. Or use the Maven toolbar: Click reload button

**Eclipse:**
1. Right-click on project
2. Select "Maven" → "Update Project"
3. Check "Force Update of Snapshots/Releases"

**VS Code:**
1. Open Command Palette (Ctrl+Shift+P / Cmd+Shift+P)
2. Type "Maven: Reload Projects"
3. Select and execute

### For Command Line Execution

If running the JAR directly, ensure the PostgreSQL driver is in the classpath:

```bash
# Option 1: Add to classpath
java -cp "target/as2-1.1b67.jar:~/.m2/repository/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar" de.mendelson.comm.as2.AS2

# Option 2: Use Maven to run
mvn exec:java -Dexec.mainClass="de.mendelson.comm.as2.AS2"
```

### Manual Download (If Maven is not available)

1. Download PostgreSQL JDBC driver:
   ```bash
   wget https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar
   ```

2. Add to project lib directory:
   ```bash
   mkdir -p lib
   mv postgresql-42.7.4.jar lib/
   ```

3. Update classpath when running:
   ```bash
   java -cp "target/as2.jar:lib/postgresql-42.7.4.jar" de.mendelson.comm.as2.AS2
   ```

### Verify Driver is Available

Check if the driver JAR is in your local Maven repository:

```bash
ls -la ~/.m2/repository/org/postgresql/postgresql/42.7.4/
```

Should show:
```
postgresql-42.7.4.jar
postgresql-42.7.4.pom
```

### For Distribution/Deployment

When creating a distribution package, ensure the PostgreSQL driver is included:

**Option 1: Maven Assembly Plugin** (Already configured)
```bash
mvn package
```
This creates `target/as2-1.1b67-distribution.zip` with all dependencies.

**Option 2: Fat JAR with Dependencies**
Add to `pom.xml`:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.5.1</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Docker Setup

If using Docker, the Dockerfile should include:

```dockerfile
FROM openjdk:11-jre-slim

# Copy application and dependencies
COPY target/as2-*.jar /app/as2.jar
COPY target/lib/ /app/lib/

# Run with classpath
CMD ["java", "-cp", "/app/as2.jar:/app/lib/*", "de.mendelson.comm.as2.AS2"]
```

## Verification

After building, verify the PostgreSQL driver is loaded:

```bash
# Extract and check JAR manifest
unzip -l target/as2-1.1b67.jar | grep postgresql

# Or check dependency tree
mvn dependency:tree | grep postgresql
```

Should show:
```
[INFO] +- org.postgresql:postgresql:jar:42.7.4:compile
```

## Alternative: Use HSQLDB Instead

If you prefer to use the embedded HSQLDB database (no external PostgreSQL needed):

In your application code, use:
```java
IDBDriverManager dbDriverManager = DBDriverManagerHSQL.instance();
IDBServer dbServer = new DBServerHSQL(...);
```

## Still Having Issues?

1. **Clean Maven cache:**
   ```bash
   rm -rf ~/.m2/repository/org/postgresql
   mvn clean install
   ```

2. **Check Java version:**
   ```bash
   java -version  # Should be Java 11 or higher
   ```

3. **Verify Maven settings:**
   ```bash
   mvn -v
   cat ~/.m2/settings.xml  # Check for proxy/mirror issues
   ```

4. **Use verbose output:**
   ```bash
   mvn -X dependency:resolve | grep postgresql
   ```

## Quick Fix Script

```bash
#!/bin/bash
# Quick fix for missing PostgreSQL driver

echo "Cleaning and rebuilding project..."
mvn clean

echo "Downloading dependencies..."
mvn dependency:resolve

echo "Building project..."
mvn package

echo "Verifying PostgreSQL driver..."
if ls ~/.m2/repository/org/postgresql/postgresql/*/postgresql-*.jar 1> /dev/null 2>&1; then
    echo "✓ PostgreSQL driver found!"
    ls -lh ~/.m2/repository/org/postgresql/postgresql/*/postgresql-*.jar
else
    echo "✗ PostgreSQL driver not found!"
    echo "Trying manual download..."
    wget -P /tmp https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar
    mkdir -p lib
    mv /tmp/postgresql-42.7.4.jar lib/
    echo "Driver downloaded to lib/ directory"
fi
```

Save as `fix-postgres-driver.sh`, make executable, and run:
```bash
chmod +x fix-postgres-driver.sh
./fix-postgres-driver.sh
```
