# AS2 Test Mode Configuration

## Overview

AS2 Test Mode allows you to run a test instance of the AS2 server alongside a production instance on the same machine. This is useful for:
- Testing configuration changes before deploying to production
- Running integration tests
- Developer testing and debugging
- Training and demonstration environments

## How It Works

When test mode is enabled, the AS2 server uses alternative ports to avoid conflicts with a production instance:

| Service | Normal Mode | Test Mode |
|---------|------------|-----------|
| Jetty HTTP | 8080 | 11080 |
| Jetty HTTPS | 8443 | 11443 |
| Client-Server (Mina) | 1234 | 41234 |

## Configuration

### Method 1: Properties File (Recommended)

Edit `config/as2.properties` and set:

```properties
as2.test.mode=true
```

An example configuration is provided in `config/as2-test.properties.example`.

### Method 2: Environment Variable

Set the environment variable before starting the server:

```bash
# Linux/Mac
export AS2_TEST_MODE=true

# Windows
set AS2_TEST_MODE=true
```

Environment variables take precedence over properties file settings.

## Usage Examples

### Running Production and Test Instances Side-by-Side

1. **Start Production Instance** (default configuration):
   ```bash
   java -jar as2.jar
   ```
   - Listens on ports: 8080 (HTTP), 8443 (HTTPS), 1234 (Client-Server)

2. **Start Test Instance** (with test mode enabled):
   ```bash
   # Option A: Using properties file
   # Edit config/as2.properties and set as2.test.mode=true
   java -jar as2.jar
   
   # Option B: Using environment variable
   AS2_TEST_MODE=true java -jar as2.jar
   ```
   - Listens on ports: 18080 (HTTP), 18443 (HTTPS), 11234 (Client-Server)

### Connecting the GUI Client to Test Instance

When connecting to a test mode server, specify the test port:

```bash
# Connect to production (port 1234)
java -cp as2.jar de.mendelson.comm.as2.client.AS2Gui localhost 1234

# Connect to test instance (port 11234)
java -cp as2.jar de.mendelson.comm.as2.client.AS2Gui localhost 11234
```

## Important Notes

### Separate Databases

Test and production instances should use separate databases to avoid data conflicts:

1. Configure different database connections in your HSQL/PostgreSQL/MySQL settings
2. Or use different database schemas/names
3. Ensure the test instance doesn't interfere with production data

### Separate Data Directories

Consider using separate directories for:
- Log files
- Message archives
- Keystore files
- Configuration files

You can achieve this by running the test instance from a different working directory.

### Firewall Configuration

If accessing test instance from other machines, ensure firewall rules allow:
- TCP port 11080 (HTTP)
- TCP port 11443 (HTTPS)
- TCP port 41234 (Client-Server)

### Container/Cloud Deployments

Test mode is particularly useful in containerized environments:

```yaml
# docker-compose.yml example
services:
  as2-production:
    image: mendelson-as2:latest
    environment:
      - AS2_TEST_MODE=false
    ports:
      - "8080:8080"
      - "8443:8443"
      - "1234:1234"

  as2-test:
    image: mendelson-as2:latest
    environment:
      - AS2_TEST_MODE=true
    ports:
      - "11080:11080"
      - "11443:11443"
      - "41234:41234"
```

## Verification

When test mode is enabled, you'll see this message in the server logs:

```
*** TEST MODE ENABLED - Using alternative port 41234 for client-server communication ***
*** TEST MODE: Using HTTP port 11080 and HTTPS port 11443 ***
```

## Testing AS2 Message Endpoints

When sending AS2 messages to a test instance, use the test HTTP port in the partner URL:

- Production URL: `http://yourserver:8080/as2/HttpReceiver`
- Test URL: `http://yourserver:11080/as2/HttpReceiver`

## Disabling Test Mode

To switch back to normal mode:

1. Edit `config/as2.properties` and set:
   ```properties
   as2.test.mode=false
   ```

2. Or unset the environment variable:
   ```bash
   unset AS2_TEST_MODE  # Linux/Mac
   set AS2_TEST_MODE=   # Windows
   ```

3. Restart the server

## Troubleshooting

### Port Already in Use

If you see "Address already in use" errors:
- Check if another process is using the test ports (11080, 11443, 41234)
- Verify test mode is actually enabled (check logs for "TEST MODE ENABLED" message)
- Ensure you're not running multiple test instances

### Client Cannot Connect

If GUI client can't connect to test instance:
- Verify you're using the correct test port (41234)
- Check server logs for successful startup on test port
- Ensure firewall allows connections to test port

### Database Conflicts

If test and production interfere with each other:
- Use separate database instances/schemas
- Check that both instances aren't writing to the same message directories
- Verify log file paths don't overlap
