# User Management System - Testing Guide

## Quick Start

**Default Credentials**: 
- Username: `admin`
- Password: `admin`

⚠️ **Important**: Change the default password after first login!

---

## Pre-Testing Verification

Before testing, ensure:
- [ ] PostgreSQL database `as2_db_config` exists and is accessible
- [ ] Server has been restarted after deployment
- [ ] Database tables are created (check with SQL client)

**Verify Tables Created**:
```sql
\dt webui_*
-- Should show: webui_users, webui_roles, webui_permissions, webui_user_roles, webui_role_permissions
```

**Verify Default Data**:
```sql
SELECT * FROM webui_users;  -- Should show 'admin' user
SELECT * FROM webui_roles;  -- Should show 3 roles: ADMIN, USER, VIEWER
SELECT * FROM webui_permissions;  -- Should show 9 permissions
```

---

## Test Plan 1: WebUI User Management

### 1.1 Login & Navigation
- [ ] Navigate to: `http://localhost:8080/as2/admin/login`
- [ ] Login with: `admin` / `admin`
- [ ] Should redirect to dashboard
- [ ] Click "Users" link in navigation
- [ ] Should see Users management page with 3 tabs

### 1.2 View Users
- [ ] Users tab should display a table
- [ ] Should see admin user listed
- [ ] Table columns: Username, Full Name, Email, Roles, Enabled, Last Login, Actions

### 1.3 Create User
- [ ] Click "Create User" button
- [ ] Modal dialog should open
- [ ] Fill in:
  - Username: `testuser`
  - Password: `Test1234!`
  - Confirm Password: `Test1234!`
  - Email: `test@example.com`
  - Full Name: `Test User`
  - Enabled: ✓
  - Roles: Select "USER"
- [ ] Click "Create"
- [ ] Success message should appear
- [ ] Modal should close
- [ ] New user should appear in table

### 1.4 Edit User
- [ ] Click "Edit" button on testuser
- [ ] Modal should open with existing data
- [ ] Change Full Name to: `Test User Updated`
- [ ] Change Email to: `testupdated@example.com`
- [ ] Add "VIEWER" role (should have both USER and VIEWER)
- [ ] Click "Update"
- [ ] Success message should appear
- [ ] Changes should be reflected in table

### 1.5 Change Password
- [ ] Click "Change Password" on testuser
- [ ] Modal should open
- [ ] Fill in:
  - New Password: `NewPassword123!`
  - Confirm Password: `NewPassword123!`
- [ ] Click "Change Password"
- [ ] Success message should appear
- [ ] Verify: Logout and login as testuser with new password

### 1.6 Delete User
- [ ] Login back as admin
- [ ] Go to Users page
- [ ] Click "Delete" on testuser
- [ ] Confirmation dialog should appear
- [ ] Click "Yes" to confirm
- [ ] User should be removed from table

### 1.7 View Roles Tab
- [ ] Click "Roles" tab
- [ ] Should see 3 roles: ADMIN, USER, VIEWER
- [ ] Each role should show:
  - Role name
  - Description
  - Number of permissions
  - Number of users
- [ ] Click expand icon on ADMIN role
- [ ] Should show all 9 permissions
- [ ] Click expand icon on USER role
- [ ] Should show 7 permissions (no USER_MANAGE or SYSTEM_WRITE)
- [ ] Click expand icon on VIEWER role
- [ ] Should show 4 permissions (only READ permissions)

### 1.8 View Permissions Tab
- [ ] Click "Permissions" tab
- [ ] Should see all 9 permissions grouped by category
- [ ] Categories: Partners, Certificates, Messages, System, Administration
- [ ] Each permission should show name and description

---

## Test Plan 2: SwingUI User Management

### 2.1 Launch & Navigation
- [ ] Start AS2 desktop client
- [ ] Should NOT see any login prompt (SwingUI has no authentication)
- [ ] Go to: File → User Management (or press Ctrl+U / Cmd+U)
- [ ] Dialog should open showing user table

