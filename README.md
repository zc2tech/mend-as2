# Mend AS2 Server

A modern, feature-rich AS2 (Applicability Statement 2) server for secure B2B communication, forked from mendelson AS2.

![License](https://img.shields.io/badge/license-GPL--2.0-blue.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
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

- **HTTP Authentication**
  - Flexible HTTP Basic Auth for partner connections
  - Three modes: None, Always Use, Use User Preference
  - User-specific credential management (WebUI & SwingUI)
  - Separate credentials for Message and MDN requests
  - Runtime credential resolution based on partner configuration

- **Partner Visibility Control**
  - User-based partner access restrictions
  - "Visible to all" or "Specific users only" modes
  - Message list filtering based on partner visibility
  - Admin users bypass all visibility restrictions
  - Only applies to remote partners (local stations always visible)

- **Enhanced Security**
  - JWT-based authentication for WebUI
  - HttpOnly cookies for session management
  - PBKDF2 password hashing
  - Permission-based API endpoint protection
  - Read-only UI for viewer roles
  - Partner-level visibility controls

- **Modern Tech Stack**
  - **Backend**: Java 21, Jetty 12, Jakarta EE 10, JAX-RS (Jersey)
  - **Frontend**: React 18, React Router, TanStack Query, Vite
  - **Database**: PostgreSQL 15+ (replaced HSQLDB)
  - **Build**: Maven 3.9+

## 📋 Prerequisites

- **Java 21** or higher (JDK)
- **PostgreSQL 15** or higher
- **Maven 3.9** or higher
- **Node.js 18** or higher (for WebUI development)
- **npm** or **yarn** (for WebUI dependencies)

## 🔧 Installation

See [INSTALL.md](INSTALL.md) for detailed installation instructions.

Quick start:
```bash
# 1. Clone repository
git clone https://github.com/zc2tech/mend-as2.git
cd mend-as2

# 2. Set up PostgreSQL (see INSTALL.md)

# 3. Build project
mvn clean package -DskipTests

# 4. Run server
java -jar target/mend-as2-1.1.jar
```

Access WebUI at: http://localhost:8080/as2/webui/  
Default credentials: `admin` / `admin`

## 🎯 Usage

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
- **System** - Server configuration
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
- **User Preference → HTTP Authentication** - Configure HTTP auth credentials

**Keyboard Shortcuts:**
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

- ✅ JWT authentication with HttpOnly cookies
- ✅ PBKDF2 password hashing
- ✅ Role-based access control (RBAC)
- ✅ API permission enforcement
- ✅ Forced password change on first login
- ✅ Session timeout and refresh tokens
- ✅ Partner visibility controls
- ✅ User-specific HTTP authentication credentials

**Best Practices:**
1. Change default admin password immediately
2. Use strong passwords (min 6 chars)
3. Enable HTTPS in production
4. Assign minimum required permissions
5. Regular database backups
6. Configure partner visibility for sensitive partners
7. Use "User Preference" mode for HTTP auth when multiple users share partners

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

See migration guide in docs/ for details.

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/name`)
3. Commit changes (`git commit -m 'Add feature'`)
4. Push branch (`git push origin feature/name`)
5. Open Pull Request

## 📄 License

GNU General Public License v2.0 - see [LICENSE](LICENSE)

## 🙏 Acknowledgments

- **Original Project**: mendelson AS2 by mendelson-e-commerce GmbH
- **Contributors**: All who have helped improve this project

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/zc2tech/mend-as2/issues)
- **Email**: julian.xu@aliyun.com
- **Docs**: Check wiki for detailed guides

## 🗺️ Roadmap

- [x] Role-Based Access Control (RBAC)
- [x] HTTP Authentication preferences
- [x] Partner visibility controls
- [x] Message filtering by partner visibility
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

**HTTP Authentication Not Working**
- Verify partner is configured with "Use User Preference" mode
- Check user has set credentials in User Preference → HTTP Authentication
- For SwingUI: Check credentials are set for admin user (ID=1)

---

**Made with ❤️ by Julian Xu**
