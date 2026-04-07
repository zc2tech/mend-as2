# Mend AS2 Build Guide

Developer guide for building Mend AS2 from source.

> **For End Users**: See [INSTALL.md](../INSTALL.md) for installing pre-built releases.  
> **For Maintainers**: See [RELEASE.md](../RELEASE.md) for creating official releases.

## Prerequisites

- **Java 17+** JDK (for compiling)
- **Maven 3.9+**
- **Node.js 18+** and npm (for WebUI development)
- **PostgreSQL 14+** (for testing)
- **Git**

## Quick Build

```bash
# Clone repository
git clone https://github.com/zc2tech/mend-as2.git
cd mend-as2

# Build with default (full) profile
mvn clean package -DskipTests

# Output: target/mend-as2-1.1.0-full.jar
```

## Build Profiles

Mend AS2 offers two build profiles:

### Full Profile (Default)
```bash
mvn clean package -Pfull
# or just:
mvn clean package
```

**Produces:**
- `target/mend-as2-1.1.0-full.jar` (~120 MB)
- `target/mend-as2-1.1.0-full-full.tar.gz` (~2.6 MB)
- `target/mend-as2-1.1.0-full-full.zip` (~2.6 MB)

**Includes:**
- SwingUI (desktop client)
- WebUI
- Apache Mina (port 1234)
- All GUI dependencies

### Headless Profile
```bash
mvn clean package -Pheadless
```

**Produces:**
- `target/mend-as2-1.1.0-headless.jar` (~100-105 MB)
- `target/mend-as2-1.1.0-headless-headless.tar.gz` (~2.6 MB)
- `target/mend-as2-1.1.0-headless-headless.zip` (~2.6 MB)

**Includes:**
- WebUI only
- No SwingUI dependencies
- No Mina server
- 15-20 MB smaller

See [BUILD_PROFILES.md](BUILD_PROFILES.md) for detailed comparison.

## Development Setup

### 1. Clone and Build

```bash
git clone https://github.com/zc2tech/mend-as2.git
cd mend-as2
mvn clean package -DskipTests
```

### 2. Setup PostgreSQL

```bash
# Create databases
createdb -O as2user as2_db_config
createdb -O as2user as2_db_runtime

# Configure connection
cp config/database-postgresql.properties.example config/database-postgresql.properties
nano config/database-postgresql.properties
```

### 3. Run from IDE

**VS Code** (`.vscode/launch.json`):
```json
{
  "configurations": [
    {
      "type": "java",
      "name": "AS2 (GUI Mode)",
      "request": "launch",
      "mainClass": "de.mendelson.comm.as2.AS2",
      "projectName": "mend-as2"
    },
    {
      "type": "java",
      "name": "AS2 (Headless Mode)",
      "request": "launch",
      "mainClass": "de.mendelson.comm.as2.AS2",
      "projectName": "mend-as2",
      "args": "-nogui"
    }
  ]
}
```

**IntelliJ IDEA:**
1. Open project (pom.xml)
2. Run → Edit Configurations
3. Add Application
   - Main class: `de.mendelson.comm.as2.AS2`
   - VM options: `-Xmx2g`
   - Program arguments: (none for GUI, `-nogui` for headless)

### 4. WebUI Development

```bash
cd src/main/webapp/admin

# Install dependencies
npm install

# Development server (hot reload)
npm run dev
# Open: http://localhost:5173

# Production build
npm run build
```

## Build Commands

### Clean Build
```bash
mvn clean package
```

### Skip Tests
```bash
mvn clean package -DskipTests
```

### Build Both Profiles
```bash
mvn clean package -Pfull && mvn package -Pheadless
```

### Build Only JAR (no distributions)
```bash
mvn clean compile jar:jar
```

### Run Tests
```bash
mvn test
```

### Check Dependencies
```bash
mvn dependency:tree
mvn dependency:analyze
```

## Project Structure

```
mend-as2/
├── src/
│   ├── main/
│   │   ├── java/                      # Java source code
│   │   │   └── de/mendelson/comm/as2/
│   │   │       ├── AS2.java           # Main entry point
│   │   │       ├── server/            # AS2 server core
│   │   │       ├── client/            # SwingUI client
│   │   │       ├── servlet/rest/      # REST API
│   │   │       ├── partner/           # Partner management
│   │   │       ├── message/           # Message processing
│   │   │       └── usermanagement/    # RBAC & auth
│   │   ├── resources/
│   │   │   ├── sqlscript/             # Database schemas
│   │   │   │   ├── config/CREATE.sql
│   │   │   │   └── runtime/CREATE.sql
│   │   │   └── webapp/                # Static web resources
│   │   ├── resources-headless/        # Headless config overrides
│   │   └── webapp/admin/              # React WebUI
│   │       ├── src/
│   │       │   ├── features/          # Feature modules
│   │       │   ├── api/               # REST client
│   │       │   └── App.jsx            # Main app
│   │       ├── package.json
│   │       └── vite.config.js
│   ├── assembly/                      # Distribution assemblies
│   │   ├── full-distribution.xml
│   │   ├── headless-distribution.xml
│   │   └── scripts/                   # Startup scripts
│   └── test/java/                     # Unit tests
├── config/                            # Configuration files
├── jetty12/                           # Jetty server config
├── md-memo/                           # Documentation
├── pom.xml                            # Maven build config
├── README.md                          # Overview
├── INSTALL.md                         # Installation guide
└── RELEASE.md                         # Release process
```