### 2.2 View Users
- [ ] Table should display: Username, Full Name, Email, Enabled, Last Login
- [ ] Should see admin user
- [ ] Buttons should be: Create User, Edit User, Delete User, Change Password, Refresh, Close
- [ ] Edit, Delete, Change Password should be disabled (no selection)

### 2.3 Create User
- [ ] Click "Create User"
- [ ] Dialog should open
- [ ] Fill in:
  - Username: `swinguser`
  - Password: `Swing1234!`
  - Confirm Password: `Swing1234!`
  - Email: `swing@example.com`
  - Full Name: `Swing Test User`
  - Enabled: ✓
- [ ] Click "Create"
- [ ] Success dialog should appear
- [ ] Click OK
- [ ] New user should appear in table

### 2.4 Edit User
- [ ] Select swinguser from table
- [ ] Edit, Delete, Change Password buttons should become enabled
- [ ] Click "Edit User"
- [ ] Dialog should open with existing data
- [ ] Change Full Name to: `Swing User Updated`
- [ ] Change Email to: `swingupdated@example.com`
- [ ] Uncheck "Enabled" checkbox
- [ ] Click "Update"
- [ ] Success dialog should appear
- [ ] Changes should be reflected in table

### 2.5 Change Password
- [ ] Select swinguser from table
- [ ] Click "Change Password"
- [ ] Dialog should open
- [ ] Fill in:
  - New Password: `NewSwing456!`
  - Confirm Password: `NewSwing456!`
- [ ] Click "Change Password"
- [ ] Success dialog should appear

### 2.6 Double-Click Edit
- [ ] Double-click on swinguser row
- [ ] Edit dialog should open
- [ ] Verify it shows current data
- [ ] Click "Cancel" to close

### 2.7 Refresh
- [ ] Open WebUI in browser
- [ ] Create a new user via WebUI: `webuser`
- [ ] Go back to SwingUI
- [ ] Click "Refresh" button
- [ ] New user `webuser` should appear in SwingUI table
- [ ] ✓ Confirms data sync between WebUI and SwingUI

### 2.8 Delete User
- [ ] Select swinguser from table
- [ ] Click "Delete User"
- [ ] Confirmation dialog should appear
- [ ] Click "Yes"
- [ ] Success dialog should appear
- [ ] User should be removed from table
- [ ] Also delete webuser to clean up

---

## Test Plan 3: Permission Enforcement

### 3.1 Create Limited User
- [ ] Login to WebUI as admin
- [ ] Create user:
  - Username: `viewer`
  - Password: `Viewer123!`
  - Role: VIEWER only
- [ ] Logout

### 3.2 Test VIEWER Permissions
- [ ] Login as: `viewer` / `Viewer123!`
- [ ] Navigate to dashboard
- [ ] Try accessing different sections:

**Should SUCCEED (200 OK)**:
- [ ] GET /api/v1/partners - View partners
- [ ] GET /api/v1/certificates - View certificates
- [ ] GET /api/v1/messages - View messages
- [ ] GET /api/v1/system - View system info

**Should FAIL (403 Forbidden)**:
- [ ] POST /api/v1/partners - Create partner
- [ ] PUT /api/v1/partners/{id} - Update partner
- [ ] DELETE /api/v1/partners/{id} - Delete partner
- [ ] POST /api/v1/certificates - Import certificate
- [ ] POST /api/v1/messages - Send message
- [ ] POST /api/v1/system/settings - Update system settings
- [ ] GET /api/v1/users - View users (needs USER_MANAGE)

### 3.3 Create Standard User
- [ ] Logout, login as admin
- [ ] Create user:
  - Username: `standarduser`
  - Password: `Standard123!`
  - Role: USER
- [ ] Logout

### 3.4 Test USER Permissions
- [ ] Login as: `standarduser` / `Standard123!`

**Should SUCCEED**:
- [ ] All read operations (same as VIEWER)
- [ ] POST /api/v1/partners - Create partner
- [ ] PUT /api/v1/partners/{id} - Update partner
- [ ] DELETE /api/v1/partners/{id} - Delete partner
- [ ] POST /api/v1/certificates - Import certificate
- [ ] POST /api/v1/messages - Send message
- [ ] GET /api/v1/system - View system info

