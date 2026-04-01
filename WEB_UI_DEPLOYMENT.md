# Mendelson AS2 Web UI - Deployment Guide

## Overview

The mendelson AS2 server now includes a modern web-based management interface accessible via HTTP/HTTPS. This web UI provides full functionality for managing AS2 partners, certificates, messages, and server configuration, making it ideal for CloudFoundry deployments where the traditional Swing GUI cannot be accessed.

## Features

- **Partner Management**: Create, edit, delete, and search AS2 partners
- **Certificate Management**: Import, export, and manage signing/encryption and TLS certificates
- **Message Monitoring**: View message history with filtering, real-time updates, and detailed transaction logs
- **Manual Message Send**: Upload and send files via AS2 protocol
- **CEM Support**: Certificate Exchange Mechanism operations
- **Statistics**: Export transaction statistics with customizable time ranges
- **System Information**: View server details and runtime environment
- **Preferences**: Get and set server configuration values

## Building

The React web UI is built automatically during the Maven package phase:

```bash
mvn clean package
```

This will:
1. Install Node.js and npm (if not already present)
2. Install npm dependencies
3. Build the React app with Vite
4. Copy the build output into the JAR
5. Package everything into a single executable JAR

## Running Locally

After building, start the server:

```bash
java -jar target/as2-1.1b67.jar
```

Access the web UI at:
- **HTTP**: http://localhost:8080/admin/
- **HTTPS**: https://localhost:8443/admin/

Default credentials:
- **Username**: admin
- **Password**: admin

## CloudFoundry Deployment

### Prerequisites

1. PostgreSQL service instance (bind in manifest.yml)
2. Java 11+ buildpack

### manifest.yml Example

```yaml
applications:
  - name: mendelson-as2
    memory: 1G
    disk_quota: 512M
    instances: 1
    buildpack: java_buildpack
    path: target/as2-1.1b67.jar
    env:
      JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 11.+ } }'
      AS2_START_GUI: false
    services:
      - as2-postgresql
    routes:
      - route: as2.apps.example.com
```

### Deploy

```bash
cf push
```

### Access

After deployment, access the web UI at:
```
https://as2.apps.example.com/admin/
```

## API Endpoints

The REST API is available at `/as2/api/v1/`:

### Authentication
- `POST /as2/api/v1/auth/login` - Login (returns JWT in HttpOnly cookie)
- `POST /as2/api/v1/auth/logout` - Logout
- `POST /as2/api/v1/auth/refresh` - Refresh access token

### Partners
- `GET /as2/api/v1/partners` - List all partners
- `POST /as2/api/v1/partners` - Create partner
- `PUT /as2/api/v1/partners/{id}` - Update partner
- `DELETE /as2/api/v1/partners/{id}` - Delete partner

### Certificates
- `GET /as2/api/v1/certificates/{type}` - List certificates (type: sign or tls)
- `POST /as2/api/v1/certificates/import` - Import certificate (multipart)
- `POST /as2/api/v1/certificates/export` - Export certificate

### Messages
- `GET /as2/api/v1/messages` - List messages (with filtering)
- `GET /as2/api/v1/messages/{id}/log` - Message transaction log
- `GET /as2/api/v1/messages/{id}/details` - Detailed message info
- `GET /as2/api/v1/messages/{id}/payload` - Download payload
- `POST /as2/api/v1/messages/send` - Send message (multipart)

### System
- `GET /as2/api/v1/system/info` - Server information
- `GET /as2/api/v1/preferences/{key}` - Get preference value
- `PUT /as2/api/v1/preferences/{key}` - Set preference value
- `GET /as2/api/v1/statistics/export` - Export statistics (CSV)

## Security

### Authentication
- JWT-based authentication with HttpOnly cookies
- Access token: 15-minute expiry
- Refresh token: 7-day expiry
- Automatic token refresh on 401 errors

### Password Management
Manage users via the `config/passwd` file (same as Swing GUI):
```
admin:{bcrypt-hash}
```

### HTTPS Configuration
Configure TLS certificates in `config/certificates.p12` (TLS keystore).

## Development

### Backend Only
```bash
mvn clean package -DskipTests
java -jar target/as2-1.1b67.jar
```

### Frontend Development (Hot Reload)
```bash
cd src/main/webapp/admin
npm install
npm run dev
```

Access at http://localhost:5173 (proxies API requests to :8080)

### Skip Frontend Build
To speed up development when only changing backend code:
```bash
mvn clean package -Dskip.npm
```

## Architecture

### Backend
- **Framework**: JAX-RS (Jersey 2.41) with Jackson JSON serialization
- **Authentication**: JWT with HttpOnly cookies
- **Server**: Embedded Jetty 10.0.24

### Frontend
- **Framework**: React 18.3 with Vite 5.1
- **Routing**: React Router 6
- **State Management**: React Query (TanStack Query)
- **Forms**: React Hook Form with Zod validation
- **API Client**: Axios with automatic token refresh

### Integration
The React SPA is bundled into the JAR during Maven build and served as static files from `/admin/` path by Jetty.

## Coexistence with Swing GUI

The web UI does NOT replace the Swing GUI. Both interfaces are fully functional:

- **Swing GUI**: Connects via MINA protocol on port 1234 (desktop users)
- **Web UI**: Connects via REST API on ports 8080/8443 (browser users)

Both use the same backend business logic (`AS2ServerProcessing`), ensuring consistency.

## Troubleshooting

### Web UI Not Loading
- Check that Jetty is running: `curl http://localhost:8080/admin/`
- Verify React build was included: `jar tf target/as2-1.1b67.jar | grep webapp/admin`

### Authentication Fails
- Check `config/passwd` file exists and has valid user entries
- Verify cookies are enabled in browser
- Check browser console for errors

### API Returns 404
- Verify Jersey servlet is registered in `jetty10/webapps/as2/WEB-INF/web.xml`
- Check JAX-RS application is loading: look for "REST API started" in logs

### Frontend Build Fails During Maven
- Check Node.js version: requires v20.11.0+
- Clear npm cache: `rm -rf src/main/webapp/admin/node_modules`
- Manually build: `cd src/main/webapp/admin && npm install && npm run build`

## Support

For issues or feature requests, contact the development team or file an issue in the project repository.