## Maven Profiles

### Full Profile
```xml
<profile>
    <id>full</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
    <dependencies>
        <!-- Mina, FlatLaf, Batik, JCalendar, etc. -->
        <!-- scope=compile (included) -->
    </dependencies>
</profile>
```

### Headless Profile
```xml
<profile>
    <id>headless</id>
    <dependencies>
        <!-- SwingUI dependencies excluded -->
        <!-- Mina scope=provided (not included) -->
    </dependencies>
</profile>
```

## Dependency Management

### Key Dependencies

- **Jetty 12**: Web server and servlet container
- **Jersey 3**: JAX-RS REST API implementation
- **HikariCP**: Connection pooling
- **PostgreSQL JDBC**: Database driver
- **BouncyCastle**: Cryptography (AS2 encryption/signing)
- **Jackson**: JSON serialization
- **JJWT**: JWT authentication (WebUI)

**SwingUI (full profile only):**
- **Apache Mina**: Client-server communication (port 1234)
- **FlatLaf**: Modern Swing Look & Feel
- **Batik SVG**: SVG icon rendering
- **JCalendar**: Date picker components
- **JFreeChart**: Statistics charts

### Updating Dependencies

```bash
# Check for updates
mvn versions:display-dependency-updates

# Update to latest versions
mvn versions:use-latest-versions

# Check plugin updates
mvn versions:display-plugin-updates
```

## Troubleshooting

### WebUI Not Building

**Issue**: `npm install` fails or WebUI not in JAR

**Solution:**
```bash
cd src/main/webapp/admin
rm -rf node_modules package-lock.json
npm install
npm run build
cd ../../../..
mvn package
```

### Java Version Issues

**Issue**: Build fails with `UnsupportedClassVersionError`

**Solution:**
```bash
# Check Java version
java -version
javac -version

# Should both be 17+
# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/jdk-17
```

### Database Connection Fails

**Issue**: Tests fail with database connection error

**Solution:**
```bash
# Check PostgreSQL running
pg_isready

# Verify credentials in config/database-postgresql.properties
# Create databases if missing
createdb -O as2user as2_db_config
createdb -O as2user as2_db_runtime
```

### Assembly Plugin Fails

**Issue**: Distribution archives not created

**Solution:**
```bash
# Ensure src/assembly/ exists with descriptors
ls src/assembly/*.xml

# Check Maven assembly plugin in pom.xml
mvn help:effective-pom | grep assembly
```

## Contributing

### Code Style

- Java: Follow existing style (Google Java Style with modifications)
- JavaScript/React: Prettier + ESLint (see WebUI `.eslintrc`)
- 4 spaces indentation for Java
- 2 spaces for JavaScript/JSX

### Commit Messages

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Example:
```
feat(webui): add partner visibility controls

- Add user-specific partner access restrictions
- Filter message list by partner visibility
- Admin users bypass all restrictions

Closes #24
```

### Pull Request Process

1. Fork the repository
2. Create feature branch: `git checkout -b feature/my-feature`
3. Make changes and commit
4. Push to fork: `git push origin feature/my-feature`
5. Open Pull Request with description
6. Ensure CI passes
7. Wait for review

### Running Tests Before PR

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=UserManagementTest

# Skip tests during development
mvn package -DskipTests
```

## CI/CD

GitHub Actions workflows (if configured):

- **Build**: Runs on every push
- **Test**: Runs test suite
- **Release**: Creates GitHub release on tag push

See `.github/workflows/` for configurations.

## Documentation

- [README.md](../README.md) - Project overview
- [INSTALL.md](../INSTALL.md) - End-user installation
- [RELEASE.md](../RELEASE.md) - Release process
- [BUILD_PROFILES.md](BUILD_PROFILES.md) - Full vs Headless comparison
- [RELEASE_DISTRIBUTION.md](RELEASE_DISTRIBUTION.md) - Distribution details

## Support

- **Issues**: [GitHub Issues](https://github.com/zc2tech/mend-as2/issues)
- **Discussions**: [GitHub Discussions](https://github.com/zc2tech/mend-as2/discussions)
- **Email**: julian.xu@aliyun.com

---

**Happy coding!** 🚀
