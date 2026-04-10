# Mend AS2 Server

A modern, feature-rich AS2 (Applicability Statement 2) server for secure B2B communication, forked from mendelson AS2.

![License](https://img.shields.io/badge/license-GPL--2.0-blue.svg)
![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)

## 🚀 Features

### Core AS2 Capabilities
- **AS2 Protocol Implementation** - Full AS2 1.0/1.1 support for secure message exchange
- **Message Encryption & Signing** - Support for multiple encryption and signing algorithms
- **MDN Support** - Synchronous and asynchronous MDN (Message Disposition Notification)
- **Certificate Management** - Built-in certificate and key management
- **Partner Configuration** - Flexible trading partner setup and management

### Modern Enhancements
- **Dual UI Support**
  - **SwingUI** - Traditional Java desktop application
  - **WebUI** - Modern React-based web interface with responsive design
  
- **Role-Based Access Control (RBAC)**
  - Pre-defined roles: Admin, Partner Manager, Certificate Manager, Message Operator, System Manager, Viewer
  - Granular permission system (READ/WRITE per module)
  - Custom role creation and management
  - User-to-role assignment via both UIs

- **User Management**
  - Web and desktop user management interfaces
  - Password generation and email delivery
  - Forced password change on first login
  - Password complexity enforcement
  - Account enable/disable functionality

- **HTTP Authentication (Outbound)**
  - Flexible HTTP Basic Auth for partner connections
  - Three modes: None, Always Use, Use User Preference
  - User-specific credential management (WebUI & SwingUI)
  - Separate credentials for Message and MDN requests
  - Runtime credential resolution based on partner configuration

- **Inbound Authentication**
  - System-wide authentication for incoming AS2 messages
  - Two authentication types (can enable both):
    - **Basic Authentication** - Multiple username/password pairs
    - **Certificate Authentication** - Multiple trusted certificates
  - OR logic: message accepted if ANY credential matches
  - Configuration via SwingUI (System Preferences) and WebUI (System menu)
  - HTTP 401 response for failed authentication
  - Authentication logging for security auditing

- **Partner Visibility Control**
  - User-based partner access restrictions
  - "Visible to all" or "Specific users only" modes
  - Message list filtering based on partner visibility
  - Admin users bypass all visibility restrictions
  - Only applies to remote partners (local stations always visible)

- **Enhanced Security**
  - JWT-based authentication for WebUI
  - HttpOnly cookies for session management
  - PBKDF2 password hashing with mandatory first-login password change
  - SwingUI in-process communication (no network ports)
  - Permission-based API endpoint protection
  - Read-only UI for viewer roles
  - Partner-level visibility controls
  - Inbound message authentication (Basic + Certificate Auth)
  - Zero network attack surface for SwingUI (EventBus replaces Mina TCP)

- **Modern Tech Stack**
  - **Backend**: Java 17+, Jetty 12, Jakarta EE 10, JAX-RS (Jersey)
  - **Frontend**: React 18, React Router, TanStack Query, Vite
  - **Database**: PostgreSQL 15+ (replaced HSQLDB)
  - **Build**: Maven 3.9+

## 📦 Installation

### For End Users (Recommended)

Download the latest release from [GitHub Releases](https://github.com/zc2tech/mend-as2/releases):

**Single Distribution** (`mend-as2-1.1.0-dist.tar.gz`) - **81 MB**
- Supports both GUI mode (SwingUI) and Headless mode (WebUI only)
- Choose your mode at startup with command-line flag or config file

**Quick Start:**
```bash
# 1. Download and extract
wget https://github.com/zc2tech/mend-as2/releases/download/v1.1.0/mend-as2-1.1.0-dist.tar.gz
tar -xzf mend-as2-1.1.0-dist.tar.gz
cd mend-as2-1.1.0

# 2. Install PostgreSQL 14+ and create databases
createdb -O as2user as2_db_config
createdb -O as2user as2_db_runtime

# 3. Configure database connection
nano config/database-postgresql.properties

# 4. Choose your mode:

# GUI Mode (SwingUI + WebUI):
./start.sh

# Headless Mode (WebUI only):
./start-headless.sh
# Or:
java -jar mend-as2-1.1.0.jar -nogui

# 5. Access WebUI
# http://localhost:8080/as2/webui/
# Login: admin / admin (forced password change)
```

**Mode Selection:**
- **GUI Mode**: Desktop client (SwingUI) + Web interface
  - Best for: Workstations, development environments
  - Start with: `./start.sh` or `java -jar mend-as2-1.1.0.jar`
  
- **Headless Mode**: Web interface only (no desktop GUI)
  - Best for: Servers, containers, cloud deployments
  - Start with: `./start-headless.sh` or `java -jar mend-as2-1.1.0.jar -nogui`
  - Can also set in config: `as2.startup.gui.enabled=false`

**📖 Full Installation Guide:** [INSTALL.md](INSTALL.md)

### For Developers

Build from source for development or customization:

**Prerequisites:**
- Java 17+ JDK
- Maven 3.9+
- PostgreSQL 14+
- Node.js 18+ (for WebUI development)

**Build:**

```bash
# Clone repository
git clone https://github.com/zc2tech/mend-as2.git
cd mend-as2

# Build release distribution
mvn clean package

# Run in GUI mode
java -jar target/mend-as2-1.1.0.jar

# Run in headless mode
java -jar target/mend-as2-1.1.0.jar -nogui
```

**📖 Developer Guide:** [md-memo/BUILD.md](md-memo/BUILD.md)  
**📦 Build Profiles:** [md-memo/BUILD_PROFILES.md](md-memo/BUILD_PROFILES.md)  
**🚀 Release Process:** [RELEASE.md](RELEASE.md)

## 🎯 Usage

### Inbound Authentication

Configure authentication required for incoming AS2 messages:

**WebUI:**
1. Login as Admin
2. Navigate to **System** → **Inb. Auth** tab
3. Enable authentication types:
   - ☑ **Enable Basic Authentication**
     - Click **Add Credential** to add username/password pairs
     - Support multiple credentials (OR logic - any match accepted)
     - Password visibility toggle available
   - ☑ **Enable Certificate Authentication**
     - Click **Add Certificate** to select certificates from repository
     - Support multiple certificates (OR logic - any match accepted)
4. Click **Save Settings**

**SwingUI:**
1. Open AS2Gui
2. Menu → **File → Preferences**
3. Navigate to **Inb. Auth** tab
4. Check authentication types:
   - ☑ **Enable Basic Authentication** - Add multiple username/password rows
   - ☑ **Enable Certificate Authentication** - Add multiple certificates from dropdown
5. Click **OK** to save

**Behavior:**
- If both unchecked: No authentication required (open access)
- If one enabled: Must pass that authentication type
- If both enabled: Must pass EITHER Basic OR Certificate auth
- Failed authentication returns HTTP 401 Unauthorized
- All authentication attempts logged to server log

### Partner Visibility Control

Control which users can see and interact with specific partners:

1. **Configure Partner Visibility** (Admin/Partner Manager):
   - Edit Partner → Navigate to **Visibility** tab
   - Choose "Visible to all users" (default) or "Visible to specific users only"
   - Select specific users if using restricted mode
   - Local stations are always visible to all users

2. **Effects**:
   - Users only see their visible partners in:
     - Message sending interface
     - Partner filter dropdowns
     - Message list (filtered automatically)
   - Admin users bypass all restrictions

### HTTP Authentication Preferences

Configure HTTP Basic Auth credentials for partner connections:

**WebUI:**
1. Login to WebUI
2. Click username dropdown → **Preferences**
3. View **HTTP Authentication** tab
4. For each partner:
   - Enable/disable Message Authentication
   - Enter username and password
   - Enable/disable MDN Authentication
   - Enter username and password
5. Click **Save** for each partner

**SwingUI:**
1. Open AS2Gui
2. Menu → **User Preference → HTTP Authentication**
3. Table shows all remote partners
4. Check auth checkboxes and enter credentials
5. Click **OK** to save all changes

**Partner Configuration:**
- Edit Partner → **HTTP Authentication** tab
- Select mode:
  - **None** - No HTTP auth
  - **Always Use** - Use credentials from partner config
  - **Use User Preference** - Use credentials from user preferences (above)

### WebUI Access

Navigate to `http://localhost:8080/as2/webui/` and login.

**Available Sections (permission-based):**
- **Dashboard** - Quick access to all sections
- **Partners** - Manage trading partners with visibility controls
- **Certificates** - Import/export certificates
- **Messages** - View and send AS2 messages (filtered by partner visibility)
- **System** - Server configuration and inbound authentication
  - HTTP Server Configuration
  - **Inbound Authentication** - Configure incoming message authentication
  - System Events
  - Server Log Search
  - Maintenance
  - Notification
- **Users** - User and role management (Admin only)
- **Preferences** - HTTP authentication credentials (user-specific)

### SwingUI (Desktop)

```bash
java -cp target/mend-as2-1.1.jar de.mendelson.comm.as2.client.AS2Gui
```

**Menu Navigation:**
- **File → Partner** - Manage trading partners
- **File → Certificates** - Certificate management
- **File → Preferences** - System-wide preferences
  - HTTP Server Configuration
  - **Inbound Authentication** - Configure incoming message authentication
  - Connectivity, Proxy, Directories, etc.
- **User Preference → HTTP Authentication** - Configure HTTP auth credentials

**Keyboard Shortcuts:**
- `Cmd/Ctrl + ,` - Open Preferences
- `Cmd/Ctrl + U` - Open User Management
- `Cmd/Ctrl + N` - Create new user
- `Cmd/Ctrl + E` - Edit selected
- `Cmd/Ctrl + D` - Delete selected
- `Cmd/Ctrl + R` - Refresh
- `ESC` - Close dialog

### REST API

```bash
# Login
curl -X POST http://localhost:8080/as2/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' \
  -c cookies.txt

# List users
curl http://localhost:8080/as2/api/v1/users -b cookies.txt
```

**Key Endpoints:**
- `POST /auth/login` - Authenticate
- `GET /users` - List users (USER_MANAGE)
- `GET /partners` - List partners (PARTNER_READ)
- `GET /partners?visibleToUser={id}` - List partners visible to user
- `GET /certificates` - List certificates (CERT_READ)
- `GET /messages` - List messages (MESSAGE_READ, filtered by partner visibility)
- `GET /system/info` - System info (SYSTEM_READ)
- `GET /system/inbound-auth/config` - Get inbound auth mode
- `POST /system/inbound-auth/config` - Set inbound auth mode
- `GET /system/inbound-auth/credentials/basic` - Get basic auth credentials
- `POST /system/inbound-auth/credentials/basic` - Save basic credentials
- `GET /system/inbound-auth/credentials/cert` - Get cert credentials
- `POST /system/inbound-auth/credentials/cert` - Save cert credentials
- `GET /user-preferences/http-auth` - Get user's HTTP auth preferences
- `POST /user-preferences/http-auth` - Save HTTP auth credentials

### User Roles & Permissions

| Role | Permissions | Use Case |
|------|------------|----------|
| **ADMIN** | All | Full system access |
| **PARTNER_MANAGER** | Partner R/W | Manage trading partners |
| **CERTIFICATE_MANAGER** | Certificate R/W | Manage certificates |
| **MESSAGE_OPERATOR** | Message R/W | Send/view messages |
| **SYSTEM_MANAGER** | System R/W | Configure server |
| **VIEWER** | All Read | Read-only access |

## 🏗️ Architecture

```
mend-as2/
├── src/main/
│   ├── java/                    # Java backend
│   │   └── de/mendelson/comm/as2/
│   │       ├── server/          # AS2 server core
│   │       ├── servlet/rest/    # REST API
│   │       ├── partner/         # Partner management
│   │       └── usermanagement/  # User, RBAC & HTTP Auth
│   ├── resources/
│   │   └── sqlscript/           # Database schemas
│   └── webapp/admin/            # React WebUI
│       └── src/
│           ├── features/
│           │   ├── partners/    # Partner UI
│           │   ├── messages/    # Message UI
│           │   ├── users/       # User management
│           │   └── preferences/ # HTTP Auth preferences
│           └── api/             # REST client
├── config/                      # Configuration
└── jetty12/                     # Jetty server
```

## 🔐 Security

- ✅ JWT authentication with HttpOnly cookies (WebUI)
- ✅ SwingUI in-process communication via EventBus (no network ports)
- ✅ PBKDF2 password hashing
- ✅ Role-based access control (RBAC)
- ✅ API permission enforcement
- ✅ Forced password change on first login
- ✅ Session timeout and refresh tokens
- ✅ Partner visibility controls
- ✅ User-specific HTTP authentication credentials (outbound)
- ✅ System-wide inbound message authentication (Basic + Certificate Auth)
- ✅ Zero attack surface for GUI mode (Mina removed, EventBus replaces TCP)

**Best Practices:**
1. Change default admin password immediately
2. Use strong passwords (min 6 chars)
3. Enable HTTPS in production
4. Assign minimum required permissions
5. Regular database backups
6. Configure partner visibility for sensitive partners
7. Use "User Preference" mode for HTTP auth when multiple users share partners
8. Enable inbound authentication to prevent unauthorized message submission
9. Use certificate authentication for strongest security
10. Regularly audit authentication logs for suspicious activity

## 🔄 Backup & Restore

```bash
# Backup
pg_dump -U as2user -d as2_db_config -f backup_config.sql
pg_dump -U as2user -d as2_db_runtime -f backup_runtime.sql

# Restore
psql -U as2user -d as2_db_config -f backup_config.sql
psql -U as2user -d as2_db_runtime -f backup_runtime.sql
```

## 🛠️ Development

```bash
# Build WebUI only
cd src/main/webapp/admin
npm install
npm run dev      # Dev server with hot reload
npm run build    # Production build

# Run tests
mvn test
```

## 📝 Migration from Mendelson AS2

**Key Differences:**
- Database: PostgreSQL (was HSQLDB)
- WebUI: React (was Vaadin)
- Auth: JWT-based (was session-based)
- New: Full RBAC system
- New: Modern responsive UI
- New: HTTP Authentication preferences
- New: Partner visibility controls
- New: User-specific preferences

**Migration Steps:**
1. Export data from mendelson AS2 (use database export tools)
2. Install Mend AS2 following [INSTALL.md](INSTALL.md)
3. Configure PostgreSQL databases
4. Import certificates and partner configurations
5. Set up users and roles using new RBAC system
6. Test connections with trading partners

For detailed migration assistance, see [GitHub Issues](https://github.com/zc2tech/mend-as2/issues) or contact support.

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/name`)
3. Commit changes (`git commit -m 'Add feature'`)
4. Push branch (`git push origin feature/name`)
5. Open Pull Request

## 📄 License

GNU General Public License v2.0 - see [LICENSE](license/LICENSE.gpl.txt)

## 🙏 Acknowledgments

- **Original Project**: mendelson AS2 by mendelson-e-commerce GmbH
- **Contributors**: All who have helped improve this project

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/zc2tech/mend-as2/issues)
- **Email**: julian.xu@aliyun.com
- **Docs**: Check wiki for detailed guides

## 🗺️ Roadmap

- [x] Role-Based Access Control (RBAC)
- [x] HTTP Authentication preferences (outbound)
- [x] Inbound message authentication (Basic + Certificate)
- [x] Partner visibility controls
- [x] Message filtering by partner visibility
- [x] SwingUI in-process communication (Mina removal)
- [x] Zero network attack surface for GUI mode
- [x] Forced password change for admin user
- [x] Mode selection (GUI vs headless) via config/flag
- [x] Database auto-creation on first run
- [x] Fat JAR with all dependencies (no Maven needed)
- [x] Single release distribution (81 MB)
- [ ] Read-only UI for all components
- [ ] Enhanced message filtering options
- [ ] Real-time monitoring dashboard
- [ ] Multi-language support
- [ ] Docker containerization
- [ ] Kubernetes templates
- [ ] Performance metrics
- [ ] LDAP/AD integration
- [ ] OAuth2 support for HTTP authentication

## 🔍 Troubleshooting

**Database Connection Failed**
- Check PostgreSQL is running
- Verify credentials in `config/database-postgresql.properties`

**WebUI Not Loading**
- Rebuild WebUI: `cd src/main/webapp/admin && npm run build`
- Check server logs

**Permission Denied**
- Verify user has correct role
- Check JWT token validity

**Email Not Sending**
- Test SMTP with "Send Test Mail"
- For Gmail: use App Password
- For Aliyun/163: use Authorization Code

**Partner Not Visible in Message Send**
- Check partner visibility settings (Partners → Edit → Visibility tab)
- Verify user is in the "visible to" list (or visibility is "all users")
- Admin users can see all partners

**HTTP Authentication Not Working (Outbound)**
- Verify partner is configured with "Use User Preference" mode
- Check user has set credentials in User Preference → HTTP Authentication
- For SwingUI: Check credentials are set for admin user (ID=1)

**Inbound Messages Being Rejected (401 Unauthorized)**
- Check System → Inb. Auth settings (WebUI) or File → Preferences → Inb. Auth (SwingUI)
- Verify at least one authentication type is enabled
- For Basic Auth: Ensure sending partner uses one of the configured username/password pairs
- For Certificate Auth: Ensure sending partner's certificate is in the trusted list
- Check server logs for authentication failure details
- If both auth types enabled, message must pass EITHER one (OR logic)

**SwingUI Login Issues**
- SwingUI now runs in-process (no network authentication required)
- No login dialog - SwingUI connects directly to server components via EventBus
- For password reset, use WebUI or database update: `UPDATE webui_users SET password_hash='...' WHERE username='admin'`

**Headless Mode**
- Use `-nogui` flag: `java -jar mend-as2-1.1.0-full.jar -nogui`
- Or use headless build profile: `mvn clean package -Pheadless`
- Headless build is ~15-20 MB smaller (excludes SwingUI dependencies)
- Access via WebUI at http://localhost:8080/as2/webui/

---

**Made with ❤️ by Julian Xu**
