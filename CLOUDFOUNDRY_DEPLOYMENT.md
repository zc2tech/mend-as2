# CloudFoundry Deployment Guide

## Prerequisites

1. **CloudFoundry CLI installed:**
   ```bash
   cf --version
   ```

2. **PostgreSQL service available in your CloudFoundry space:**
   ```bash
   cf marketplace | grep postgres
   ```

3. **Build the project:**
   ```bash
   mvn clean package -DskipTests
   ```

## Create PostgreSQL Service

Create a PostgreSQL service instance for the AS2 server:

```bash
# Example for different CloudFoundry providers:

# SAP BTP / SAP Cloud Platform
cf create-service postgresql-db small as2-postgresql

# Pivotal / VMware Tanzu
cf create-service p-postgres small-11 as2-postgresql

# AWS RDS via broker
cf create-service aws-rds-postgres basic as2-postgresql

# Check service status
cf service as2-postgresql
```

**Note:** Replace `postgresql-db`, `small`, etc. with the actual service name and plan available in your CloudFoundry marketplace.

## Initialize Database

Before first deployment, you need to initialize the PostgreSQL database with the AS2 schema.

### Option 1: Local Database Init (Recommended)

1. Get service credentials:
   ```bash
   cf create-service-key as2-postgresql admin-key
   cf service-key as2-postgresql admin-key
   ```

2. Connect to database and run SQL scripts:
   ```bash
   psql -h <hostname> -U <username> -d <database>
   ```

3. Run the initialization scripts:
   ```sql
   -- From config/sqlscript/config/CREATE.sql
   -- From config/sqlscript/runtime/CREATE.sql
   -- From config/sqlscript/config/data.sql
   ```

### Option 2: SSH Tunnel

```bash
# SSH to app container after first deployment
cf ssh mendelson-as2

# Run psql from within container
psql $DATABASE_URL -f config/sqlscript/config/CREATE.sql
psql $DATABASE_URL -f config/sqlscript/runtime/CREATE.sql
psql $DATABASE_URL -f config/sqlscript/config/data.sql
```

## Deploy

1. **Login to CloudFoundry:**
   ```bash
   cf login -a <api-endpoint>
   cf target -o <org> -s <space>
   ```

2. **Deploy the application:**
   ```bash
   cf push
   ```

3. **Check deployment status:**
   ```bash
   cf app mendelson-as2
   cf logs mendelson-as2 --recent
   ```

## Access the Application

After successful deployment:

**Web Admin UI:**
```
https://mendelson-as2.apps.example.com/as2/admin/
```

**AS2 Protocol Endpoint:**
```
https://mendelson-as2.apps.example.com/as2/HttpReceiver
```

**REST API:**
```
https://mendelson-as2.apps.example.com/as2/api/v1/
```

**Default Credentials:**
- Username: `admin`
- Password: `admin` (change immediately after first login!)

## Database Connection

CloudFoundry automatically injects PostgreSQL connection details via the `VCAP_SERVICES` environment variable. The AS2 server reads this automatically from the bound `as2-postgresql` service.

**Manual configuration (if needed):**

Set these environment variables:
```bash
cf set-env mendelson-as2 DB_HOST your-postgres-host.com
cf set-env mendelson-as2 DB_PORT 5432
cf set-env mendelson-as2 DB_NAME as2_db
cf set-env mendelson-as2 DB_USER as2user
cf set-env mendelson-as2 DB_PASSWORD your-password

cf restage mendelson-as2
```

## Configuration

### Change Default Password

After first login to the web UI:

1. SSH to the app:
   ```bash
   cf ssh mendelson-as2
   ```

2. Update the passwd file:
   ```bash
   cd /home/vcap/app
   # Use bcrypt to hash new password and update config/passwd
   ```

Or manage users through the web UI (future enhancement).

### Configure Certificates

Upload certificates through the web UI:
1. Go to **Certificates** tab
2. Click **Import Certificate**
3. Upload your .p12/.pfx certificate files
4. Configure partners to use the certificates

### Environment Variables

Available configuration options:

| Variable | Default | Description |
|----------|---------|-------------|
| `AS2_START_GUI` | `true` | Set to `false` for CloudFoundry (no GUI) |
| `AS2_SKIP_CONFIG_CHECK` | `false` | Set to `true` to skip startup checks |
| `AS2_CLIENT_SERVER_ENABLED` | `true` | Set to `false` to disable MINA port 1234 |
| `JETTY_HTTP_PORT` | `8080` | HTTP port for Jetty |
| `JETTY_HTTPS_PORT` | `8443` | HTTPS port for Jetty |

Set via:
```bash
cf set-env mendelson-as2 AS2_START_GUI false
cf restage mendelson-as2
```

## Scaling

### Horizontal Scaling
```bash
cf scale mendelson-as2 -i 2
```

**Note:** The AS2 server maintains file-based state in the `messages/` directory. For multiple instances, you need shared storage (NFS volume service).

### Vertical Scaling
```bash
cf scale mendelson-as2 -m 2G  # Increase memory
cf scale mendelson-as2 -k 2G  # Increase disk
```

## Monitoring

### View Logs
```bash
# Real-time logs
cf logs mendelson-as2

# Recent logs
cf logs mendelson-as2 --recent
```

### Health Check
```bash
cf app mendelson-as2

# Manual health check
curl https://mendelson-as2.apps.example.com/as2/api/v1/system/info
```

### Restart App
```bash
cf restart mendelson-as2
```

## Troubleshooting

### Application Won't Start

Check logs:
```bash
cf logs mendelson-as2 --recent
```

Common issues:
- Database service not bound: `cf bind-service mendelson-as2 as2-postgresql && cf restage mendelson-as2`
- Database not initialized: Run SQL scripts (see "Initialize Database" above)
- Not enough memory: `cf scale mendelson-as2 -m 2G`

### Web UI Not Loading

1. Check app is running: `cf app mendelson-as2`
2. Check route: `cf routes | grep mendelson-as2`
3. Test endpoint: `curl https://mendelson-as2.apps.example.com/as2/admin/`

### Cannot Connect to PostgreSQL

1. Verify service is bound:
   ```bash
   cf services
   cf env mendelson-as2 | grep VCAP_SERVICES
   ```

2. Verify database credentials:
   ```bash
   cf ssh mendelson-as2
   echo $VCAP_SERVICES | jq '.postgresql'
   ```

### Messages Not Processing

Check the application logs for errors:
```bash
cf logs mendelson-as2 --recent | grep ERROR
```

## Security Considerations

1. **Change default password** immediately after first deployment
2. **Use HTTPS** for all access (CloudFoundry provides this automatically)
3. **Bind to private network** if AS2 partners are within the same CloudFoundry foundation
4. **Rotate certificates** regularly through the web UI
5. **Enable audit logging** through preferences
6. **Restrict network policies** to allow only expected AS2 partners

## Cleanup

To remove the application and service:

```bash
# Delete application
cf delete mendelson-as2 -f

# Delete service (WARNING: destroys all data!)
cf delete-service as2-postgresql -f
```

## Support

For deployment issues:
- Check CloudFoundry logs: `cf logs mendelson-as2 --recent`
- Review application logs in the web UI under **System** tab
- Verify PostgreSQL service is running: `cf service as2-postgresql`
