# Mend AS2 Installation Guide

Complete installation instructions for Mend AS2 Server. This guide is for **end users** who want to install and run the AS2 server.

> **For Developers**: If you want to build from source or create releases, see [BUILD.md](md-memo/BUILD.md)

## Table of Contents

- [Download](#download)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Detailed Installation](#detailed-installation)
  - [PostgreSQL Setup](#postgresql-setup)
  - [Extract Distribution](#extract-distribution)
  - [Configure Database](#configure-database)
  - [Choose Your Mode](#choose-your-mode)
  - [First Run](#first-run)
- [Running the Server](#running-the-server)
- [Running as Background Service](#running-as-background-service)
- [Docker Deployment](#docker-deployment)
- [Platform-Specific Notes](#platform-specific-notes)
- [Troubleshooting](#troubleshooting)
- [Next Steps](#next-steps)

## Download

Download the latest release from [GitHub Releases](https://github.com/zc2tech/mend-as2/releases):

### Release Types

**1. Fat JAR Distribution** (`mend-as2-1.1.0-dist.tar.gz` or `.zip`) - **~67 MB**

Single executable JAR with all dependencies included:
- ✅ Simplest to deploy (one JAR file)
- ✅ No classpath issues
- ✅ Ideal for containers/Docker
- ✅ Works offline after download
- ❌ Harder to update individual libraries
- ❌ Larger single file

**2. Thin JAR Distribution** (`mend-as2-1.1.0-thin-dist.tar.gz` or `.zip`) - **~2.6 MB + downloads**

Small distribution that downloads dependencies from Maven Central on first run:
- ✅ Very small download (2.6 MB vs 67 MB)
- ✅ Fast download and extraction
- ✅ Dependencies downloaded from trusted Maven Central
- ✅ Can update individual libraries
- ✅ Uses Maven's reliable dependency resolution
- ❌ Requires Maven to be installed on first run
- ❌ Requires internet connection on first run
- ❌ First startup takes longer (downloads ~110 dependencies)

**Both distributions support:**
- **GUI Mode**: SwingUI (desktop client) + WebUI
- **Headless Mode**: WebUI only (for servers/containers)

Choose your mode at startup - no separate downloads needed!

## Prerequisites

### Required Software

**Java 17 or higher** (JRE is sufficient for running)

Choose any OpenJDK distribution:
- **Eclipse Temurin** (Adoptium): [https://adoptium.net/](https://adoptium.net/)
- **SAP Machine**: [https://sap.github.io/SapMachine/](https://sap.github.io/SapMachine/)
- **Microsoft Build of OpenJDK**: [https://www.microsoft.com/openjdk](https://www.microsoft.com/openjdk)
- **Amazon Corretto**: [https://aws.amazon.com/corretto/](https://aws.amazon.com/corretto/)
- **Azul Zulu**: [https://www.azul.com/downloads/](https://www.azul.com/downloads/)
- **Red Hat OpenJDK**: [https://developers.redhat.com/products/openjdk/download](https://developers.redhat.com/products/openjdk/download)

Verify installation: `java -version` (should show version 17 or higher)

**PostgreSQL 14 or higher**
- Download: [PostgreSQL](https://www.postgresql.org/download/)
- Version 15+ recommended for longer support lifecycle
- Version 14 works fine and is supported until November 2026

**Maven** (only required for thin JAR distribution)
- Download: [Apache Maven](https://maven.apache.org/download.cgi)
- Required to download dependencies on first run of thin JAR
- Not needed if using fat JAR distribution
- Installation:
  - **Linux/Mac**: `brew install maven` or `sudo apt install maven`
  - **Windows**: Download ZIP, extract, add `bin\` to PATH
- Verify: `mvn --version` (should show Maven 3.6+)

### System Requirements

- **RAM**: Minimum 2GB, Recommended 4GB+
- **Disk**: Minimum 500MB for application, 10GB+ for data/logs
- **Network**: Port 8080 (WebUI/REST API)
- **Optional**: Port 1234 (SwingUI - localhost only, full distribution)

## Quick Start

For experienced users, here's the fastest path:

```bash
# 1. Install Java 17+ and PostgreSQL 14+

# 2. Create databases
createdb -O as2user as2_db_config
createdb -O as2user as2_db_runtime

# 3. Extract distribution (choose one):

# Option A: Fat JAR (single executable)
tar -xzf mend-as2-1.1.0-dist.tar.gz
cd mend-as2-1.1.0

# Option B: Thin JAR (downloads dependencies on first run)
tar -xzf mend-as2-1.1.0-thin-dist.tar.gz
cd mend-as2-1.1.0-thin

# 4. Edit database connection
nano config/database-postgresql.properties
# Set your database credentials

# 5. Choose your mode and start:

# Fat JAR:
./start.sh              # GUI Mode
./start-headless.sh     # Headless Mode

# Thin JAR (first run will download ~110 dependencies from Maven Central):
./start.sh              # GUI Mode
./start-headless.sh     # Headless Mode
# First run output: "Downloading dependencies from Maven Central..."
# Subsequent runs start immediately (dependencies cached in lib/)

# 6. Open browser
# http://localhost:8080/as2/webui/
# Login: admin / admin (forced password change on first login)
```

## Detailed Installation

### 1. PostgreSQL Setup

#### Install PostgreSQL

```bash
# macOS (Homebrew)
brew install postgresql@15
brew services start postgresql@15

# Ubuntu/Debian
sudo apt install postgresql-15
sudo systemctl start postgresql
sudo systemctl enable postgresql

# RHEL/CentOS
sudo yum install postgresql15-server
sudo systemctl start postgresql-15
sudo systemctl enable postgresql-15
```

#### Create Database User

```bash
# Switch to postgres user
sudo -u postgres psql

# In PostgreSQL shell:
CREATE USER as2user WITH PASSWORD 'your_secure_password';
ALTER USER as2user CREATEDB;
\q
```

#### Create Databases

**Option 1: Using createdb command (Recommended)**

```bash
# Create config database
sudo -u postgres createdb -O as2user as2_db_config

# Create runtime database
sudo -u postgres createdb -O as2user as2_db_runtime
```

**Option 2: Using psql directly**

```bash
# Switch to postgres user
sudo -u postgres psql

# In PostgreSQL shell, run these commands:
CREATE DATABASE as2_db_config OWNER as2user;
CREATE DATABASE as2_db_runtime OWNER as2user;

# Verify databases were created:
\l as2_db_config
\l as2_db_runtime

# Exit psql:
\q
```

**Option 3: As as2user (if granted CREATEDB permission)**

```bash
# Login as as2user
psql -U as2user -d postgres -h localhost

# In PostgreSQL shell:
CREATE DATABASE as2_db_config;
CREATE DATABASE as2_db_runtime;

# Exit:
\q
```

#### Test Connection

```bash
psql -U as2user -d as2_db_config -h localhost
# Enter password when prompted
# If successful: as2_db_config=>
\q
```

### 2. Extract Distribution

**Linux/macOS:**
```bash
# Fat JAR distribution (single executable)
tar -xzf mend-as2-1.1.0-dist.tar.gz
cd mend-as2-1.1.0

# Thin JAR distribution (with lib/ directory)
tar -xzf mend-as2-1.1.0-thin.tar.gz
cd mend-as2-1.1.0-thin

# Or extract zip
unzip mend-as2-1.1.0-dist.zip        # Fat JAR
unzip mend-as2-1.1.0-thin.zip        # Thin JAR
```

**Windows:**
```powershell
# Extract using Windows Explorer or:
Expand-Archive mend-as2-1.1.0-dist.zip       # Fat JAR
Expand-Archive mend-as2-1.1.0-thin.zip       # Thin JAR
cd mend-as2-1.1.0  # or mend-as2-1.1.0-thin
```

**Fat JAR Distribution Structure:**
```
mend-as2-1.1.0/
├── mend-as2-1.1.0.jar             # Main application (67 MB fat JAR)
├── config/                        # Configuration files
│   ├── as2.properties             # Mode selection & settings
│   └── database-postgresql.properties
├── sqlscript/                     # SQL scripts (auto-created)
├── log/                           # Log files (created at runtime)
├── messages/                      # AS2 messages (created at runtime)
├── data/                          # Runtime data (created at runtime)
├── start.sh                       # Linux/Mac GUI mode startup
├── start-headless.sh              # Linux/Mac headless mode startup
├── start.bat                      # Windows GUI mode startup
├── start-headless.bat             # Windows headless mode startup
├── README.md
├── INSTALL.md
└── LICENSE.gpl.txt
```

**Thin JAR Distribution Structure:**
```
mend-as2-1.1.0-thin/
├── mend-as2-1.1.0-thin.jar        # Main application (3 MB thin JAR)
├── lib/                           # All dependencies (~64 MB)
│   ├── bcprov-jdk18on-1.80.jar
│   ├── jetty-server-12.0.15.jar
│   ├── postgresql-42.7.4.jar
│   └── ... (all other dependencies)
├── config/                        # Configuration files
│   ├── as2.properties
│   └── database-postgresql.properties
├── sqlscript/                     # SQL scripts
├── start.sh                       # Uses classpath
├── start-headless.sh              # Uses classpath
├── start.bat                      # Uses classpath
├── start-headless.bat             # Uses classpath
├── README.md
├── INSTALL.md
└── LICENSE.gpl.txt
```

### 3. Configure Database

Edit `config/database-postgresql.properties`:

```properties
# Config database connection
db.config.driver.url=jdbc:postgresql://localhost:5432/as2_db_config
db.config.driver.user=as2user
db.config.driver.password=your_secure_password

# Runtime database connection
db.runtime.driver.url=jdbc:postgresql://localhost:5432/as2_db_runtime
db.runtime.driver.user=as2user
db.runtime.driver.password=your_secure_password
```

**Security Note**: Protect this file with proper permissions:
```bash
chmod 600 config/database-postgresql.properties
```

### 4. Choose Your Mode

The single distribution supports two modes:

**GUI Mode** - Desktop client (SwingUI) + Web interface
- **Use when**: You want desktop application access
- **Best for**: Workstations, development environments, local management
- **Starts**: SwingUI login dialog + WebUI on port 8080
- **Mina port**: 1234 (localhost only, for SwingUI connection)

**Headless Mode** - Web interface only
- **Use when**: Running on servers without display
- **Best for**: Production servers, Docker containers, cloud VMs
- **Starts**: WebUI only on port 8080
- **Mina port**: Disabled (better security)

**How to select:**

**Option 1: Startup script (recommended)**
```bash
# GUI Mode
./start.sh          # Linux/Mac
start.bat           # Windows

# Headless Mode  
./start-headless.sh # Linux/Mac
start-headless.bat  # Windows
```

**Option 2: Command line flag**
```bash
# GUI Mode (default)
java -jar mend-as2-1.1.0.jar

# Headless Mode
java -jar mend-as2-1.1.0.jar -nogui
```

**Option 3: Configuration file**
```bash
# Edit config/as2.properties
nano config/as2.properties

# Set mode:
as2.startup.gui.enabled=true   # GUI Mode
as2.startup.gui.enabled=false  # Headless Mode
```

### 5. First Run

**The database tables are automatically created on first startup!**

You don't need to run any SQL scripts manually. The application will:
1. Check if database tables exist
2. If not found, automatically create all tables
3. Initialize default admin user (username: `admin`, password: `admin`)
4. Insert initial configuration

Just start the server and it will handle everything.

## Running the Server

### GUI Mode (Desktop + Web)

**Fat JAR:**
```bash
# Linux/macOS
./start.sh

# Windows
start.bat
```

**Thin JAR:**
```bash
# Linux/macOS (uses classpath with lib/*)
./start.sh

# Windows (uses classpath with lib\*)
start.bat
```

This will:
1. Show SwingUI login dialog
2. Start Mina server on port 1234 (localhost only)
3. Start WebUI on port 8080

**Login credentials:**
- SwingUI: `admin` / `admin` (forced password change on first login)
- WebUI: http://localhost:8080/as2/webui/ (`admin` / `admin`)

### Headless Mode (Web Only)

**Fat JAR:**
```bash
# Linux/macOS
./start-headless.sh

# Windows
start-headless.bat
```

**Thin JAR:**
```bash
# Linux/macOS (uses classpath with lib/*)
./start-headless.sh

# Windows (uses classpath with lib\*)
start-headless.bat
```

This will:
- Skip SwingUI
- Disable Mina server (port 1234 not started)
- Start WebUI on port 8080

**Access:**
- WebUI: http://localhost:8080/as2/webui/
- Login: `admin` / `admin` (forced password change on first login)

### Manual Startup

**Fat JAR:**
```bash
# GUI Mode
java -jar mend-as2-1.1.0.jar

# Headless Mode
java -jar mend-as2-1.1.0.jar -nogui
```

**Thin JAR:**
```bash
# GUI Mode
java -cp "mend-as2-1.1.0-thin.jar:lib/*" de.mendelson.comm.as2.AS2

# Headless Mode
java -cp "mend-as2-1.1.0-thin.jar:lib/*" de.mendelson.comm.as2.AS2 -nogui

# Windows (use semicolon):
java -cp "mend-as2-1.1.0-thin.jar;lib\*" de.mendelson.comm.as2.AS2
```

### Customizing JVM Options

Edit the startup script to adjust memory settings:

**For Linux/macOS (edit `start-headless.sh`):**
```bash
# Change this line for production:
JAVA_OPTS="-Xms1g -Xmx4g -XX:+UseG1GC"

# For low-memory systems:
JAVA_OPTS="-Xms256m -Xmx1g"
```

**For Windows (edit `start-headless.bat`):**
```cmd
REM Change this line for production:
set JAVA_OPTS=-Xms1g -Xmx4g -XX:+UseG1GC
```

## Running as Background Service

### Linux (systemd)

Create `/etc/systemd/system/mend-as2.service`:

```ini
[Unit]
Description=Mend AS2 Server
After=network.target postgresql.service

[Service]
Type=simple
User=as2user
WorkingDirectory=/opt/mend-as2
ExecStart=/usr/bin/java -Xms1g -Xmx4g -jar /opt/mend-as2/mend-as2-1.1.0-headless.jar
Restart=on-failure
RestartSec=10

# Security hardening
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=/opt/mend-as2/log /opt/mend-as2/messages /opt/mend-as2/data

[Install]
WantedBy=multi-user.target
```

**Enable and start:**
```bash
# Copy distribution to /opt
sudo mkdir -p /opt/mend-as2
sudo cp -r mend-as2-1.1.0-headless/* /opt/mend-as2/
sudo chown -R as2user:as2user /opt/mend-as2

# Enable service
sudo systemctl daemon-reload
sudo systemctl enable mend-as2
sudo systemctl start mend-as2

# Check status
sudo systemctl status mend-as2

# View logs
sudo journalctl -u mend-as2 -f
```

### macOS (launchd)

Create `~/Library/LaunchAgents/com.zc2tech.mend-as2.plist`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>Label</key>
    <string>com.zc2tech.mend-as2</string>
    <key>ProgramArguments</key>
    <array>
        <string>/usr/bin/java</string>
        <string>-Xms1g</string>
        <string>-Xmx4g</string>
        <string>-jar</string>
        <string>/Users/yourusername/mend-as2/mend-as2-1.1.0-headless.jar</string>
    </array>
    <key>WorkingDirectory</key>
    <string>/Users/yourusername/mend-as2</string>
    <key>RunAtLoad</key>
    <true/>
    <key>KeepAlive</key>
    <true/>
    <key>StandardOutPath</key>
    <string>/Users/yourusername/mend-as2/log/stdout.log</string>
    <key>StandardErrorPath</key>
    <string>/Users/yourusername/mend-as2/log/stderr.log</string>
</dict>
</plist>
```

**Load and start:**
```bash
launchctl load ~/Library/LaunchAgents/com.zc2tech.mend-as2.plist
launchctl start com.zc2tech.mend-as2

# Check status
launchctl list | grep mend-as2
```

### Windows (NSSM - Non-Sucking Service Manager)

1. Download NSSM: https://nssm.cc/download
2. Install as service:

```cmd
nssm install MendAS2 "C:\Program Files\Java\jdk-17\bin\java.exe"
nssm set MendAS2 AppDirectory "C:\mend-as2"
nssm set MendAS2 AppParameters "-Xms1g -Xmx4g -jar C:\mend-as2\mend-as2-1.1.0-headless.jar"
nssm set MendAS2 DisplayName "Mend AS2 Server"
nssm set MendAS2 Description "Mend AS2 B2B Communication Server"
nssm set MendAS2 Start SERVICE_AUTO_START

# Start service
nssm start MendAS2

# Check status
sc query MendAS2
```

## Docker Deployment

### Using Pre-built Distribution

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_USER: as2user
      POSTGRES_PASSWORD: your_secure_password
      POSTGRES_DB: as2_db_config
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-db.sh:/docker-entrypoint-initdb.d/init-db.sh
    networks:
      - as2_network

  mend-as2:
    image: eclipse-temurin:17-jre-alpine
    working_dir: /app
    volumes:
      - ./mend-as2-1.1.0-headless.tar.gz:/tmp/app.tar.gz:ro
      - as2_data:/app/data
      - as2_logs:/app/log
      - as2_messages:/app/messages
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-Xms1g -Xmx4g
    depends_on:
      - postgres
    networks:
      - as2_network
    command: >
      sh -c "
      if [ ! -f /app/mend-as2-1.1.0-headless.jar ]; then
        tar -xzf /tmp/app.tar.gz -C /tmp &&
        cp -r /tmp/mend-as2-1.1.0-headless/* /app/ &&
        sed -i 's/localhost:5432/postgres:5432/g' /app/config/database-postgresql.properties &&
        sed -i 's/as2user/as2user/g' /app/config/database-postgresql.properties &&
        sed -i 's/your_secure_password/your_secure_password/g' /app/config/database-postgresql.properties;
      fi &&
      java $$JAVA_OPTS -jar /app/mend-as2-1.1.0-headless.jar
      "

volumes:
  postgres_data:
  as2_data:
  as2_logs:
  as2_messages:

networks:
  as2_network:
    driver: bridge
```

Create `init-db.sh`:

```bash
#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE as2_db_runtime;
    GRANT ALL PRIVILEGES ON DATABASE as2_db_runtime TO $POSTGRES_USER;
EOSQL
```

**Start:**
```bash
chmod +x init-db.sh
docker-compose up -d

# View logs
docker-compose logs -f mend-as2

# Access WebUI
# http://localhost:8080/as2/webui/
```

## Platform-Specific Notes

### macOS

- **Java**: Use Homebrew or download from Adoptium
- **PostgreSQL**: Homebrew recommended (`brew install postgresql@15`)
- **SwingUI**: Full macOS desktop integration
- **Security**: macOS Firewall may prompt for network access
- **Service**: Use launchd for background service

### Linux

- **Java**: Use package manager (apt/yum) or SDKMAN
- **PostgreSQL**: Use package manager or official repositories
- **SwingUI**: Requires X11/Wayland display (full distribution)
- **Headless**: Recommended for servers
- **SELinux**: May need to configure contexts for database access
- **Service**: Use systemd for background service

### Windows

- **Java**: Download from Adoptium or Oracle
- **PostgreSQL**: Use Windows installer from postgresql.org
- **SwingUI**: Fully supported with native look and feel (full distribution)
- **Service**: Use NSSM or Windows Service Wrapper
- **Firewall**: Add exception for port 8080

## Troubleshooting

### Database Connection Failed

**Error**: `Could not connect to PostgreSQL database`

**Solutions:**

1. Verify PostgreSQL is running:
   ```bash
   # Linux/macOS
   sudo systemctl status postgresql
   # or
   brew services list
   
   # Windows
   sc query postgresql-x64-15
   ```

2. Check connection parameters in `config/database-postgresql.properties`

3. Test connection manually:
   ```bash
   psql -U as2user -d as2_db_config -h localhost
   ```

4. Check PostgreSQL logs:
   ```bash
   # Linux
   sudo tail -f /var/log/postgresql/postgresql-15-main.log
   
   # macOS (Homebrew)
   tail -f /usr/local/var/log/postgresql@15.log
   ```

### Port Already in Use

**Error**: `Address already in use: bind`

**Solutions:**

1. Check what's using port 8080:
   ```bash
   # Linux/macOS
   lsof -i :8080
   
   # Windows
   netstat -ano | findstr :8080
   ```

2. Stop conflicting process or change port in application configuration

3. For Mina port 1234 conflicts (full distribution), use headless mode

### Java Version Mismatch

**Error**: Startup script fails with Java version error

**Solutions:**

1. Verify Java version:
   ```bash
   java -version
   # Should show Java 17 or higher
   ```

2. Set JAVA_HOME:
   ```bash
   # Linux/macOS
   export JAVA_HOME=/path/to/jdk-17
   export PATH=$JAVA_HOME/bin:$PATH
   
   # Windows
   set JAVA_HOME=C:\Program Files\Java\jdk-17
   set PATH=%JAVA_HOME%\bin;%PATH%
   ```

3. Edit startup script to use explicit Java path:
   ```bash
   # Change from:
   java -jar ...
   
   # To:
   /usr/lib/jvm/java-17-openjdk/bin/java -jar ...
   ```

### Tables Not Created

**Error**: Application starts but tables don't exist

**Solutions:**

1. Check application logs in `log/as2.log`:
   ```bash
   tail -f log/as2.log
   ```

2. Verify database user has CREATEDB permission:
   ```sql
   -- In psql:
   SELECT rolname, rolcreatedb FROM pg_roles WHERE rolname = 'as2user';
   ```

3. Check database connection in logs for errors

4. Manually create tables if auto-creation fails:
   ```bash
   psql -U as2user -d as2_db_config -h localhost -f sqlscript/config/CREATE.sql
   psql -U as2user -d as2_db_runtime -h localhost -f sqlscript/runtime/CREATE.sql
   ```

### Cannot Login to WebUI

**Error**: Login fails with "Invalid credentials"

**Solutions:**

1. **First time login**: Use `admin` / `admin`
   - You will be forced to change password

2. Check database for user:
   ```sql
   psql -U as2user -d as2_db_config
   SELECT id, username, enabled FROM webui_users;
   ```

3. Reset admin password if forgotten (requires database access):
   ```sql
   -- In psql connected to as2_db_config:
   UPDATE webui_users 
   SET password_hash = 'pbkdf2_sha256$150000$...',  -- Generate new hash
       must_change_password = true
   WHERE username = 'admin';
   ```

### Memory Issues

**Error**: `OutOfMemoryError`

**Solutions:**

1. Increase heap size in startup script:
   ```bash
   # Edit start-headless.sh
   JAVA_OPTS="-Xms1g -Xmx4g -XX:+UseG1GC"
   ```

2. Monitor memory usage:
   ```bash
   # Linux
   free -h
   ps aux | grep java
   
   # macOS
   top -pid $(pgrep java)
   ```

3. For production, recommended settings:
   ```bash
   JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
   ```

## Next Steps

After successful installation:

### 1. Change Default Password
- Login to WebUI: http://localhost:8080/as2/webui/
- Login as `admin` / `admin`
- You will be **forced to change password** on first login

### 2. Configure Trading Partners
- **WebUI**: Navigate to **Partners** section
- **SwingUI** (full distribution): Menu → **File → Partner**

### 3. Import Certificates
- **WebUI**: Navigate to **Certificates** section
- **SwingUI** (full distribution): Menu → **File → Certificates**

### 4. Configure System Settings
- **WebUI**: Navigate to **System** section
- **SwingUI** (full distribution): Menu → **File → Preferences**

### 5. Enable HTTPS (Production)
- Configure TLS certificate in Jetty configuration
- Update `jetty12/etc/jetty-https.xml`
- See [Security Best Practices](md-memo/SECURITY.md)

### 6. Setup Backups
- Regular PostgreSQL database backups:
  ```bash
  pg_dump -U as2user -d as2_db_config -f backup_config.sql
  pg_dump -U as2user -d as2_db_runtime -f backup_runtime.sql
  ```
- Backup `config/` directory
- Backup certificate keystores

### 7. Configure Firewall
- Allow port 8080 for WebUI/API
- Block port 1234 (Mina - full distribution only, localhost-only by default)
- Configure inbound AS2 message reception port

### 8. Setup Monitoring
- Configure log rotation for `log/as2.log`
- Monitor disk space for `messages/` directory
- Set up alerts for error conditions
- Consider using log aggregation (ELK, Splunk, etc.)

## Test Mode

For testing configurations, development, or running test and production instances side-by-side, you can enable **Test Mode** which uses alternative ports:

| Service | Normal Port | Test Mode Port |
|---------|-------------|----------------|
| HTTP | 8080 | **11080** |
| HTTPS | 8443 | **11443** |
| Client-Server | 1234 | **41234** |

### Quick Test Mode Start

**Windows:**
```batch
start.bat -Dmend.as2.testmode=true
start-headless.bat -Dmend.as2.testmode=true
```

**Linux/Mac:**
```bash
./start.sh -Dmend.as2.testmode=true
./start-headless.sh -Dmend.as2.testmode=true
```

### Using Environment Variable

**Windows:**
```batch
set AS2_TEST_MODE=true
start.bat
```

**Linux/Mac:**
```bash
export AS2_TEST_MODE=true
./start.sh
```

### Using Properties File

Edit `config/as2.properties`:
```properties
as2.test.mode=true
```

**Important**: Use separate databases and data directories for test instances.

For complete test mode documentation, see [md-memo/TEST_MODE.md](md-memo/TEST_MODE.md)

## Support

- **Documentation**: [README.md](README.md) - Overview and features
- **Build Guide**: [md-memo/BUILD.md](md-memo/BUILD.md) - Building from source
- **Build Profiles**: [md-memo/BUILD_PROFILES.md](md-memo/BUILD_PROFILES.md) - Full vs Headless
- **Issues**: [GitHub Issues](https://github.com/zc2tech/mend-as2/issues)
- **Email**: julian.xu@aliyun.com

---

**Installation complete!** Access your AS2 server at http://localhost:8080/as2/webui/

**Default credentials**: `admin` / `admin` (forced password change on first login)
