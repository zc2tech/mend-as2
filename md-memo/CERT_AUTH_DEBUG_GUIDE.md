# Certificate Authentication Debug Guide

## Overview
Debug logs have been added to troubleshoot certificate authentication for AS2 message transmission.

## Log Locations

### Primary Log File
```
/Users/I572958/SAPDevelop/github/mend-as2/log/<YYYY-MM-DD>/as2.log
```

Example:
```bash
# View today's log
tail -f ~/SAPDevelop/github/mend-as2/log/$(date +%Y-%m-%d)/as2.log

# Search for certificate auth logs
grep "\[OUTBOUND\|CERT AUTH\|certificate\|SSL session" ~/SAPDevelop/github/mend-as2/log/*/as2.log
```

## What Was Added

### 1. Outbound (Sender) Debug Logs
Location: `MessageHttpUploader.java` - When sending AS2 messages

**Log Messages:**
- `[OUTBOUND] HTTP Authentication mode: CERTIFICATE|BASIC|NONE` - Shows auth mode configured
- `[OUTBOUND CERT AUTH] Certificate authentication requested. Configured fingerprint: XX:XX:...` - Shows which cert is configured
- `[OUTBOUND CERT AUTH] Looking for certificate with fingerprint: XX:XX:..., Found alias: <alias>` - Certificate lookup in keystore
- `[OUTBOUND CERT AUTH] Successfully configured SingleCertificateKeyManager with alias: <alias>` - Certificate found and configured
- `[OUTBOUND CERT AUTH] SSL context initialized with client certificate. Certificate will be sent during TLS handshake.` - ✅ Ready to send
- `[OUTBOUND CERT AUTH] Client certificate with fingerprint XX:XX:... not found in keystore.` - ❌ Certificate missing
- `[OUTBOUND CERT AUTH] Certificate authentication mode selected but no certificate fingerprint specified.` - ❌ Config incomplete

### 2. Inbound (Receiver) Debug Logs
Location: `HttpReceiver.java` - When receiving AS2 messages

**Log Messages:**
- `Client certificates not found via standard attribute, checking SSL session...` - Trying to find client cert
- `Available request attributes: ...` - Shows all HTTP request attributes
- `Found SSL session attribute key: <key>` - SSL session found
- `Client certificate fingerprint: XX:XX:...` - Remote client's certificate
- `Comparing with configured fingerprint: XX:XX:...` - Checking against configured certs
- `Inbound Certificate Auth accepted for local station: <name> from <ip>` - ✅ Auth success
- `No client certificate found in request. Remote client may not be sending a certificate...` - ❌ No cert received
- `Inbound Certificate Auth failed for local station: <name> from <ip>` - ❌ Auth failed

## Testing Steps

### 1. Restart the AS2 Server
```bash
# Stop if running
pkill -f mend-as2

# Start with logs visible
cd ~/SAPDevelop/github/mend-as2
java -jar target/mend-as2-*.jar
```

### 2. Configure Remote Partner for Certificate Auth

On the **sending server** (testpod-admin):
1. Open partner configuration for the receiver
2. Go to **"HTTP Authentication"** tab
3. Under **"Message Authentication"** section:
   - Select **"Certificate"** radio button
   - Choose the client certificate from dropdown
4. Save and verify the fingerprint is shown

### 3. Send Test Message

Send an AS2 message from testpod-admin to your local station.

### 4. Check Logs

**On Sender Side:**
```bash
grep "\[OUTBOUND.*testpod-admin\|\[OUTBOUND CERT AUTH\]" log/*/as2.log
```

Expected output:
```
[OUTBOUND] HTTP Authentication mode: CERTIFICATE, Partner: testpod-admin -> admin
[OUTBOUND CERT AUTH] Certificate authentication requested. Configured fingerprint: 8A:2F:...
[OUTBOUND CERT AUTH] Looking for certificate with fingerprint: 8A:2F:..., Found alias: testpod-client
[OUTBOUND CERT AUTH] Successfully configured SingleCertificateKeyManager with alias: testpod-client
[OUTBOUND CERT AUTH] SSL context initialized with client certificate. Certificate will be sent during TLS handshake.
```

**On Receiver Side:**
```bash
grep "Certificate\|SSL session\|Inbound.*Auth" log/*/as2.log | tail -20
```

Expected output (success):
```
Available request attributes: targetUsername, javax.servlet.request.X509Certificate, ...
Client certificate fingerprint: 8A:2F:3D:...
Comparing with configured fingerprint: 8A:2F:3D:...
Inbound Certificate Auth accepted for local station: admin from 127.0.0.1
```

Expected output (failure - no cert sent):
```
Client certificates not found via standard attribute, checking SSL session...
Available request attributes: targetUsername, org.eclipse.jetty.server.Request.Cookies, ...
No SSL session attribute found in request. SSL client authentication may not be configured.
No client certificate found in request.
Inbound Certificate Auth failed for local station: admin from 127.0.0.1
```

## Common Issues

### Issue 1: Certificate Not Found in Keystore (Sender)
**Log:** `Client certificate with fingerprint XX:XX:... not found in keystore`

**Solution:**
1. Check the certificate exists in the TLS/SSL keystore
2. Verify the fingerprint in the partner configuration matches
3. Certificate must be in the **TLS/SSL** keystore, not just sign/encrypt

### Issue 2: No Certificate Sent (Receiver sees no cert)
**Log:** `No client certificate found in request`

**Solution:**
- Check sender logs - is certificate mode actually configured?
- Verify certificate was found in sender's keystore
- Check if the certificate has a private key (required for client auth)

### Issue 3: Fingerprint Mismatch
**Log:** `Comparing with configured fingerprint: XX:XX:..., Inbound Certificate Auth failed`

**Solution:**
- Get the actual fingerprint from sender logs: `Client certificate fingerprint: ...`
- Compare with receiver's configured fingerprints
- Update receiver's "Inbound Auth Cert" tab with correct fingerprint

## Quick Diagnostic Commands

```bash
# Monitor logs in real-time
tail -f ~/SAPDevelop/github/mend-as2/log/$(date +%Y-%m-%d)/as2.log | grep -i "cert\|auth\|ssl"

# Check all certificate auth events today
grep -E "\[OUTBOUND.*CERT|\[INBOUND.*CERT|certificate fingerprint" ~/SAPDevelop/github/mend-as2/log/$(date +%Y-%m-%d)/as2.log

# Find failed authentication attempts
grep "Auth failed\|not found in keystore\|No client certificate" ~/SAPDevelop/github/mend-as2/log/*/as2.log
```

## Success Criteria

✅ **Outbound (Sender) Success:**
```
[OUTBOUND] HTTP Authentication mode: CERTIFICATE
[OUTBOUND CERT AUTH] Successfully configured SingleCertificateKeyManager
[OUTBOUND CERT AUTH] SSL context initialized with client certificate
```

✅ **Inbound (Receiver) Success:**
```
Client certificate fingerprint: XX:XX:...
Comparing with configured fingerprint: XX:XX:...
Inbound Certificate Auth accepted for local station: admin
```

## Next Steps After Testing

Once certificate authentication is working:
1. Remove debug logs if desired (they're verbose but helpful for troubleshooting)
2. Configure all partners that need certificate auth
3. Test with actual production certificates
4. Monitor logs for authentication failures

## Notes

- Both Basic and Certificate authentication use **OR logic** - if ANY configured credential matches, authentication passes
- Certificate fingerprints are case-insensitive and colons are optional in comparison
- The "Inbound Auth Basic" and "Inbound Auth Cert" tabs are independent - you can enable both
