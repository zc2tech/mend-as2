# README.md Updates - Summary

## Changes Made

Updated the main README.md to reflect recent features and improvements:

### 1. Enhanced Security Section
**Added:**
- System-wide TLS certificate management (separate from user certificates)
- User-specific TLS certificates for local station authentication

### 2. Database Support Section
**Added:**
- System-wide keystores use `user_id=-1` (not associated with any user)
- User-specific keystores use actual user IDs for proper ownership

### 3. Database Initialization Section
**Added:**
- Note about PostgreSQL requiring manual database creation
- Note about MySQL creating databases automatically
- Links to comprehensive documentation:
  - Database Initialization Guide (DATABASE_INITIALIZATION.md)
  - PostgreSQL Configuration (config/POSTGRESQL-CONFIG.md)
  - MySQL Configuration (config/MYSQL-CONFIG.md)
  - Backup & Restore Scripts (dev-scripts/README.md)

### 4. WebUI Access Section
**Updated System menu description:**
- Changed "Inb. AS2 Auth" to "TLS" subtab
- Noted: "System-wide HTTPS server certificates (permission-based access)"
- TLS tab is now positioned as second tab after HTTP Server Configuration

### 5. Backup & Restore Section
**Complete Rewrite:**
- **Before:** Simple pg_dump/psql commands only
- **After:** 
  - Automated scripts section (recommended approach)
  - Examples for both Linux/Mac and Windows
  - Feature list (auto-detection, cross-platform, safe restore)
  - Link to full documentation
  - Manual backup commands for both PostgreSQL and MySQL

**New Features Highlighted:**
```bash
# Automated backup (Linux/Mac)
cd dev-scripts
./backup.sh

# Automated restore
./restore.sh backup_20260416_120000.sql
```

### 6. Roadmap Section
**Added completed items:**
- ✅ System TLS certificates management (WebUI subtab)
- ✅ User-specific TLS certificates for local station auth
- ✅ System-wide keystore user_id migration (user_id=-1)
- ✅ MySQL configuration quick reference guide
- ✅ Automated backup/restore scripts (dev-scripts/)

### 7. Troubleshooting Section
**Added new entries:**

#### System TLS vs User TLS Certificates
- Explanation of System TLS (System → TLS tab)
  - System-wide HTTPS server certificates
  - Permission requirements (CERT_TLS_READ/WRITE)
  - Stored with user_id=-1
- Explanation of User TLS (My Sign/Crypt/TLS → TLS tab)
  - User-specific certificates
  - Stored with actual user ID
- Migration note: user_id=0 → user_id=-1 automatic migration

#### Backup & Restore
- Points users to automated scripts in dev-scripts/
- Notes automatic database type detection
- Links to detailed documentation

## Documentation Links Added

1. **DATABASE_INITIALIZATION.md** - How databases are created automatically
2. **config/POSTGRESQL-CONFIG.md** - PostgreSQL quick reference
3. **config/MYSQL-CONFIG.md** - MySQL quick reference  
4. **dev-scripts/README.md** - Backup/restore tools guide

## Key Improvements

### Before
- Limited backup documentation (PostgreSQL only)
- No mention of System TLS certificates
- Missing MySQL-specific information
- No automated backup tools mentioned

### After
- ✅ Comprehensive backup section with automated tools
- ✅ Clear distinction between System TLS and User TLS
- ✅ Full MySQL and PostgreSQL documentation links
- ✅ User_id=-1 migration documented
- ✅ Cross-platform backup/restore scripts highlighted
- ✅ Complete feature roadmap updates

## Impact on Users

### New Users
- Clear guidance on database initialization differences (PostgreSQL vs MySQL)
- Easy-to-find backup/restore tools
- Understanding of certificate management architecture

### Existing Users
- Migration information (user_id=0 → user_id=-1)
- New System TLS management capabilities
- Automated backup tools for production use

### Administrators
- Quick reference guides for both databases
- Production-ready backup strategies
- Security best practices with proper certificate separation

## Related Documentation

All referenced documentation exists and is comprehensive:

```
mend-as2/
├── README.md                                    ✅ Updated
├── DATABASE_INITIALIZATION.md                   ✅ Exists
├── config/
│   ├── POSTGRESQL-CONFIG.md                     ✅ Exists
│   └── MYSQL-CONFIG.md                          ✅ Exists
├── dev-scripts/
│   ├── README.md                                ✅ Exists
│   ├── backup.sh                                ✅ Exists
│   ├── backup.bat                               ✅ Exists
│   ├── restore.sh                               ✅ Exists
│   └── restore.bat                              ✅ Exists
└── SYSTEM_WIDE_USERID_MIGRATION_COMPLETED.md   ✅ Exists
```

## Summary

The README.md now:
- ✅ Reflects all recent architectural changes
- ✅ Documents new System TLS management
- ✅ Includes automated backup/restore tools
- ✅ Provides MySQL and PostgreSQL guidance
- ✅ Links to comprehensive documentation
- ✅ Maintains consistent formatting and style
- ✅ Helps users understand certificate ownership model
- ✅ Guides users to appropriate tools and docs

Users now have a complete, up-to-date reference for all major features!
