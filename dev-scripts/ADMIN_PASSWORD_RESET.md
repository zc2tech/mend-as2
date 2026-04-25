# Admin Password Reset Tool

## Overview

This tool allows you to reset the password for the 'admin' user when they have forgotten their password and are locked out of the system.

**🎯 Smart Environment Detection**: The script automatically detects whether you're running in:
- **Development environment** (with `pom.xml`) - Uses Maven
- **Production environment** (deployed installation) - Uses JAR files directly

No configuration needed - just run the script!

## When to Use

Use this tool when:
- The admin user has forgotten their password
- Admin is locked out and cannot access WebUI or SwingUI
- No other user can reset the admin password

## Prerequisites

⚠️ **IMPORTANT**: The AS2 server **MUST** be stopped before running this tool.

Running this tool while the server is active may cause database corruption or conflicts.

## Usage Methods

### Method 1: Using Helper Scripts (Recommended)

#### On Linux/Mac:
```bash
./dev-scripts/reset-admin-password.sh
```

#### On Windows:
```batch
dev-scripts\reset-admin-password.bat
```

The helper scripts will:
1. **Detect environment** (development or production)
2. **Navigate to the correct directory** automatically
3. **Check if the server is running**
4. **Prompt you to stop the server** if needed (Linux/Mac only)
5. **Use the appropriate method** (Maven or JAR)
6. **Guide you through** the password reset process

### Method 2: Manual Java Invocation (Advanced)

#### In Development:

```bash
# Compile first
mvn compile -DskipTests

# Run with classpath
java -cp "target/classes:target/lib/*:target/lib-prepackaged/*" \
  de.mendelson.comm.as2.usermanagement.AdminPasswordResetTool
```

#### In Production:

```bash
# Build the classpath with all dependencies
java -cp "mend-as2-*.jar:lib/*" \
  de.mendelson.comm.as2.usermanagement.AdminPasswordResetTool
```

**Note**: The helper scripts (Method 1) handle all classpath building automatically.
```

## Step-by-Step Instructions

1. **Stop the AS2 Server**
   ```bash
   # Find the server process
   ps aux | grep mend-as2
   
   # Stop the server (use the PID from above)
   kill <PID>
   ```

2. **Run the Password Reset Tool**
   ```bash
   ./dev-scripts/reset-admin-password.sh
   ```

3. **Follow the Prompts**
   - Confirm you want to continue
   - Enter new password (minimum 8 characters)
   - Confirm the new password
   
4. **Verify Success**
   You should see:
   ```
   SUCCESS: Admin password has been reset!
   
   You can now login with:
     Username: admin
     Password: (the password you just set)
   ```

5. **Start the Server**
   ```bash
   java -jar target/mend-as2.jar
   ```

6. **Login and Change Password**
   - Login to WebUI or SwingUI with username 'admin' and your new password
   - You will be prompted to change the password on first login
   - Set a secure password and remember it!

## Password Requirements

- Minimum length: 8 characters
- Must be confirmed (entered twice)
- Should be strong and memorable

**Tip**: Use a password manager to generate and store secure passwords.

## Security Notes

- This tool requires direct database access
- Only system administrators with server access can run this tool
- The tool sets `must_change_password = TRUE`, forcing a password change on next login
- Always use strong, unique passwords for the admin account

## Troubleshooting

### Error: "Admin user not found in database"
**Solution**: This tool only works with existing 'admin' users. If the admin user was deleted, you need to recreate it using SQL:

```sql
INSERT INTO webui_users (username, password_hash, full_name, enabled, must_change_password) 
VALUES ('admin', '75000#efbfbd5207efbfbd0159efbfbd4befbfbd2befbfbdefbfbd1f22277e#13fbcaadc6706ff58a7666b6fa82dbed', 
        'System Administrator', TRUE, TRUE);
```
(Password will be 'admin' - change it immediately!)

### Error: "Database connection failed"
**Solution**: 
- Ensure you're running from the project root directory
- Check that the database files exist in the `config/` directory
- Verify database is not locked by another process

### Error: "Server appears to be running"
**Solution**: 
- Stop the AS2 server completely before running the tool
- Check for any remaining processes: `ps aux | grep mend-as2`
- Kill any running server processes

### Password not masked during input (shows plain text)
**Cause**: Running in an IDE or environment without a Console object
**Impact**: Low security risk for this specific use case
**Solution**: Run from a real terminal/command prompt for password masking

## Alternative: Direct Database Method

If the tool doesn't work, you can reset the password directly in the database:

```bash
# Connect to the database (H2 example)
java -cp target/mend-as2.jar org.h2.tools.Shell

# Run SQL update (replace YOUR_NEW_PASSWORD_HASH)
UPDATE webui_users 
SET password_hash = 'YOUR_NEW_PASSWORD_HASH', 
    must_change_password = TRUE 
WHERE username = 'admin';
```

To generate a password hash, you can temporarily modify the tool to print the hash without updating the database.

## Support

For issues or questions:
- Check server logs in `log/` directory
- Review database connection settings
- Ensure proper permissions to access database files

## Security Best Practices

After resetting the password:

1. ✅ Login immediately and change to a strong password
2. ✅ Set up a recovery email for the admin account
3. ✅ Consider enabling two-factor authentication (if available)
4. ✅ Document the recovery procedure for your team
5. ✅ Store the new password in a secure password manager

## Future Enhancements

Planned improvements:
- Email-based password reset (no server access needed)
- Recovery codes during initial setup
- Multi-factor authentication
- Account lockout protection

---

**Version**: 1.0  
**Last Updated**: 2026-04-25  
**Author**: Julian Xu
