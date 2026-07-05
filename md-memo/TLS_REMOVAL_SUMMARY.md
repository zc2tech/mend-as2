=============================================================================
WEBUI: USER-SPECIFIC TLS CERTIFICATES REMOVAL - COMPLETE
=============================================================================

SUMMARY:
Removed user-specific TLS certificate management from WebUI. Users can now
only manage Sign/Encrypt certificates. System-wide TLS management (System → TLS)
remains unchanged.

FILES MODIFIED: 6 files
- CertificateList.jsx (main changes)
- Layout.jsx (navigation)
- PartnerFormTabs.jsx (certificate selection)
- CertificateImportTypeSelector.jsx (import descriptions)
- CertificateImport.jsx (import target description)
- GenerateKeyDialog.jsx (key generation defaults)

=============================================================================
DETAILED CHANGES:
=============================================================================

1. CertificateList.jsx (81 lines changed)
   REMOVED:
   - keystoreType state variable and all dynamic switching
   - TLS tab button UI elements
   - tabStyle function (no longer needed)
   - TLS permission checks (CERT_TLS_READ, CERT_TLS_WRITE)
   
   CHANGED:
   - Page title: "My Sign/Crypt/TLS" → "My Sign/Crypt"
   - All certificate operations hardcoded to 'sign' keystore
   - Ownership filter moved to right (no tabs on left)
   - Export filenames: "sign_keystore.p12" instead of dynamic names
   
   RESULT:
   - Single-page view showing only Sign/Encrypt certificates
   - No tab structure (direct certificate list)
   - Cleaner, simpler UI

2. Layout.jsx (2 lines changed)
   CHANGED:
   - Navigation menu: "My Sign/Crypt/TLS" → "My Sign/Crypt"
   
   RESULT:
   - Consistent naming across UI

3. PartnerFormTabs.jsx (16 lines changed)
   REMOVED:
   - Fetching TLS certificates (useCertificates('tls'))
   - Certificate merging from both keystores
   - Source labels ("Sign/Crypt" vs "TLS")
   
   CHANGED:
   - Only fetches Sign/Encrypt certificates
   - Certificate dropdown shows alias only (no source indicator)
   
   RESULT:
   - Partner certificate selections exclude user-specific TLS
   - Local station, HTTP auth, and inbound auth use only Sign/Encrypt certs

4. CertificateImportTypeSelector.jsx (2 lines changed)
   CHANGED:
   - Target description: Hardcoded to "Sign/Encrypt Keystore"
   - Removed dynamic keystoreType check
   
   RESULT:
   - Clear indication that only Sign/Encrypt can be imported

5. CertificateImport.jsx (2 lines changed)
   CHANGED:
   - Target description: Hardcoded to "Sign/Encrypt Keystore"
   - Removed dynamic keystoreType check
   
   RESULT:
   - Import dialog clearly shows Sign/Encrypt target only

6. GenerateKeyDialog.jsx (6 lines changed)
   CHANGED:
   - extensionTLS: Always false
   - extensionSignEncrypt: Always true
   - Comment updated to reflect sign/encrypt only
   
   RESULT:
   - Generated keys always have sign/encrypt extensions
   - No TLS-specific key generation

=============================================================================
WHAT STILL WORKS:
=============================================================================

✅ System → TLS tab (system-wide TLS, user_id=-1)
   - Completely unchanged
   - Admin can still manage system-wide TLS certificates
   - Used for HTTPS server certificates

✅ All certificate operations for Sign/Encrypt
   - Import (certificate and keystore)
   - Export (PEM, PKCS#12, full keystore, all public certs)
   - Generate Key
   - Generate CSR
   - Verify certificates (CRL)
   - Delete certificates

✅ Partner certificate selections
   - Local station signature/encryption certificates
   - HTTP authentication certificates (message and MDN)
   - Inbound authentication certificates
   - All use Sign/Encrypt certificates only

✅ Admin "All User Certificates" filter
   - Shows all users' Sign/Encrypt certificates
   - Ownership indicator still works

=============================================================================
WHAT NO LONGER WORKS (INTENTIONALLY REMOVED):
=============================================================================

❌ User-specific TLS certificate management
   - No more "TLS Certificates" tab in "My Sign/Crypt"
   - Users cannot import/manage their own TLS certificates
   - keystoreType='tls' API endpoints still exist but not called from WebUI

❌ Tab switching between Sign/Encrypt and TLS
   - Single-page view only

=============================================================================
BACKEND IMPACT:
=============================================================================

NO BACKEND CHANGES REQUIRED for WebUI functionality:
- Backend API already supports keystoreType='sign' filtering
- GET /certificates?keystoreType=sign works correctly
- User-specific TLS endpoints still exist but won't be called
- Database schema unchanged (keydata table still has both types)

=============================================================================
TESTING CHECKLIST:
=============================================================================

[ ] Certificate Management Page
    [ ] Navigate to "My Sign/Crypt" (title updated)
    [ ] No tab structure visible
    [ ] Only Sign/Encrypt certificates shown
    [ ] Import button opens type selector
    [ ] Export menu works (keystore and public certs)
    [ ] Tools menu works (Generate Key, Verify)

[ ] Certificate Import
    [ ] Type selector shows "Sign/Encrypt Keystore" as target
    [ ] Import dialog shows correct target
    [ ] Can import certificate file (.pem, .cer)
    [ ] Can import keystore file (.p12, .jks)

[ ] Certificate Export
    [ ] Individual cert export (PEM, PKCS#12)
    [ ] Full keystore export (names: sign_keystore.p12/jks)
    [ ] All public certs export (name: sign_public_certificates.zip)

[ ] Generate Key
    [ ] Opens dialog successfully
    [ ] Generated keys have sign/encrypt extensions
    [ ] Generated keys saved to sign keystore

[ ] Partner Forms
    [ ] Local station: signature/encryption cert dropdowns
        [ ] Show only Sign/Encrypt certificates
        [ ] No "(TLS)" or "(Sign/Crypt)" labels
    [ ] HTTP Authentication: cert dropdowns for message/MDN
        [ ] Show only Sign/Encrypt certificates with private keys
    [ ] Inbound Auth: "Add Certificate" functionality
        [ ] Shows only Sign/Encrypt certificates

[ ] Admin Features
    [ ] "All User Certificates" filter still works
    [ ] Shows ownership indicators
    [ ] Can see other users' Sign/Encrypt certificates

[ ] System TLS (Unchanged)
    [ ] Navigate to System → TLS
    [ ] System-wide TLS management still works
    [ ] Import/export TLS certificates for HTTPS server

[ ] Navigation
    [ ] Menu shows "My Sign/Crypt" (not "My Sign/Crypt/TLS")
    [ ] Route /certificates still works

[ ] Multiple Users
    [ ] Test with admin user (full access)
    [ ] Test with regular user (own certs only)
    [ ] No console errors

=============================================================================
NEXT STEPS (OPTIONAL):
=============================================================================

1. SwingUI: Update to remove user-specific TLS (if desired)
2. Backend: Consolidate all TLS as system-wide (if desired)
   - Migration script to move user TLS to system TLS
   - Remove keystoreType='tls' with user_id>0 support
3. Database: Add constraint to prevent user-specific TLS

=============================================================================
