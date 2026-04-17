# TLS Certificates Architecture: System-wide vs User-specific

## Overview

The AS2 system uses different TLS certificate contexts for inbound (receiving) and outbound (sending) connections. Understanding this architecture is crucial for configuring certificate authentication correctly.

## Architecture Summary

### Inbound Connections (Server-side)
When a client connects to `/as2/HttpReceiver/{username}`:

1. **The Jetty HTTP server uses ONLY the SYSTEM-WIDE TLS keystore** (`user_id=0`, "Sys TLS")
2. **ALL inbound connections use the SAME server certificate** regardless of which username is in the URL
3. The server presents its server certificate during TLS handshake
4. If client certificate authentication is configured (mutual TLS), the server requests a client certificate from the remote client
5. The client sends its certificate, which is then validated against the **inbound authentication credentials** configured for that local station (these ARE user-specific)

**Code Reference:**
```java
// AS2Server.java lines 428-433
KeystoreStorage tlsStorage = new KeystoreStorageImplDB(
    SystemEventManagerImplAS2.instance(),
    this.dbDriverManager,
    KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
    KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS,
    0);  // user_id=0 (admin/system) - SYSTEM-WIDE ONLY

// JettyStarter.java line 239
sslContextFactory.setKeyStore(this.tlsStorage.getKeystore());
```

### Outbound Connections (Client-side)
When sending a message to a partner:

1. **Uses the USER-SPECIFIC TLS keystore** (based on the user who owns the partner/message)
2. If certificate authentication is configured for that partner, it sends the client certificate from that user's TLS keystore
3. Each user can have their own set of TLS certificates for outbound connections

**Code Reference:**
```java
// MessageHttpUploader.java lines 955-970
if (this.certStore == null) {
    // Get userId from message info - for outbound messages, use the sender's userId
    int userId = 0; // Default to admin/system
    if (as2Info instanceof AS2MessageInfo) {
        userId = ((AS2MessageInfo) as2Info).getOwnerUserId();
    }

    this.trustStore = new KeystoreStorageImplDB(
            SystemEventManagerImplAS2.instance(),
            this.dbDriverManager,
            KeystoreStorageImplDB.KEYSTORE_USAGE_TLS,
            KeystoreStorageImplDB.KEYSTORE_STORAGE_TYPE_JKS,
            userId  // USER-SPECIFIC based on message owner
    );
    this.certStore = this.trustStore;
}
```

## Certificate Usage Table

| Scenario | Certificate Type | User Context | Keystore Location | Purpose |
|----------|-----------------|--------------|-------------------|---------|
| **Inbound HTTPS Server** | Server Certificate | System-wide (`user_id=0`) | "Sys TLS" | Jetty presents this to ALL incoming HTTPS clients |
| **Inbound Client Auth Validation** | Client Certificate Fingerprints | User-specific | `partner_inbound_auth_credentials` table | Validate which client certs are allowed for each local station |
| **Outbound HTTPS Client** | Client Certificate | User-specific (message owner) | "My TLS certificates" | Send this cert when connecting to partner with cert auth |

## Database Storage

### Keystore Storage
- **Table:** `keydata`
- **Fields:**
  - `user_id`: 0 = system-wide, >0 = user-specific
  - `purpose`: 1 = Sign/Encrypt, 2 = TLS/SSL
  - `storagedata`: Binary blob containing the Java KeyStore
  - `storagetype`: KeyStore type (JKS, PKCS12)

### Inbound Auth Credentials
- **Table:** `partner_inbound_auth_credentials`
- **Fields:**
  - `partner_id`: Which local station this applies to
  - `auth_type`: 1 = Basic Auth, 2 = Certificate
  - `cert_fingerprint`: SHA-1 fingerprint of allowed client certificates
  - `cert_alias`: Alias/name of the certificate
  - `enabled`: Whether this credential is active

## Important Implications

### 1. "My TLS certificates" (User-specific) are ONLY used for:
- **Outbound** connections when sending messages with certificate authentication
- Each user can have different client certificates for their partners
- Located in `keydata` table with `user_id=<specific_user>` and `purpose=2`

