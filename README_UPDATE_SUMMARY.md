# README.md Update Summary

## Date
2026-04-06

## Changes Made

Updated README.md to document all recent features and enhancements added to the project.

## Sections Updated

### 1. Modern Enhancements Section
**Added:**
- HTTP Authentication feature with three modes
- Partner Visibility Control
- Enhanced security features

### 2. Usage Section
**Added new subsections:**
- **Partner Visibility Control**
  - How to configure visibility
  - Effects on UI and filtering
  - Admin bypass behavior

- **HTTP Authentication Preferences**
  - WebUI setup instructions
  - SwingUI setup instructions
  - Partner configuration modes

**Updated:**
- WebUI Available Sections
- SwingUI Menu Navigation
- Added HTTP auth and visibility context

### 3. REST API Section
**Added endpoints:**
- `GET /partners?visibleToUser={id}` - Partner visibility filtering
- `GET /user-preferences/http-auth` - Get HTTP auth preferences
- `POST /user-preferences/http-auth` - Save HTTP auth credentials
- Note about message filtering by partner visibility

### 4. Architecture Section
**Updated directory structure:**
- Added partner management folder
- Added preferences folder
- Expanded usermanagement description
- Added WebUI feature folders

### 5. Security Section
**Added:**
- Partner visibility controls
- User-specific HTTP authentication credentials
- Additional best practices for visibility and HTTP auth

### 6. Migration Section
**Added key differences:**
- HTTP Authentication preferences
- Partner visibility controls
- User-specific preferences

### 7. Roadmap Section
**Marked as completed:**
- ✅ Role-Based Access Control (RBAC)
- ✅ HTTP Authentication preferences
- ✅ Partner visibility controls
- ✅ Message filtering by partner visibility

**Added future items:**
- OAuth2 support for HTTP authentication

### 8. Troubleshooting Section
**Added new troubleshooting entries:**
- Partner Not Visible in Message Send
- HTTP Authentication Not Working

## Key Features Documented

### HTTP Authentication
- Three authentication modes (None, Always Use, Use User Preference)
- User-specific credential management
- Separate credentials for Message and MDN
- Available in both WebUI and SwingUI
- Runtime credential resolution

### Partner Visibility
- User-based access control for partners
- "Visible to all" vs "Specific users only"
- Message list auto-filtering
- Admin bypass
- Local stations always visible

### User Interface
- SwingUI: New "User Preference" menu with HTTP Authentication submenu
- WebUI: Preferences page with HTTP Authentication tab
- Partner dialog: Visibility tab and HTTP Authentication tab

## Documentation Style

- Added detailed step-by-step instructions
- Included both WebUI and SwingUI workflows
- Added practical examples
- Referenced specific UI locations
- Provided troubleshooting tips

## Impact

Users can now:
1. Understand the HTTP authentication feature
2. Learn how to configure partner visibility
3. Follow clear setup instructions for both UIs
4. Troubleshoot common issues
5. See what features are completed vs planned

## Files Modified

- `/Users/I572958/SAPDevelop/github/mend-as2/README.md`
