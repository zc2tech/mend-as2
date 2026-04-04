# Read-Only UI Pattern for VIEWER Role

## Pattern to Apply

For each feature component (Partners, Certificates, Messages, System), follow this pattern:

### 1. Import useAuth and check for WRITE permission

```javascript
import { useAuth } from '../auth/useAuth';

export default function ComponentName() {
  const { hasPermission } = useAuth();
  const canWrite = hasPermission('PERMISSION_WRITE'); // e.g., 'PARTNER_WRITE', 'CERT_WRITE', etc.
  
  // ... rest of component
}
```

### 2. Conditionally render write-operation buttons

```javascript
{canWrite && (
  <button onClick={handleCreate}>Create</button>
)}

{canWrite && (
  <button onClick={handleEdit}>Edit</button>
)}

{canWrite && (
  <button onClick={handleDelete}>Delete</button>
)}

{canWrite && (
  <button onClick={handleSave}>Save</button>
)}
```

## Components to Update

### ✅ MessageList.jsx
- ✅ Hide "Send Message" button if no MESSAGE_WRITE
- ✅ Hide "Delete" button in table if no MESSAGE_WRITE

### PartnerList.jsx
- Hide "Create Partner" button if no PARTNER_WRITE
- Hide "Edit" button in table if no PARTNER_WRITE
- Hide "Delete" button in table if no PARTNER_WRITE

### CertificateList.jsx
- Hide "Import Certificate" button if no CERT_WRITE
- Hide "Generate Key" button if no CERT_WRITE
- Hide "Delete" button in table if no CERT_WRITE
- Hide "Export" button in table if no CERT_WRITE

### SystemInfo.jsx
- Hide "Save Settings" button if no SYSTEM_WRITE
- Make input fields read-only if no SYSTEM_WRITE
- Hide any "Edit Configuration" buttons if no SYSTEM_WRITE

## REST API Protection

The REST API already has proper permission checks in JwtAuthenticationFilter.java:

- GET requests require *_READ permission
- POST/PUT/DELETE requests require *_WRITE permission

So even if a VIEWER user tries to call write endpoints directly, they will be blocked by the API.

## Testing

Test with VIEWER role user (e.g., view1):
1. ✅ Can see all sections (Partners, Certificates, Messages, System)
2. ✅ Can view data in all sections
3. ✅ Cannot see Create/Edit/Delete/Save buttons
4. ✅ If they try to call write API directly, get 403 Forbidden