**Should FAIL (403 Forbidden)**:
- [ ] POST /api/v1/system/settings - Update system settings (needs SYSTEM_WRITE)
- [ ] GET /api/v1/users - View users (needs USER_MANAGE)
- [ ] POST /api/v1/users - Create user (needs USER_MANAGE)

### 3.5 Test ADMIN Permissions
- [ ] Logout, login as admin
- [ ] Should have access to ALL endpoints
- [ ] Including user management endpoints
- [ ] Including system settings modification

---

## Test Plan 4: Database Verification

### 4.1 Password Storage
```sql
SELECT username, password_hash FROM webui_users;
```
- [ ] Password hashes should be in format: `75000#...#...`
- [ ] Should NOT see plain text passwords
- [ ] Each hash should be unique (different salts)

### 4.2 Role Assignments
```sql
SELECT u.username, r.name
FROM webui_users u
JOIN webui_user_roles ur ON u.id = ur.user_id
JOIN webui_roles r ON ur.role_id = r.id;
```
- [ ] admin should have ADMIN role
- [ ] viewer should have VIEWER role
- [ ] standarduser should have USER role

### 4.3 Permission Assignments
```sql
SELECT r.name AS role, COUNT(p.id) AS permission_count
FROM webui_roles r
JOIN webui_role_permissions rp ON r.id = rp.role_id
JOIN webui_permissions p ON rp.permission_id = p.id
GROUP BY r.name;
```
- [ ] ADMIN should have 9 permissions
- [ ] USER should have 7 permissions
- [ ] VIEWER should have 4 permissions

### 4.4 Updated Timestamp Trigger
```sql
-- Update a user
UPDATE webui_users SET full_name = 'Test Trigger' WHERE username = 'admin';

-- Check updated_at
SELECT username, updated_at FROM webui_users WHERE username = 'admin';
```
- [ ] updated_at should be current timestamp
- [ ] Should automatically update on any UPDATE operation

### 4.5 Cascading Deletes
```sql
-- Create test user and assign role
INSERT INTO webui_users (username, password_hash, full_name, enabled) 
VALUES ('deletetest', '75000#test#test', 'Delete Test', TRUE);

INSERT INTO webui_user_roles (user_id, role_id)
SELECT u.id, r.id FROM webui_users u, webui_roles r 
WHERE u.username = 'deletetest' AND r.name = 'USER';

-- Delete user
DELETE FROM webui_users WHERE username = 'deletetest';

-- Check that user_roles entry was deleted
SELECT COUNT(*) FROM webui_user_roles ur
JOIN webui_users u ON ur.user_id = u.id
WHERE u.username = 'deletetest';
```
- [ ] Should return 0 (role assignment was cascade deleted)

---

## Test Plan 5: Security Testing

### 5.1 JWT Token
- [ ] Login to WebUI
- [ ] Open browser DevTools → Application → Cookies
- [ ] Should see `jwt_token` cookie
- [ ] Properties:
  - HttpOnly: true
  - Path: /as2
  - SameSite: Lax or Strict

### 5.2 Unauthorized Access
- [ ] Logout or open incognito window
- [ ] Try accessing: `http://localhost:8080/as2/admin/users`
- [ ] Should redirect to login page
- [ ] Try API: `curl http://localhost:8080/as2/api/v1/users`
- [ ] Should return 401 Unauthorized

### 5.3 Password Validation
- [ ] Try creating user with empty password
- [ ] Should show validation error
- [ ] Try mismatched passwords (password ≠ confirm)
- [ ] Should show validation error
- [ ] Try changing password to empty
- [ ] Should show validation error

### 5.4 Username Uniqueness
- [ ] Create user: `duplicatetest`
- [ ] Try creating another user: `duplicatetest`
- [ ] Should show error: "Username already exists" or similar

### 5.5 Session Timeout
- [ ] Login to WebUI
- [ ] Wait 15+ minutes (JWT expiry time)
- [ ] Try accessing protected page
- [ ] Should redirect to login (token expired)

