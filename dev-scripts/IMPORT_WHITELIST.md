# IP Whitelist Import Script

Import CIDR entries from a JSON file to the Global IP Whitelist using a fast Java-based batch import utility.

## Requirements

- **Java 17+** (already required for mend-as2)
- **mend-as2.jar** - The compiled release JAR file

No external dependencies (jq, database clients) needed - the Java utility handles everything internally using the application's existing database connection framework.

## Deployment Structure

The scripts automatically detect the JAR file location based on your deployment:

**Release Deployment** (extracted from tar.gz):
```
mend-as2-1.1.0/
├── mend-as2-1.1.0.jar    ← Versioned JAR file
├── dev-scripts/
│   ├── import-whitelist.sh
│   └── import-whitelist.bat
├── config/
└── ...
```

**Alternative Release** (generic name):
```
mend-as2-1.1.0/
├── mend-as2.jar          ← Generic JAR file name
├── dev-scripts/
└── ...
```

**Development Build**:
```
mend-as2/
├── target/
│   └── mend-as2-1.1.0.jar
├── dev-scripts/
└── ...
```

The scripts search in this order:
1. Parent directory: `mend-as2.jar` or `mend-as2-*.jar`
2. Target directory: `target/mend-as2.jar` or `target/mend-as2-*.jar`

## Performance

The Java-based importer uses batch processing and database transactions for optimal performance:
- **Speed**: 500-1000+ entries/second (vs ~100-200 with shell script)
- **Memory efficient**: Processes entries in batches of 100
- **Transaction safe**: Automatic rollback on failure
- **Database agnostic**: Automatically detects PostgreSQL or MySQL

## Database Support

The Java utility automatically detects the database type from the application's configuration and works with:
- **PostgreSQL** 14+
- **MySQL** 8+ / MariaDB 10.6+

Configuration is loaded from:
- `config/as2.properties` - Database type selection
- `config/database-postgresql.properties` - PostgreSQL settings
- `config/database-mysql.properties` - MySQL settings

The utility uses the same database connection framework as the main application, ensuring consistent behavior.

## JSON File Format

The JSON file should be an array of objects with these fields:

```json
[
  {
    "region": "APJ",
    "country": "New Zealand",
    "location": "Auckland",
    "building": "AKL02",
    "cidr": "202.126.202.144/28",
    "last_modified": "2024-11-16T04:31Z"
  },
  {
    "region": "EMEA",
    "country": "Kazakhstan",
    "location": "Almaty",
    "building": "ALA03",
    "cidr": "80.242.212.240/28",
    "last_modified": "2025-03-07T04:05Z"
  }
]
```

**Required fields:**
- `cidr` - IP address in CIDR notation (e.g., `192.168.1.0/24`)
- `country` - Country name (used in description)
- `location` - Location/city name (used in description)
- `building` - Building code (used in description)

**Optional fields:**
- `region`, `last_modified` - Not used by import script

## Usage

The scripts are simple wrappers around the Java utility. They automatically find the JAR file and invoke the importer.

### Linux/Mac

```bash
cd dev-scripts

# Import for all endpoint types (AS2, TRACKER, WEBUI, API)
./import-whitelist.sh ../private/public_ip_cidr.json

# Import for specific endpoint type
./import-whitelist.sh ../private/public_ip_cidr.json AS2
./import-whitelist.sh ../private/public_ip_cidr.json WEBUI
./import-whitelist.sh ../private/public_ip_cidr.json TRACKER
./import-whitelist.sh ../private/public_ip_cidr.json API
```

### Windows

```cmd
cd dev-scripts

REM Import for all endpoint types
import-whitelist.bat ..\private\public_ip_cidr.json

REM Import for specific endpoint type
import-whitelist.bat ..\private\public_ip_cidr.json AS2
import-whitelist.bat ..\private\public_ip_cidr.json WEBUI
```

### Direct Java Invocation

You can also invoke the Java utility directly:

```bash
# From project root
java -cp mend-as2.jar de.mendelson.comm.as2.tools.IPWhitelistImporter private/public_ip_cidr.json

# Or from target directory during development
java -cp target/mend-as2-1.1.0.jar de.mendelson.comm.as2.tools.IPWhitelistImporter ../private/public_ip_cidr.json AS2
```

## Parameters

| Parameter | Required | Description |
|-----------|----------|-------------|
| `json_file` | Yes | Path to JSON file with CIDR entries |
| `target_type` | No | Endpoint type: `AS2`, `TRACKER`, `WEBUI`, `API`, or `ALL` (default: `ALL`) |

