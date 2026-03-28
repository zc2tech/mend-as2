# PostgreSQL Configuration - Quick Reference

## Configuration Files Created

1. **config/database-postgresql.properties** - Main configuration file
2. **config/database-postgresql.env.example** - Environment variables example
3. **config/docker-compose-postgresql.yml** - Docker Compose setup
4. **config/init-db.sql** - Database initialization script
5. **config/setup-postgresql.sh** - Automated setup script

## Configuration Priority (Highest to Lowest)

1. Environment Variables (`POSTGRES_*`)
2. System Properties (`-Dpostgresql.*`)
3. Properties File (`config/database-postgresql.properties`)
4. Default Values

## Quick Start

### Option 1: Automated Setup (Easiest)

```bash
./config/setup-postgresql.sh
```

### Option 2: Manual Configuration

1. Edit `config/database-postgresql.properties`
2. Create databases manually
3. Start the application

### Option 3: Environment Variables

```bash
source config/database-postgresql.env
java -jar mendelson-as2.jar
```

### Option 4: Docker

```bash
docker-compose -f config/docker-compose-postgresql.yml up -d
```

## Key Configuration Parameters

| Parameter | Environment Variable | Default | Description |
|-----------|---------------------|---------|-------------|
| postgresql.host | POSTGRES_HOST | localhost | Database server hostname |
| postgresql.port | POSTGRES_PORT | 5432 | Database server port |
| postgresql.user | POSTGRES_USER | as2user | Database username |
| postgresql.password | POSTGRES_PASSWORD | as2password | Database password |
| postgresql.db.config | POSTGRES_DB_CONFIG | as2_db_config | Config database name |
| postgresql.db.runtime | POSTGRES_DB_RUNTIME | as2_db_runtime | Runtime database name |

## Connection Pool Parameters

| Parameter | Environment Variable | Default | Description |
|-----------|---------------------|---------|-------------|
| postgresql.pool.maximumPoolSize | POSTGRES_POOL_MAX_SIZE | 10 | Max connections |
| postgresql.pool.minimumIdle | POSTGRES_POOL_MIN_IDLE | 2 | Min idle connections |
| postgresql.pool.connectionTimeout | POSTGRES_POOL_CONN_TIMEOUT | 30000 | Connection timeout (ms) |
| postgresql.pool.idleTimeout | POSTGRES_POOL_IDLE_TIMEOUT | 600000 | Idle timeout (ms) |
| postgresql.pool.maxLifetime | POSTGRES_POOL_MAX_LIFETIME | 1800000 | Max lifetime (ms) |

## Examples

### Development (Local PostgreSQL)

```properties
postgresql.host=localhost
postgresql.port=5432
postgresql.user=as2dev
postgresql.password=devpass123
```

### Production (Remote PostgreSQL with SSL)

```properties
postgresql.host=db.production.com
postgresql.port=5432
postgresql.user=as2prod
postgresql.password=SecureP@ssw0rd
postgresql.ssl.enabled=true
postgresql.ssl.mode=require
```

### High-Performance Setup

```properties
postgresql.pool.maximumPoolSize=50
postgresql.pool.minimumIdle=10
postgresql.pool.connectionTimeout=10000
```

## Verification

After configuration, the application will log the settings at startup:

```
INFO: PostgreSQL Configuration:
  Host: localhost
  Port: 5432
  User: as2user
  Config DB: as2_db_config
  Runtime DB: as2_db_runtime
  Max Pool Size: 10
  Min Idle: 2
  SSL Enabled: false
```

## Troubleshooting

### Configuration not loading?

Check the order:
1. Verify environment variables are set: `echo $POSTGRES_HOST`
2. Check properties file exists: `ls -l config/database-postgresql.properties`
3. Enable debug logging to see what values are being used

### Connection refused?

- Verify PostgreSQL is running: `pg_isready -h localhost -p 5432`
- Check firewall settings
- Verify host and port in configuration

### Authentication failed?

- Check username and password in configuration
- Verify user exists in PostgreSQL: `psql -U postgres -c "\du"`
- Check `pg_hba.conf` authentication method

## Security Best Practices

1. **Use environment variables for production** - Don't commit passwords to git
2. **Enable SSL for remote connections** - Set `postgresql.ssl.enabled=true`
3. **Use strong passwords** - Minimum 16 characters with mixed case/numbers/symbols
4. **Restrict network access** - Configure `pg_hba.conf` appropriately
5. **Regular backups** - Schedule automated backups of both databases
6. **Monitor connections** - Use connection pool statistics for optimization

## See Also

- [README-postgres.MD](README-postgres.MD) - Full PostgreSQL implementation guide
- [PostgreSQL JDBC Documentation](https://jdbc.postgresql.org/documentation/)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