---

## Test Plan 6: Cross-Interface Consistency

### 6.1 Create in WebUI, View in SwingUI
- [ ] Login to WebUI
- [ ] Create user: `webtoswing`
- [ ] Open SwingUI
- [ ] Open User Management dialog
- [ ] User `webtoswing` should be visible
- [ ] Data should match (username, email, full name, enabled status)

### 6.2 Create in SwingUI, View in WebUI
- [ ] Open SwingUI User Management
- [ ] Create user: `swingtoweb`
- [ ] Open WebUI Users page
- [ ] Click Refresh or reload page
- [ ] User `swingtoweb` should be visible
- [ ] Data should match

### 6.3 Edit in WebUI, Reflect in SwingUI
- [ ] Edit user in WebUI
- [ ] Refresh SwingUI User Management
- [ ] Changes should be visible

### 6.4 Delete in SwingUI, Reflect in WebUI
- [ ] Delete user in SwingUI
- [ ] Refresh WebUI Users page
- [ ] User should be gone

---

## Common Issues & Troubleshooting

### Issue: Tables not created
**Solution**: Run CREATE.sql manually
```sql
\i /path/to/sqlscript/config/CREATE.sql
```

### Issue: No default data
**Solution**: Run data.sql manually
```sql
\i /path/to/sqlscript/config/data.sql
```

### Issue: Cannot login with admin/admin
**Check**:
1. Database has admin user: `SELECT * FROM webui_users WHERE username='admin';`
2. Password hash is correct (starts with `75000#`)
3. User is enabled: `enabled = TRUE`

**Reset admin password**:
```sql
UPDATE webui_users 
SET password_hash = '75000#efbfbd5207efbfbd0159efbfbd4befbfbd2befbfbdefbfbd1f22277e#13fbcaadc6706ff58a7666b6fa82dbed' 
WHERE username = 'admin';
```

### Issue: 403 Forbidden on all endpoints
**Check**:
1. User has at least one role assigned
2. Role has appropriate permissions
3. JWT token is valid (check browser cookies)

**Verify permissions**:
```sql
SELECT u.username, r.name AS role, p.name AS permission
FROM webui_users u
JOIN webui_user_roles ur ON u.id = ur.user_id
JOIN webui_roles r ON ur.role_id = r.id
JOIN webui_role_permissions rp ON r.id = rp.role_id
JOIN webui_permissions p ON rp.permission_id = p.id
WHERE u.username = 'yourusername';
```

### Issue: SwingUI menu item not visible
**Check**:
1. Server restarted after code deployment
2. AS2Gui.java compiled correctly
3. Try keyboard shortcut: Ctrl+U (Windows/Linux) or Cmd+U (Mac)

### Issue: updated_at not updating
**Check trigger exists**:
```sql
SELECT tgname FROM pg_trigger WHERE tgname = 'trigger_update_webui_users_updated_at';
```

**Recreate trigger**:
```sql
DROP TRIGGER IF EXISTS trigger_update_webui_users_updated_at ON webui_users;
CREATE TRIGGER trigger_update_webui_users_updated_at
    BEFORE UPDATE ON webui_users
    FOR EACH ROW
    EXECUTE FUNCTION update_webui_users_updated_at();
```

---

## Success Criteria

✅ All test cases pass
✅ No console errors in browser
✅ No exceptions in server logs
✅ Data consistency between WebUI and SwingUI
✅ Permission enforcement works correctly
✅ Password changes work
✅ Database trigger functions correctly

---

## Feedback Template

Please provide feedback on:

1. **Functionality**: Does everything work as expected?
2. **Usability**: Is the interface intuitive?
3. **Performance**: Any slow operations?
4. **Bugs**: Any errors or unexpected behavior?
5. **Suggestions**: Features or improvements?

**Format**:
```
Test Case: [e.g., WebUI - Create User]
Status: [PASS / FAIL]
Notes: [Any issues or observations]
```

---

**Testing Start Date**: _____________
**Tester Name**: _____________
**Environment**: _____________

Good luck with testing! 🚀
