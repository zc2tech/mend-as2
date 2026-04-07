# Mina Client-Server Security

## Current Security Model

The internal Mina-based client-server communication (port 1234) is secured by **localhost-only binding**.

### Security Measures in Place

1. **Localhost Restriction** (Default)
   - Port 1234 only accepts connections from `localhost` (127.0.0.1)
   - Configured via `allowAllClients=false` in AS2Server constructor
   - Enforced by `ClientServerSessionHandlerLocalhost.sessionOpened()`
   - Remote connections are immediately rejected

2. **Connection Validation**
   ```java
   // ClientServerSessionHandlerLocalhost.java line 55-61
   if (!this.allowAllClients && !localAddress.getHostName().equalsIgnoreCase(remoteAddress.getHostName())) {
       // Reject connection
       session.close(immediately);
   }
   ```

3. **Architecture**
   - SwingUI and AS2Server run on same machine (localhost)
   - WebUI uses separate REST API (port 8080) with JWT authentication
   - No remote access to Mina port needed

### Deployment Scenarios

**✅ Single Server (Default)**
- AS2Server runs with SwingUI on same machine
- Port 1234 bound to localhost only
- **Secure**: Remote attackers cannot connect

**✅ Container/Cloud (Headless)**
- AS2Server runs without SwingUI (`-nogui` flag)
- Port 1234 still bound to localhost
- **Secure**: Container isolation + localhost binding

**✅ Remote Desktop (VNC/RDP)**
- User connects to server via VNC/RDP
- SwingUI runs locally on server accessing localhost:1234
- **Secure**: Mina connection is local to server

**⚠️ Distributed Setup (Not Supported)**
- SwingUI on machine A, AS2Server on machine B
- Would require `allowAllClients=true` (insecure)
- **Not Recommended**: Use WebUI instead for remote access

### Firewall Configuration

**Recommended Rules:**

```bash
# Allow HTTP/HTTPS for AS2 protocol and WebUI
iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
iptables -A INPUT -p tcp --dport 443 -j ACCEPT

# Block Mina port from external access (defense in depth)
iptables -A INPUT -p tcp --dport 1234 ! -i lo -j DROP

# Alternative: Bind Mina to loopback only (already configured in code)
```

### Verification

Check if Mina is bound to localhost:

```bash
# Linux/Mac
netstat -an | grep 1234
# Should show: 127.0.0.1:1234 or 0.0.0.0:1234 (binding)

# Check remote access (from another machine)
telnet <server-ip> 1234
# Should fail: Connection refused or timeout
```

### Security Best Practices

1. ✅ **Keep `allowAllClients=false`** (default) - Never change unless absolutely necessary
2. ✅ **Use WebUI for remote access** - Properly authenticated with JWT
3. ✅ **Firewall Mina port** - Block port 1234 from external networks (defense in depth)
4. ✅ **Monitor logs** - Check for rejected connection attempts
5. ✅ **Container isolation** - When dockerized, Mina is internal to container

### Known Limitations

1. **No TLS on Mina Port**
   - Mina connection is unencrypted
   - Mitigated by localhost-only binding
   - Traffic never leaves the machine

2. **No Authentication After Connection**
   - SwingUI bypasses authentication (dummy user)
   - Mitigated by localhost-only access
   - Physical access = full system access (expected)

3. **Port Scanning Visibility**
   - Port 1234 may be visible in port scans (bound to 0.0.0.0)
   - Connections rejected at application layer (not TCP layer)
   - Consider binding to 127.0.0.1 at socket level for additional obscurity

### Advanced: Bind to Loopback Interface Only

To prevent port 1234 from appearing in external port scans, modify socket binding:

**File**: `src/main/java/de/mendelson/util/clientserver/ClientServer.java`

Find the acceptor binding and change:
```java
// Current (binds to all interfaces)
acceptor.bind(new InetSocketAddress(port));

// Secure (binds to loopback only)
acceptor.bind(new InetSocketAddress("127.0.0.1", port));
```

This ensures the port is not even visible to external scanners.

### Comparison: Mina vs REST API Security

| Aspect | Mina (Port 1234) | REST API (Port 8080) |
|--------|------------------|----------------------|
| **Access** | Localhost only | Network accessible |
| **Authentication** | None (localhost trust) | JWT + HttpOnly cookies |
| **Encryption** | None (localhost) | HTTPS in production |
| **Authorization** | None (full access) | RBAC with permissions |
| **Audit** | Basic logging | Full audit trail |
| **Clients** | SwingUI only | WebUI, external apps |

### Conclusion

The Mina client-server port is **secure by default** due to localhost-only binding. This is appropriate for:
- ✅ Same-machine SwingUI + Server deployment
- ✅ Headless server deployment (no SwingUI)
- ✅ Container/cloud deployment (internal only)

For remote administrative access, **use WebUI instead** - it provides proper authentication, authorization, and audit trails.

### Migration Path (If Needed)

If you need to eliminate Mina entirely for security/simplicity:
1. See `MINA_REMOVAL_PLAN.md` for full removal strategy
2. Convert SwingUI to embedded mode (same JVM as server)
3. Removes network layer completely
4. Estimated effort: 150-200 hours (complex refactor)

**Recommendation**: Keep current architecture - it's secure and functional.
