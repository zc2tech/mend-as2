# Recent Updates Summary (2026-04-18)

This document summarizes the recent changes made to the mend-as2 project that should be reflected in README.md.

## 1. User Switching Feature (SwingUI)
**Feature**: Admin users can now switch to other users to test their permissions and view
**Location**: SwingUI menu → User Management → Switch User
**Details**:
- Admin users can impersonate other admin or regular users
- Automatic refresh of message list and partner list after switching
- Menu item changes to "Switch Back" when switched
- Window title shows current user when switched

## 2. Username Validation
**Feature**: Comprehensive validation rules for username creation
**Rules**:
- 3-32 characters
- Letters, digits, hyphens, underscores, dots only
- Must start with letter or digit
- Cannot end with special characters
**Implementation**: Both WebUI and SwingUI with pattern validation and tooltips

## 3. SwingUI Password Visibility Toggle
**Feature**: Show/hide password toggle for Inbound Basic Authentication table
**Location**: Partner → HTTP Authentication tab → Inbound Auth Basic table
**UI**: Eye icon button column (using same SVG icons as HTTP Auth password fields)
- 👁 (unmasked eye) when password visible
- Masked eye icon when password hidden
- Toggle button appears immediately after Password column

## 4. Async MDN Authentication Bug Fix
**Issue**: "None" option for Async MDN authentication not saving
**Fixed**: Radio button event handler now properly sets AUTH_MODE_NONE
**Location**: JPanelPartner.java HTTP Authentication tab

## 5. WebUI Message Sending - Admin Partner Visibility
**Issue**: Admin users couldn't send messages using partners created by other users
**Fixed**: Admin users now see ALL partners (userId=-1) when sending messages
**Logic**: Regular users only see their own partners, admin users bypass filtering

## 6. Database Restore Scripts Update
**Change**: Scripts no longer drop/recreate databases
**New Behavior**: 
- Assumes databases already exist
- Only drops tables within existing databases
- Preserves database-level permissions and settings
**Files**: `dev-scripts/restore.sh` and `dev-scripts/restore.bat`

## Summary for README Update

Key points to add to README.md:

1. **User Switching** section under SwingUI features
2. **Username Validation** rules in User Management section
3. **Password Visibility Toggle** in Inbound Authentication UI features
4. **Troubleshooting** entry for Async MDN auth not saving (now fixed)
5. **Backup & Restore** section update: clarify that restore assumes databases exist
6. **Admin User Privileges** clarification in User Roles section
