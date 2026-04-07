# Mend AS2 Release Distribution Guide

This document describes the release distribution structure and build process for Mend AS2.

## Release Packages

Mend AS2 provides two release distribution packages:

### 1. Full Distribution (SwingUI + WebUI)
- **File**: `mend-as2-1.1.0-full.tar.gz` / `mend-as2-1.1.0-full.zip`
- **Size**: ~120-130 MB
- **Contents**:
  - `mend-as2-1.1.0-full.jar` - Complete application with SwingUI and WebUI
  - `config/` - Configuration files (database, application settings)
  - `sqlscript/` - SQL scripts for manual database setup (optional)
  - `docs/` - Documentation (BUILD_PROFILES.md)
  - `README.md`, `INSTALL.md` - Main documentation
  - `LICENSE.gpl.txt` - License file
  - `start.sh`, `start.bat` - GUI mode startup scripts
  - `start-headless.sh`, `start-headless.bat` - Headless mode startup scripts
  - Empty directories: `log/`, `messages/`, `data/`

### 2. Headless Distribution (WebUI only)
- **File**: `mend-as2-1.1.0-headless.tar.gz` / `mend-as2-1.1.0-headless.zip`
- **Size**: ~100-110 MB
- **Contents**:
  - `mend-as2-1.1.0-headless.jar` - Application without SwingUI dependencies
  - `config/` - Configuration files (pre-configured for headless mode)
  - `sqlscript/` - SQL scripts for manual database setup (optional)
  - `docs/` - Documentation (BUILD_PROFILES.md)
  - `README.md`, `INSTALL.md` - Main documentation
  - `LICENSE.gpl.txt` - License file
  - `start-headless.sh`, `start-headless.bat` - Startup scripts
  - Empty directories: `log/`, `messages/`, `data/`

## Building Release Distributions

### Full Distribution

```bash
# Build with default (full) profile
mvn clean package

# Output files:
# - target/mend-as2-1.1.0-full.jar
# - target/mend-as2-1.1.0-full-full.tar.gz
# - target/mend-as2-1.1.0-full-full.zip
```

### Headless Distribution

```bash
# Build with headless profile
mvn clean package -Pheadless

# Output files:
# - target/mend-as2-1.1.0-headless.jar
# - target/mend-as2-1.1.0-headless-headless.tar.gz
# - target/mend-as2-1.1.0-headless-headless.zip
```

### Both Distributions

```bash
# Build both profiles
mvn clean package && mvn clean package -Pheadless

# Or use separate commands
mvn clean package -Pfull
mvn clean package -Pheadless
```

## Distribution Structure

After extraction, both distributions have this structure:

```
mend-as2-1.1.0-{full|headless}/
├── mend-as2-1.1.0-{full|headless}.jar    # Main application JAR
├── config/                                # Configuration directory
│   ├── as2.properties                     # Application settings
│   └── database-postgresql.properties     # Database connection
├── sqlscript/                             # SQL scripts (optional - auto-created)
│   ├── config/
│   │   └── CREATE.sql                     # Config database schema
│   └── runtime/
│       └── CREATE.sql                     # Runtime database schema
├── docs/                                  # Additional documentation
│   └── BUILD_PROFILES.md
├── log/                                   # Log files (created at runtime)
├── messages/                              # AS2 messages (created at runtime)
├── data/                                  # Runtime data (created at runtime)
├── start.sh                               # Linux/Mac GUI startup (full only)
├── start.bat                              # Windows GUI startup (full only)
├── start-headless.sh                      # Linux/Mac headless startup
├── start-headless.bat                     # Windows headless startup
├── README.md                              # Main documentation
├── INSTALL.md                             # Installation guide
└── LICENSE.gpl.txt                        # GPL-2.0 license
```

## Using Release Distributions

### Prerequisites

Users need to:
1. Install Java 17+ JRE (not JDK required for running)
2. Install PostgreSQL 14+ and create databases:
   ```bash
   createdb -O as2user as2_db_config
   createdb -O as2user as2_db_runtime
   ```
3. Configure `config/database-postgresql.properties`

### Startup

**Full Distribution - GUI Mode:**
```bash
# Linux/Mac
./start.sh

# Windows
start.bat
```

