# GitHub Release Guide for Mend AS2

## Prerequisites

1. **GitHub Account** with access to the repository
2. **Git** installed locally
3. **Clean working directory** - commit all changes first

## Step 1: Prepare Your Repository

### Check Current Status

```bash
cd /Users/I572958/SAPDevelop/github/mend-as2
git status
```

### Commit All Changes

```bash
# Add all changes
git add .

# Commit with meaningful message
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

## Step 2: Create a Git Tag

Tags mark specific points in your repository's history as important (like releases).

```bash
# Create annotated tag
git tag -a v1.1.0 -m "Version 1.1.0 - Modern AS2 Server with RBAC and WebUI

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

### Option A: Via GitHub Web Interface (Recommended)

1. **Navigate to your repository** on GitHub
   ```
   https://github.com/zc2tech/mend-as2
   ```

2. **Click "Releases"** in the right sidebar

3. **Click "Create a new release"** or "Draft a new release"

4. **Fill in Release Information:**

   **Tag version:** `v1.1.0` (select the tag you just created)
   
   **Release title:** `Mend AS2 v1.1.0 - Modern AS2 Server with RBAC`
   
   **Description:**
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
   - Better concurrent access handling

   #### User Management
   - Create users via SwingUI and WebUI
   - Generate and email passwords
   - Role assignment interface
   - Password complexity enforcement
   - Account enable/disable

   ### 📦 Installation

   See [README.md](https://github.com/zc2tech/mend-as2/blob/main/README.md) for detailed instructions.

   **Quick Start:**
   ```bash
   # Download and extract
   # Set up PostgreSQL
   # Build: mvn clean package -DskipTests
   # Run: java -jar target/mend-as2-1.1.jar
   # Access: http://localhost:8080/as2/webui/
   ```

   ### 🔄 Migration from Mendelson AS2

   This is a major architectural change:
   - **Database**: Requires PostgreSQL setup
   - **Configuration**: New config files
   - **Users**: Re-create users with roles

   ### 📋 Requirements

   - Java 21+
   - PostgreSQL 15+
   - Maven 3.9+
   - Node.js 18+ (for development)

   ### 🐛 Known Issues

   - Partners, Certificates, System sections need read-only UI implementation
   - SwingUI role management needs message routing (partially implemented)

   ### 📝 Full Changelog

   **Backend:**
   - Upgraded to Java 21 and Jetty 12
   - Migrated from HSQLDB to PostgreSQL
   - Implemented full RBAC system with roles and permissions
   - Added JWT authentication filter
   - Created REST API for user management

   **Frontend:**
   - Built modern WebUI with React 18
   - Implemented permission-based routing
   - Added dashboard with quick access cards
   - Created user management interface
   - Added change password functionality

   **SwingUI:**
   - Added User Management dialog
   - Implemented role selection UI
   - Added keyboard shortcuts (Cmd/Ctrl+U, +N, +E, +D, +P, +R)
   - Table column sorting
   - Created custom user management icon

   **Database:**
   - Created comprehensive schema for RBAC
   - Added triggers for updated_at timestamps
   - Implemented proper foreign key constraints
   - Created default roles and permissions

   **Security:**
   - PBKDF2 password hashing (OWASP recommended)
   - JWT with 15-min access tokens, 7-day refresh tokens
   - HttpOnly cookies for XSS protection
   - Permission checks on all API endpoints
   - Forced password change on first login

   ### 🙏 Credits

   - **Original Project**: mendelson AS2 by mendelson-e-commerce GmbH
   - **Fork Author**: Julian Xu (julian.xu@aliyun.com)

   ### 📄 License

   GNU General Public License v2.0
   ```

5. **Attach Build Artifacts (Optional):**
   - Click "Attach binaries by dropping them here or selecting them"
   - Upload: `target/mend-as2-1.1.jar`
   - Upload: Database export files (if desired)

6. **Choose Pre-release or Release:**
   - ☑️ **Set as pre-release** if this is a beta/alpha
   - ☐ **Set as the latest release** (check this for stable releases)

7. **Click "Publish release"**

### Option B: Via GitHub CLI (gh)

```bash
# Install GitHub CLI if not already
# macOS: brew install gh
# Login: gh auth login

# Create release with binary
gh release create v1.1.0 \
  --title "Mend AS2 v1.1.0 - Modern AS2 Server with RBAC" \
  --notes-file RELEASE_NOTES.md \
  target/mend-as2-1.1.jar
```

## Step 4: Verify Release

1. Visit your repository's releases page
2. Verify the release appears correctly
3. Test downloading the attached binary
4. Check that the tag link works

## Step 5: Announce Release (Optional)

**Update GitHub README Badge:**
```markdown
![Latest Release](https://img.shields.io/github/v/release/zc2tech/mend-as2)
```

**Social Media/Email:**
```
🎉 Mend AS2 v1.1.0 is now available!

Modern AS2 server with full RBAC, React WebUI, and PostgreSQL database.

Download: https://github.com/zc2tech/mend-as2/releases/tag/v1.1.0
Docs: https://github.com/zc2tech/mend-as2

#AS2 #B2B #EDI #OpenSource
```

## Common Release Commands

### List all tags
```bash
git tag
```

### Delete a tag (if you made a mistake)
```bash
# Delete local tag
git tag -d v1.1.0

# Delete remote tag
git push origin --delete v1.1.0
```

### Create a lightweight tag (not recommended for releases)
```bash
git tag v1.1.0-beta
git push origin v1.1.0-beta
```

### View tag details
```bash
git show v1.1.0
```

## Semantic Versioning

Follow semantic versioning (MAJOR.MINOR.PATCH):

- **MAJOR** (1.x.x) - Incompatible API changes
- **MINOR** (x.1.x) - New features, backward compatible
- **PATCH** (x.x.1) - Bug fixes, backward compatible

**Examples:**
- `v1.0.0` - Initial release
- `v1.1.0` - Added RBAC (new features)
- `v1.1.1` - Fixed bug in user management
- `v2.0.0` - Breaking changes (e.g., API redesign)

## Release Checklist

- [ ] All code committed and pushed
- [ ] Tests pass (`mvn test`)
- [ ] Build succeeds (`mvn clean package`)
- [ ] Version number updated in pom.xml
- [ ] CHANGELOG.md updated
- [ ] README.md up to date
- [ ] Documentation complete
- [ ] Database migrations tested
- [ ] Git tag created and pushed
- [ ] GitHub release created
- [ ] Release notes comprehensive
- [ ] Binary artifacts attached
- [ ] Release announced

## Next Release Preparation

After releasing v1.1.0, prepare for v1.2.0:

```bash
# Create a new branch for development
git checkout -b develop

# Update version in pom.xml to 1.2.0-SNAPSHOT
# Continue development...
```

## Troubleshooting

**Tag already exists:**
```bash
# Delete and recreate
git tag -d v1.1.0
git push origin --delete v1.1.0
git tag -a v1.1.0 -m "New message"
git push origin v1.1.0
```

**Binary too large for GitHub:**
- GitHub has a 2GB file size limit
- Consider using GitHub Releases API for larger files
- Or host large files elsewhere and link in release notes

**Permission denied:**
```bash
# Ensure you have push access
git remote -v
# If SSH: ensure SSH key is configured
# If HTTPS: use personal access token
```
