# Development Scripts

This folder contains utility scripts for development and administrative tasks.

## Available Scripts

### 🔐 Admin Password Reset

**Purpose**: Reset the admin user's password when locked out of the system.

**Files**:
- `reset-admin-password.sh` - Linux/Mac version
- `reset-admin-password.bat` - Windows version
- `ADMIN_PASSWORD_RESET.md` - Complete documentation

**Quick Start**:
```bash
# Linux/Mac
./dev-scripts/reset-admin-password.sh

# Windows
dev-scripts\reset-admin-password.bat
```

**Important**: Server must be stopped before running this tool.

📖 See [ADMIN_PASSWORD_RESET.md](./ADMIN_PASSWORD_RESET.md) for full documentation.

---

## Adding New Scripts

When adding new development scripts to this folder:

1. Place the script files here (`*.sh` for Unix, `*.bat` for Windows)
2. Make Unix scripts executable: `chmod +x script-name.sh`
3. Scripts should auto-detect project root: `cd "$(dirname "$0")/.."`
4. Add documentation in this README
5. Consider creating a detailed `.md` file for complex scripts

## Guidelines

- ✅ Scripts should work when run from any directory
- ✅ Include clear error messages
- ✅ Add usage instructions at the top of each script
- ✅ Use meaningful names: `verb-noun.sh` (e.g., `reset-admin-password.sh`)
- ✅ Document prerequisites and side effects