## Target Types

- **AS2** - AS2 message endpoint (`/as2/HttpReceiver/*`)
- **TRACKER** - Tracker endpoint (`/as2/tracker/*`)
- **WEBUI** - Web interface (`/as2/webui/*`)
- **API** - REST API (`/as2/api/*`)
- **ALL** - Import for all four endpoint types

## Behavior

- **Batch Processing**: Entries are inserted in batches of 100 for optimal performance
- **Transaction Safety**: Uses database transactions with automatic rollback on failure
- **Duplicate Handling**: Entries with duplicate (ip_pattern, target_type) are updated with new description
- **Description Format**: `{Country} - {Location} ({Building})` (e.g., `New Zealand - Auckland (AKL02)`)
- **Enabled by Default**: All imported entries are enabled
- **Created By**: Set to `import-tool`
- **Confirmation**: Prompts for confirmation before importing
- **Progress**: Shows real-time import progress and performance metrics

## Examples

### Import all SAP office IPs for all endpoints

```bash
./import-whitelist.sh ../private/public_ip_cidr.json
```

This creates 4 entries per CIDR (one for each endpoint type):
- `202.126.202.144/28` for AS2
- `202.126.202.144/28` for TRACKER
- `202.126.202.144/28` for WEBUI
- `202.126.202.144/28` for API

### Import only for WebUI access

```bash
./import-whitelist.sh ../private/public_ip_cidr.json WEBUI
```

This restricts WebUI login to the specified CIDRs only.

### Import only for AS2 endpoint

```bash
./import-whitelist.sh ../private/public_ip_cidr.json AS2
```

This restricts AS2 message submissions to the specified CIDRs only.

## Verification

After import, verify the entries:

**WebUI:**
1. Login to WebUI
2. Navigate to **System** → **IP Whitelist** → **Global** tab
3. Filter by Target Type to see imported entries

**SwingUI:**
1. Open AS2Gui
2. Menu → **System** → **IP Whitelist Manage** (Cmd/Ctrl+Shift+W)
3. Click **Global Whitelist** tab
4. Filter by Target Type

**SQL Query:**
```sql
-- PostgreSQL or MySQL
SELECT target_type, COUNT(*) as count 
FROM ip_whitelist_global 
WHERE created_by = 'import-tool'
GROUP BY target_type;
```

## Enabling IP Whitelist

After importing entries, enable the whitelist for desired endpoints:

**WebUI/SwingUI:**
1. Navigate to **System** → **IP Whitelist** → **Settings** tab
2. Check the endpoint types you want to protect:
   - ☑ AS2 Endpoint
   - ☑ Tracker Endpoint
   - ☑ WebUI Access
   - ☑ REST API Access
3. Select mode (recommended: **Global + Specific**)
4. Click **Save**

Changes take effect within 60 seconds (cache refresh interval).

## Troubleshooting

### JAR file not found
```
ERROR: Could not find mend-as2.jar
```

**Solution**: Build the project first:
```bash
cd /path/to/mend-as2
mvn clean package
```

The JAR should be created at `target/mend-as2-1.1.0.jar`

### Database connection failed
- Verify database is running
- Check credentials in `config/database-*.properties`
- Ensure `config/as2.properties` has correct `as2.database.type`

### JSON parse error
- Validate JSON syntax: `java -cp mend-as2.jar com.fasterxml.jackson.databind.ObjectMapper < your_file.json`
- Ensure UTF-8 encoding
- Check for trailing commas

### Out of memory
For very large files (100,000+ entries), increase Java heap:
```bash
java -Xmx2g -cp mend-as2.jar de.mendelson.comm.as2.tools.IPWhitelistImporter large_file.json
```

## Notes

- **Idempotent**: Safe to run multiple times - duplicates are updated, not duplicated
- **High Performance**: Uses batch processing (100 entries per batch) and transactions
- **Large Files**: Tested with 10,000+ entries (662KB JSON file) - completes in seconds
- **Performance**: ~500-1000 entries/second depending on database and hardware
- **Transaction Safety**: Automatic rollback on errors - no partial imports
- **Database Agnostic**: Automatically handles PostgreSQL and MySQL syntax differences
- **Memory Efficient**: Batch processing prevents memory issues with large files
- **Rollback**: Uses transactions - errors rollback automatically, manual rollback via database backups for completed imports

## Related Documentation

- [IP Whitelist Feature Documentation](../README.md#ip-whitelist-management)
- [Database Configuration](../config/README.md)
- [Backup & Restore Scripts](README.md)