### 2. "Sys TLS" (System-wide, `user_id=0`) is used for:
- **Inbound** server certificate for the HTTPS server
- **ALL users share the same server certificate**
- Located in `keydata` table with `user_id=0` and `purpose=2`
- This is the certificate presented when ANY client connects to `/as2/HttpReceiver/{any_username}`

### 3. Certificate Authentication Flow

#### Outbound (Sending Message with Cert Auth):
```
1. User sends message to partner
2. System loads TLS keystore for that user (user_id=message_owner)
3. Partner config specifies cert fingerprint for authentication
4. System finds cert by fingerprint in user's TLS keystore
5. Sends HTTP request with that client certificate
6. Remote server validates the certificate
```

#### Inbound (Receiving Message with Cert Auth):
```
1. Remote client connects to https://server:port/as2/HttpReceiver/admin
2. Server presents system-wide TLS certificate (user_id=0)
3. Server requests client certificate (if mutual TLS configured)
4. Remote client sends their certificate
5. System calculates SHA-1 fingerprint of received certificate
6. System queries partner_inbound_auth_credentials for local station "admin"
7. Compares received fingerprint against ALL enabled cert credentials
8. Authentication passes if ANY match found (OR logic)
```

## Configuration Guide

### For Certificate Authentication to Work:

#### As Sender (Outbound):
1. Create/import TLS certificate with private key in **"My TLS certificates"**
2. Note the certificate's SHA-1 fingerprint
3. Configure partner's "HTTP Authentication" tab:
   - Select "Certificate" mode
   - Choose the certificate from dropdown
4. The certificate will be sent during TLS handshake when connecting to that partner

#### As Receiver (Inbound):
1. Remote sender must have their client certificate
2. Get the SHA-1 fingerprint of their certificate
3. Configure local station's **"Inbound Auth Cert"** tab:
   - Toggle "Enable Certificate Authentication" ON
   - Add row with their certificate fingerprint and alias
   - Mark as enabled
4. When they connect, system validates their certificate against this list

#### Server Certificate (Required for HTTPS):
1. Import/generate server certificate in **"Sys TLS"** (system-wide)
2. This certificate must be valid for the server's hostname/IP
3. Remote clients will see this certificate when connecting via HTTPS
4. Only system administrators (user_id=0) can modify this

## Common Issues and Solutions

### Issue 1: Certificate Not Found in Dropdown
**Symptom:** Newly created TLS certificate doesn't appear in HTTP Authentication dropdown

**Cause:** Before the fix, the dropdown was loading from Sign/Encrypt keystore instead of TLS keystore

**Solution:** 
- Fixed in `JPanelPartner.java` lines 260-296
- Now correctly loads from `certificateManagerSSL` (TLS) instead of `certificateManagerEncSign`
- Restart application to load the fix

### Issue 2: Certificate Not Found in Keystore (Outbound)
**Symptom:** 
```
[OUTBOUND CERT AUTH] Client certificate with fingerprint XX:XX:... not found in keystore
```

**Cause:** Certificate is in wrong keystore (e.g., Sign/Encrypt instead of TLS) or wrong user's keystore

**Solution:**
- Verify certificate exists in **"My TLS certificates"** for the user sending the message
- Check `keydata` table: `user_id=<sender_user_id>` and `purpose=2` (TLS)
- Certificate must have private key (not just public cert)

### Issue 3: Inbound Certificate Auth Always Fails
**Symptom:**
```
No client certificate found in request.
Inbound Certificate Auth failed
```

**Cause:** Remote client not sending certificate, or mutual TLS not configured on server

**Solution:**
- Verify HTTPS is being used (not HTTP)
- Check if server is configured to request client certificates
- Verify remote sender has certificate authentication configured
- Check remote sender logs to confirm certificate is being sent

### Issue 4: Wrong Certificate Being Used
**Symptom:** System uses system-wide certificate instead of user-specific

