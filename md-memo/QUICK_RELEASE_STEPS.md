# Quick Release Steps for Mend AS2 v1.1.0

## Prerequisites
- All changes are committed and tested
- You have push access to the GitHub repository
- Git is configured with your credentials

## Step 1: Commit and Push All Changes

```bash
cd /Users/I572958/SAPDevelop/github/mend-as2

# Check status
git status

# Add all changes
git add .

# Commit with message
git commit -m "Prepare for v1.1.0 release

- Added complete RBAC system with 6 predefined roles
- Implemented WebUI with React 18 and modern design
- Migrated from HSQLDB to PostgreSQL
- Added JWT authentication for WebUI
- Implemented user management for both SwingUI and WebUI
- Added forced password change on first login
- Implemented permission-based UI protection
- Added comprehensive README and documentation"

# Push to remote
git push origin main
```

## Step 2: Create and Push Git Tag

```bash
# Create annotated tag
git tag -a v1.1.0 -m "Version 1.1.0 - Modern AS2 Server with RBAC

Major Features:
- Role-Based Access Control (RBAC)
- Modern React WebUI
- PostgreSQL database
- JWT authentication
- Enhanced user management
- Permission-based API protection"

# Push tag to remote
git push origin v1.1.0
```

## Step 3: Create GitHub Release

1. **Open your browser** and navigate to:
   ```
   https://github.com/zc2tech/mend-as2/releases
   ```

2. **Click** "Draft a new release" or "Create a new release"

3. **Fill in the form:**

   **Choose a tag:** Select `v1.1.0` from dropdown
   
   **Release title:** 
   ```
   Mend AS2 v1.1.0 - Modern AS2 Server with RBAC
   ```
   
   **Description:** Copy and paste the following:

   ```markdown
   ## 🎉 Major Release: Modern AS2 Server with Full RBAC

   This release transforms mendelson AS2 into a modern, enterprise-ready AS2 server with comprehensive role-based access control and a beautiful web interface.

   ### ✨ Key Features

   #### Role-Based Access Control (RBAC)
   - 6 pre-defined roles (Admin, Partner Manager, Certificate Manager, Message Operator, System Manager, Viewer)
   - Granular permissions (READ/WRITE per module)
   - User management via both SwingUI and WebUI
   - Permission-based API endpoint protection

   #### Modern WebUI
   - React 18 with responsive design
   - Permission-based dashboard
   - Real-time updates with TanStack Query
   - Dark-mode friendly interface
   - Mobile-responsive layout

   #### Enhanced Security
   - JWT authentication with HttpOnly cookies
   - PBKDF2 password hashing
   - Forced password change on first login
   - Session management with refresh tokens
   - Read-only UI for viewer roles

   #### Database Migration
   - PostgreSQL 15+ (replaced HSQLDB)
   - Separate config and runtime databases
   - Improved performance and scalability

   #### User Management
   - Create users via SwingUI and WebUI
   - Generate and email passwords
   - Role assignment interface
   - Password complexity enforcement
   - Account enable/disable

   ### 📦 Installation

   **Requirements:**
   - Java 21+
   - PostgreSQL 15+
   - Maven 3.9+

   **Quick Start:**
   ```bash
   # 1. Clone repository
   git clone https://github.com/zc2tech/mend-as2.git
   cd mend-as2

   # 2. Set up PostgreSQL databases
   # See README.md for database setup instructions

   # 3. Build project
   mvn clean package -DskipTests

   # 4. Run server
   java -jar target/mend-as2-1.1.jar

   # 5. Access WebUI
   # http://localhost:8080/as2/webui/
   # Default: admin / admin
   ```

   ### 📖 Documentation

   - [README.md](https://github.com/zc2tech/mend-as2/blob/main/README.md) - Complete documentation
   - [GITHUB_RELEASE_GUIDE.md](https://github.com/zc2tech/mend-as2/blob/main/GITHUB_RELEASE_GUIDE.md) - Release process
   - [READ_ONLY_UI_PATTERN.md](https://github.com/zc2tech/mend-as2/blob/main/READ_ONLY_UI_PATTERN.md) - UI pattern guide

   ### 🔄 Migration from Mendelson AS2

   This is a major architectural change:
   - **Database**: Requires PostgreSQL setup (was HSQLDB)
   - **Configuration**: New config files required
   - **Users**: Re-create users with roles
   - **WebUI**: New React-based interface (was Vaadin)

   See README.md for detailed migration guide.

   ### 🐛 Known Issues

   - Partners, Certificates, System sections need read-only UI implementation
   - SwingUI role management message routing partially implemented

   ### 📝 Full Changelog

   **Backend:**
   - ✅ Upgraded to Java 21 and Jetty 12
   - ✅ Migrated from HSQLDB to PostgreSQL
   - ✅ Implemented full RBAC system
   - ✅ Added JWT authentication filter
   - ✅ Created REST API for user management

   **Frontend:**
   - ✅ Built modern WebUI with React 18
   - ✅ Implemented permission-based routing
   - ✅ Added dashboard with quick access cards
   - ✅ Created user management interface
   - ✅ Added change password functionality

   **SwingUI:**
   - ✅ Added User Management dialog with role selection
   - ✅ Added keyboard shortcuts (Cmd/Ctrl+U, +N, +E, +D, +P, +R)
   - ✅ Table column sorting
   - ✅ Created custom user management icon

   **Security:**
   - ✅ PBKDF2 password hashing
   - ✅ JWT with HttpOnly cookies
   - ✅ Permission checks on all API endpoints
   - ✅ Forced password change on first login
   - ✅ Read-only UI for Messages (partial)

   ### 🙏 Credits

   - **Original Project**: mendelson AS2 by mendelson-e-commerce GmbH
   - **Fork Author**: Julian Xu (julian.xu@aliyun.com)

   ### 📄 License

   GNU General Public License v2.0
   ```

4. **Attach binary file:**
   - Click "Attach binaries by dropping them here or selecting them"
   - Upload file: `/Users/I572958/SAPDevelop/github/mend-as2/target/mend-as2-1.1.jar`

5. **Set release options:**
   - ☐ Set as a pre-release (leave unchecked for stable release)
   - ☑ Set as the latest release (check this)

6. **Click** "Publish release"

## Step 4: Verify Release

1. Visit: https://github.com/zc2tech/mend-as2/releases
2. Verify v1.1.0 appears
3. Test downloading the JAR file
4. Check that all links work

## Done! 🎉

Your release is now live at:
```
https://github.com/zc2tech/mend-as2/releases/tag/v1.1.0
```

## Optional: Clean Up

```bash
# Remove old database exports
rm target/db-exports/*.sql

# Keep only the log folder exports
ls -lh log/*.sql
```

## Next Steps

Consider announcing your release:
- Update repository description on GitHub
- Share on LinkedIn/Twitter
- Post in AS2/EDI communities
- Email announcement to interested parties
