# Email Password Feature - Remaining Implementation Steps

## Completed:
1. ✅ Added `must_change_password` column to database schema
2. ✅ Added `mustChangePassword` field to WebUIUser model  
3. ✅ Created PasswordGenerator utility class
4. ✅ Created UserNotificationMailer utility class
5. ✅ Added `generateAndEmailPassword` flag to UserCreateRequest

## TODO - Critical Files to Modify:

### 1. UserManagementAccessDB.java
**Method: getAllUsers(), getUser(), getUserByUsername(), createUser()**
- Add `must_change_password` to SELECT queries
- Add `must_change_password` to INSERT statement in createUser()
- Set result: `user.setMustChangePassword(rs.getBoolean("must_change_password"))`

### 2. AS2ServerProcessing.java  
**Method: processUserCreateRequest()**
```java
public UserCreateResponse processUserCreateRequest(UserCreateRequest request) {
    UserCreateResponse response = new UserCreateResponse(request);
    try {
        String password;
        if (request.isGenerateAndEmailPassword()) {
            // Generate random password
            password = PasswordGenerator.generatePassword();
            // Set must change password flag for non-admin users
            if (!"admin".equals(request.getUser().getUsername())) {
                request.getUser().setMustChangePassword(true);
            }
        } else {
            password = request.getPlainPassword();
        }
        
        String passwordHash = User.cryptPassword(password.toCharArray());
        request.getUser().setPasswordHash(passwordHash);
        
        UserManagementAccessDB userMgmt = new UserManagementAccessDB(this.dbDriverManager, this.logger);
        int userId = userMgmt.createUser(request.getUser());
        
        // Send email if requested
        if (request.isGenerateAndEmailPassword()) {
            try {
                NotificationAccessDB notificationAccess = new NotificationAccessDBImplAS2(this.dbDriverManager);
                NotificationData notificationData = notificationAccess.getNotificationData();
                
                // Get server URL from preferences or use default
                String serverUrl = "https://yourserver.com/as2"; // TODO: Get from config
                
                UserNotificationMailer.sendUserCreationEmail(request.getUser(), password, 
                                                            notificationData, serverUrl);
            } catch (Exception emailEx) {
                this.logger.warning("User created but email failed: " + emailEx.getMessage());
                response.setException(new Exception("User created but email notification failed: " + emailEx.getMessage()));
            }
        }
    } catch (Exception e) {
        response.setException(e);
        this.logger.log(Level.SEVERE, "Error processing user create request", e);
    }
    return response;
}
```

### 3. JDialogEditUser.java (SwingUI)
**Add checkbox for "Generate and email password" (create mode only)**
```java
private JCheckBox checkGeneratePassword;

// In initComponents(), after password fields for new users:
if (editingUser == null) {
    // ... existing password fields ...
    
    row++;
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.gridwidth = 2;
    checkGeneratePassword = new JCheckBox("Generate and email password to user");
    checkGeneratePassword.addItemListener(e -> {
        boolean generatePassword = checkGeneratePassword.isSelected();
        textPassword.setEnabled(!generatePassword);
        textConfirmPassword.setEnabled(!generatePassword);
        if (generatePassword) {
            textPassword.setText("");
            textConfirmPassword.setText("");
        }
    });
    formPanel.add(checkGeneratePassword, gbc);
}

// In createUser():
if (checkGeneratePassword != null && checkGeneratePassword.isSelected()) {
    request.setGenerateAndEmailPassword(true);
    // Don't validate password fields
} else {
    // Existing password validation
}
```

### 4. AuthenticationResource.java (WebUI JWT Login)
**Check must_change_password flag on login**
```java
// After successful password validation:
if (user.isMustChangePassword()) {
    // Return special response indicating password change required
    return Response.ok()
        .entity(Map.of(
            "token", token,
            "mustChangePassword", true,
            "message", "You must change your password before continuing"
        ))
        .cookie(jwtCookie)
        .build();
}
```

### 5. WebUI React - Login Component
**Handle mustChangePassword response**
- Redirect to password change page after login if `mustChangePassword` is true
- Create ChangePasswordPage component
- Update AuthenticationResource to clear flag after password change

### 6. Database Migration
**Add column to existing installations**
```sql
-- Migration script for existing databases
ALTER TABLE webui_users ADD COLUMN IF NOT EXISTS must_change_password BOOLEAN DEFAULT FALSE;
UPDATE webui_users SET must_change_password = FALSE WHERE must_change_password IS NULL;
```

## UI Text Suggestions:

**Checkbox label**: "Generate and email password to user"
**Tooltip**: "A secure random password will be generated and sent to the user's email address. User must change password on first login."
**Email validation**: "Email address is required when generating password"
**Success message**: "User created successfully. Password has been sent to {email}"

## Testing Checklist:
- [ ] Create user with manual password (existing flow)
- [ ] Create user with generated password + valid email
- [ ] Verify email received with correct credentials
- [ ] Login with emailed password
- [ ] Verify forced password change on first login
- [ ] Change password successfully
- [ ] Login again with new password (no force change)
- [ ] Test with missing email address (should show error)
- [ ] Test with invalid SMTP config (should show error)

## Security Notes:
- Generated passwords are 12 characters with uppercase, lowercase, digits, and special chars
- Email uses TLS/STARTTLS based on notification config
- Plain password never stored in database
- must_change_password flag cleared after successful password change
- Admin user exempt from forced password change