**Cause:** Context confusion - remember inbound uses system-wide, outbound uses user-specific

**Solution:**
- **Inbound:** Server certificate is ALWAYS system-wide, cannot be changed per user
- **Outbound:** Client certificate is user-specific, configure in "My TLS certificates"

## Testing Certificate Authentication

### Step 1: Verify Certificates
```bash
# Check what certificates exist in database
mysql -u as2user -pas2password -e "
  USE as2_db_config; 
  SELECT id, user_id, purpose, storagetype, lastchanged 
  FROM keydata 
  WHERE purpose=2 
  ORDER BY user_id, id;
"

# Check partner configuration
mysql -u as2user -pas2password -e "
  USE as2_db_config;
  SELECT partnername, authmodehttp, httpauth_cert_fingerprint_message
  FROM partner
  WHERE authmodehttp=3;
"

# Check inbound auth credentials
mysql -u as2user -pas2password -e "
  USE as2_db_config;
  SELECT p.partnername, c.auth_type, c.cert_fingerprint, c.cert_alias, c.enabled
  FROM partner_inbound_auth_credentials c
  JOIN partner p ON c.partner_id = p.id
  WHERE c.auth_type=2;
"
```

### Step 2: Check Logs
```bash
# Monitor outbound certificate auth
tail -f log/$(date +%Y-%m-%d)/as2.log | grep "\[OUTBOUND.*CERT\|certificate fingerprint"

# Monitor inbound certificate auth
tail -f log/$(date +%Y-%m-%d)/as2.log | grep "Certificate\|SSL session\|Inbound.*Auth"
```

### Step 3: Expected Success Logs

**Outbound (Sender):**
```
[OUTBOUND] HTTP Authentication mode: CERTIFICATE, Partner: sender -> receiver
[OUTBOUND CERT AUTH] Certificate authentication requested. Configured fingerprint: XX:XX:...
[OUTBOUND CERT AUTH] Looking for certificate with fingerprint: XX:XX:..., Found alias: my-tls-cert
[OUTBOUND CERT AUTH] Successfully configured SingleCertificateKeyManager with alias: my-tls-cert
[OUTBOUND CERT AUTH] SSL context initialized with client certificate. Certificate will be sent during TLS handshake.
```

**Inbound (Receiver):**
```
Client certificate fingerprint: XX:XX:...
Comparing with configured fingerprint: XX:XX:...
Inbound Certificate Auth accepted for local station: admin from 192.168.1.100
```

## Key Takeaways

1. **Inbound = System-wide server cert** (user_id=0, "Sys TLS")
2. **Outbound = User-specific client cert** (user_id=owner, "My TLS certificates")
3. **Inbound validation = User-specific fingerprint list** (partner_inbound_auth_credentials table)
4. The URL `/as2/HttpReceiver/{username}` determines which local station's inbound auth rules to check, but the server certificate is always system-wide
5. Certificate authentication requires HTTPS (TLS/SSL) - cannot work over plain HTTP
6. Both sender and receiver must properly configure their respective sides for cert auth to work

## References

### Code Files
- `AS2Server.java` - Lines 428-433: System-wide TLS keystore initialization
- `JettyStarter.java` - Line 239: Jetty SSL context configuration
- `MessageHttpUploader.java` - Lines 955-1044: Outbound TLS client certificate setup
- `HttpReceiver.java` - Lines 442-497: Inbound certificate authentication validation
- `JPanelPartner.java` - Lines 260-296: UI certificate dropdown population (fixed to use TLS certs)

### Database Tables
- `keydata` - Binary keystore storage (user_id, purpose)
- `partner_inbound_auth_credentials` - Allowed client certificate fingerprints
- `partner` - Partner configuration including `httpauth_cert_fingerprint_message`

### Related Documentation
- `CERT_AUTH_DEBUG_GUIDE.md` - Debug logging and troubleshooting guide
- `OAUTH2_REMOVAL_PLAN.md` - OAuth2 removal and permission framework