**Full Distribution - Headless Mode:**
```bash
# Linux/Mac
./start-headless.sh

# Windows
start-headless.bat
```

**Headless Distribution:**
```bash
# Linux/Mac
./start-headless.sh

# Windows
start-headless.bat
```

### Database Auto-Creation

The application automatically:
1. Checks if database tables exist
2. If not found, runs CREATE.sql scripts from resources
3. Creates default admin user (username: `admin`, password: `admin`)
4. Initializes version table

**No manual SQL execution required!** The included `sqlscript/` directory is for reference only.

## Startup Scripts

### start.sh / start.bat (Full Distribution Only)

```bash
#!/bin/bash
# Checks Java 17+ required
# Sets JVM options: -Xms512m -Xmx2g
# Runs: java -jar mend-as2-*-full.jar
```

Features:
- Java version check (exits if < 17)
- Automatic JAR file detection
- Default JVM memory settings
- GUI mode (shows SwingUI login)

### start-headless.sh / start-headless.bat (Both Distributions)

```bash
#!/bin/bash
# Checks Java 17+ required
# Sets JVM options: -Xms512m -Xmx2g
# Runs: java -jar mend-as2-*.jar -nogui
```

Features:
- Java version check (exits if < 17)
- Automatic JAR file detection (tries headless first, then full)
- Default JVM memory settings
- Headless mode (WebUI only)
- Prints WebUI URL: http://localhost:8080/as2/webui/

## Customization

### JVM Options

Edit startup scripts to customize memory:

```bash
# For high-volume deployments
JAVA_OPTS="-Xms1g -Xmx4g -XX:+UseG1GC"

# For low-memory systems
JAVA_OPTS="-Xms256m -Xmx1g"
```

### Configuration

Before first run, edit:

1. **config/database-postgresql.properties**
   ```properties
   db.config.driver.url=jdbc:postgresql://localhost:5432/as2_db_config
   db.config.driver.user=as2user
   db.config.driver.password=your_password
   
   db.runtime.driver.url=jdbc:postgresql://localhost:5432/as2_db_runtime
   db.runtime.driver.user=as2user
   db.runtime.driver.password=your_password
   ```

2. **config/as2.properties**
   ```properties
   as2.startup.gui.enabled=true          # Full only (false for headless)
   as2.display.mode=LIGHT                # LIGHT, DARK, HICONTRAST
   as2.startup.skip.configcheck=false    # Skip startup config check
   as2.test.mode=false                   # Use alternative ports
   ```

## Docker Deployment

Use headless distribution for Docker:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY mend-as2-1.1.0-headless.tar.gz .
RUN tar -xzf mend-as2-1.1.0-headless.tar.gz && \
    mv mend-as2-1.1.0-headless/* . && \
    rm -rf mend-as2-1.1.0-headless.tar.gz mend-as2-1.1.0-headless
EXPOSE 8080
CMD ["./start-headless.sh"]
```

## Production Recommendations

1. **Use headless distribution** for server deployments
2. **Enable HTTPS** in production (see Jetty configuration)
3. **Configure systemd/launchd** for service management (see INSTALL.md)
4. **Setup regular backups** of PostgreSQL databases
5. **Monitor log/** directory for errors
6. **Firewall rules**: Only expose port 8080 (WebUI), block 1234 (Mina)

## Assembly Configuration

The Maven assembly plugin configuration is in:
- `src/assembly/full-distribution.xml` - Full distribution descriptor
- `src/assembly/headless-distribution.xml` - Headless distribution descriptor
- `pom.xml` - Profile-specific assembly plugin executions

To modify distribution contents, edit the XML descriptors.

## Version Upgrades

When upgrading to a new version:

1. Extract new distribution
2. Copy `config/` from old installation
3. Run database migrations (automatic on startup)
4. Verify certificates and partner configurations

## Support

- **Installation Issues**: See INSTALL.md
- **Build Profiles**: See BUILD_PROFILES.md
- **General Questions**: See README.md
- **GitHub Issues**: https://github.com/zc2tech/mend-as2/issues

---

**Release Process Complete!** Users can now download and run Mend AS2 with minimal setup.
