# Certificate Import Feature - WebUI Enhancement

## Overview

Added feature parity with SwingUI for certificate imports in the WebUI. Users can now choose between:
1. **Import Certificate** (from trading partner) - standalone certificate files without private key
2. **Import Your Own Private Key** (from keystore) - PKCS#12/JKS files with private keys

This matches the SwingUI's two-step import dialog functionality.

## Changes Made

### Backend (Java)

#### 1. CertificateResource.java - New Endpoint
**File**: `src/main/java/de/mendelson/comm/as2/servlet/rest/resources/CertificateResource.java`

**Added**:
- New endpoint: `POST /certificates/import-file`
  - Accepts `multipart/form-data` for file uploads
  - Parameters:
    - `file`: Certificate/keystore file
    - `keystoreType`: "sign" or "tls"
    - `importType`: "certificate" or "keystore"
    - `password`: (optional, required for keystore)
    - `alias`: (optional) Custom alias

- Two new helper methods:
  - `importStandaloneCertificate()` - Handles `.cer`, `.crt`, `.pem` files
  - `importKeystoreFile()` - Handles `.p12`, `.pfx`, `.jks` files

- New DTO: `ImportCertificateResponseDTO`
  - Returns import details (alias, subjectDN, issuerDN, message)

**Import**: Added `org.glassfish.jersey.media.multipart.FormDataParam` for multipart support

### Frontend (React)

#### 2. CertificateImportTypeSelector.jsx (NEW)
**File**: `src/main/webapp/admin/src/features/certificates/CertificateImportTypeSelector.jsx`

**Purpose**: Two-step import workflow - user selects import type first

**Features**:
- Radio button selection UI
- Visual icons (📜 for certificate, 🔑 for keystore)
- Detailed descriptions for each option
- Responsive design with hover states

#### 3. CertificateImport.jsx (UPDATED)
**File**: `src/main/webapp/admin/src/features/certificates/CertificateImport.jsx`

**Changes**:
- Added `importType` prop ("certificate" or "keystore")
- Conditional rendering based on import type:
  - **Certificate mode**:
    - Accepts: `.cer`, `.crt`, `.pem`
    - No password required
    - Auto-generates alias from CN if not provided
  - **Keystore mode**:
    - Accepts: `.p12`, `.pfx`, `.jks`
    - Password required
    - Imports all entries from keystore

- Updated API call to use `/certificates/import-file` endpoint
- Different file type filters based on import type
- Updated UI labels and descriptions

#### 4. CertificateList.jsx (UPDATED)
**File**: `src/main/webapp/admin/src/features/certificates/CertificateList.jsx`

**Changes**:
- Added state: `showImportTypeSelector`, `importType`
- New handlers:
  - `handleImportClick()` - Shows type selector
  - `handleImportTypeSelected(type)` - Proceeds to import with selected type
  - `handleImportClose()` - Closes import and resets type
- Updated Import button to use new workflow
- Added rendering of `CertificateImportTypeSelector` component

## User Experience

### Import Flow

1. User clicks **"Import Certificate"** button
2. **Import Type Selection Dialog** appears:
   - 📜 Import Certificate (from trading partner)
   - 🔑 Import Your Own Private Key (from keystore)
3. User selects type and clicks **"Continue"**
4. **Import Dialog** appears (customized for selected type):
   - **Certificate**: Drag-drop `.cer/.crt/.pem`, optional alias
   - **Keystore**: Drag-drop `.p12/.pfx/.jks`, required password, optional alias
5. User uploads file and clicks **"Import"**
6. Success message and certificate list refreshes

### Certificate Import (Trading Partner)
- File types: `.cer`, `.crt`, `.pem`
- No password required
- Alias auto-generated from certificate CN if not provided
- Use case: Importing partner's public certificate for encryption/signature verification

### Keystore Import (Own Private Key)
- File types: `.p12`, `.pfx`, `.jks`
- Password required
- Imports all entries (keys + certificates) from keystore
- Use case: Importing your own private keys for signing/decryption

## API Endpoint Details

### POST /certificates/import-file

**Request** (multipart/form-data):
```
file: [binary data]
keystoreType: "sign" | "tls"
importType: "certificate" | "keystore"
password: string (optional, required for keystore)
alias: string (optional)
```

**Response** (JSON):
```json
{
  "message": "Certificate imported successfully",
  "alias": "partner-cert",
  "subjectDN": "CN=Partner Corp, O=Partner, C=US",
  "issuerDN": "CN=CA, O=Trust CA, C=US",
  "importedAliases": ["alias1", "alias2"]  // For keystore import
}
```

**Error Response**:
```json
{
  "error": "Failed to import certificate: Invalid certificate format"
}
```

## Backend Implementation Details

### Certificate Import Logic

1. **Parse Certificate**:
   - Use `CertificateFactory.getInstance("X.509")`
   - Generate `X509Certificate` from file bytes
   - Extract CN from SubjectDN for alias if not provided

2. **Add to Keystore**:
   - Call `CertificateManager.addCertificate(alias, x509Cert)`
   - Automatically saves keystore

### Keystore Import Logic

1. **Determine Keystore Type**:
   - `.jks` → "JKS"
   - `.p12/.pfx` → "PKCS12"

2. **Load Keystore**:
   - Use `KeyStore.getInstance(type)`
   - Load with provided password

3. **Import All Entries**:
   - Iterate through aliases
   - For key entries: Import private key + certificate chain
   - For certificate entries: Import certificate only
   - Save to target keystore

## Testing

### Manual Testing Steps

1. **Test Certificate Import**:
   ```bash
   # Create test certificate
   openssl req -x509 -newkey rsa:2048 -keyout key.pem -out cert.pem -days 365 -nodes
   
   # In WebUI:
   - Click "Import Certificate"
   - Select "Import Certificate (from trading partner)"
   - Upload cert.pem
   - Verify import success
   ```

2. **Test Keystore Import**:
   ```bash
   # Create test keystore
   keytool -genkeypair -alias testkey -keyalg RSA -keystore test.p12 -storetype PKCS12 -storepass changeit
   
   # In WebUI:
   - Click "Import Certificate"
   - Select "Import Your Own Private Key"
   - Upload test.p12
   - Enter password: changeit
   - Verify import success
   ```

3. **Test Error Handling**:
   - Try importing without password (keystore mode) → Should show error
   - Try importing invalid file → Should show error
   - Try importing to wrong keystore type → Should work but warn if inappropriate

## Dependencies

- **Jersey Multipart**: Already included in pom.xml
  ```xml
  <dependency>
    <groupId>org.glassfish.jersey.media</groupId>
    <artifactId>jersey-media-multipart</artifactId>
  </dependency>
  ```

## Compatibility

- **Java**: 17+ (uses existing certificate APIs)
- **Browsers**: All modern browsers (uses standard FormData API)
- **Existing Code**: No breaking changes, new endpoint only

## Future Enhancements

Potential improvements:
1. Support for certificate chains (`.pem` with multiple certs)
2. Preview certificate details before import
3. Bulk import (multiple files at once)
4. Import from URL/paste text
5. Validation warnings (e.g., expired certificates, weak keys)

## Rollback Plan

If issues arise, the new feature can be disabled without affecting existing functionality:
1. Remove `/certificates/import-file` endpoint
2. Revert `CertificateList.jsx` to use old import flow
3. Delete `CertificateImportTypeSelector.jsx`
4. Revert `CertificateImport.jsx` to original version

The old `/certificates/import` JSON endpoint remains unchanged and functional.
