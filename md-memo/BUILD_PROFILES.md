# Mend-AS2 Build Profiles

Mend-AS2 supports two build profiles for different deployment scenarios:

## 1. Full Profile (Default)

**Includes:** SwingUI + WebUI  
**Use case:** Desktop/server installations requiring both graphical interface and web management

### Build Command:
```bash
mvn clean package -Pfull
```

Or simply:
```bash
mvn clean package
```
(full profile is active by default)

### Output:
- `target/mend-as2-1.1.0-full.jar`

### Included Dependencies:
- **Apache Mina**: Client-server communication for SwingUI
- **FlatLaf**: Modern Look & Feel for Swing components
- **Batik SVG**: SVG icon rendering
- **JCalendar**: Date picker components
- **JFreeChart**: Statistics charts
- **L2FProd**: Button bar UI components
- All WebUI dependencies (Jetty, React, etc.)

### Startup Options:
```bash
# GUI mode (default)
java -jar mend-as2-1.1.0-full.jar

# Headless mode (WebUI only)
java -jar mend-as2-1.1.0-full.jar -nogui

# Configuration file
config/as2.properties: as2.startup.gui.enabled=true/false
```

## 2. Headless Profile

**Includes:** WebUI only (no SwingUI)  
**Use case:** Containerized/cloud deployments, servers without GUI requirements

### Build Command:
```bash
mvn clean package -Pheadless
```

### Output:
- `target/mend-as2-1.1.0-headless.jar`

### Excluded Dependencies:
- ❌ Apache Mina (~1.5 MB)
- ❌ FlatLaf (~2 MB)
- ❌ Batik SVG (~10 MB)
- ❌ JCalendar (~200 KB)
- ❌ JFreeChart (~1.5 MB)
- ❌ L2FProd (~400 KB)

**Total savings: ~15-20 MB smaller distribution**

### Configuration:
The headless build includes a pre-configured `config/as2.properties`:
```properties
as2.startup.gui.enabled=false
```

### Startup:
```bash
# WebUI management only
java -jar mend-as2-1.1.0-headless.jar
```

**Management Access:**
- WebUI: http://localhost:8080/as2/webui/
- REST API: http://localhost:8080/as2/api/

### Security:
- Mina client-server port (1234) is **NOT** started
- No localhost attack surface for SwingUI
- Suitable for multi-tenant environments

### Attempting GUI in Headless Build:
If you try to start GUI mode with a headless build:
```bash
java -jar mend-as2-1.1.0-headless.jar  # GUI disabled by default config
```

The system will detect missing Mina libraries and automatically force headless mode with a warning message.

## Comparison Table

| Feature | Full Profile | Headless Profile |
|---------|--------------|------------------|
| **SwingUI** | ✅ Available | ❌ Not available |
| **WebUI** | ✅ Available | ✅ Available |
| **REST API** | ✅ Available | ✅ Available |
| **Mina Port 1234** | Started in GUI mode | Never started |
| **Distribution Size** | ~120 MB | ~100-105 MB |
| **Docker/Container** | ✅ Works | ✅ Recommended |
| **Desktop Use** | ✅ Recommended | ⚠️ Limited |
| **Cloud Deployment** | ✅ Works | ✅ Optimized |

## Docker Example

### Full Profile Dockerfile:
```dockerfile
FROM eclipse-temurin:17-jre
COPY target/mend-as2-1.1.0-full.jar /app/as2.jar
CMD ["java", "-jar", "/app/as2.jar", "-nogui"]
```

### Headless Profile Dockerfile (Recommended):
```dockerfile
FROM eclipse-temurin:17-jre
COPY target/mend-as2-1.1.0-headless.jar /app/as2.jar
CMD ["java", "-jar", "/app/as2.jar"]
```

## Environment Variables

Both profiles support environment variable overrides:

```bash
# Force headless mode (full build only)
AS2_START_GUI=false java -jar mend-as2-1.1.0-full.jar

# Display mode for GUI (full build only)
AS2_DISPLAY_MODE=DARK java -jar mend-as2-1.1.0-full.jar

# Skip configuration check on startup
AS2_SKIP_CONFIG_CHECK=true java -jar mend-as2-1.1.0-headless.jar
```

## Development

### Running from IDE:
Both profiles work in IDE (VS Code, IntelliJ):

**.vscode/launch.json:**
```json
{
    "configurations": [
        {
            "name": "AS2 (GUI Mode)",
            "mainClass": "de.mendelson.comm.as2.AS2",
            "vmArgs": "-Dfull.profile=true"
        },
        {
            "name": "AS2 (Headless Mode)",
            "mainClass": "de.mendelson.comm.as2.AS2",
            "args": "-nogui"
        }
    ]
}
```

### Testing Profile Builds:
```bash
# Test full build
mvn clean compile -Pfull

# Test headless build
mvn clean compile -Pheadless

# Verify dependency exclusions
mvn dependency:tree -Pheadless | grep -E "(mina|flatlaf|batik|jcalendar)"
```

## Migration Guide

### From Full to Headless:
1. Back up your database
2. Back up `config/` directory
3. Build with `-Pheadless`
4. Deploy new JAR
5. Access via WebUI at port 8080

### From Headless to Full:
1. Back up your database  
2. Build with `-Pfull`
3. Deploy new JAR
4. Can now use both SwingUI and WebUI

**Note:** Database and configuration are compatible between both profiles.

## Troubleshooting

### "ClassNotFoundException: org.apache.mina.core.service.IoAcceptor"
- You're trying to start GUI with a headless build
- Solution: Use `-nogui` flag or rebuild with `-Pfull`

### SwingUI Login Fails
- Headless build: SwingUI is not available
- Full build in headless mode: Mina server disabled by configuration
- Solution: Enable GUI in `config/as2.properties` or rebuild

### WebUI Not Starting
- Check port 8080 is not in use
- Check `logs/as2.log` for errors
- Both profiles include Jetty server for WebUI

## Recommendations

✅ **Use Full Profile:**
- Desktop workstations
- Development environments
- Small servers with GUI access
- Users prefer local Swing client

✅ **Use Headless Profile:**
- Docker/Kubernetes deployments
- Cloud VMs (AWS, Azure, GCP)
- Continuous integration environments
- Security-hardened servers
- Multi-tenant SaaS deployments

---

**Questions?** See main [README.md](README.md) or [GitHub Issues](https://github.com/zc2tech/mend-as2/issues)
