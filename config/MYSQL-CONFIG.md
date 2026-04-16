# MySQL Configuration - Quick Reference

## Configuration Files Created

1. **config/database-mysql.properties** - Main configuration file
2. **config/database-mysql.env.example** - Environment variables example
3. **config/docker-compose-mysql.yml** - Docker Compose setup
4. **config/init-db-mysql.sql** - Database initialization script
5. **config/setup-mysql.sh** - Automated setup script

## Configuration Priority (Highest to Lowest)

1. Environment Variables (`MYSQL_*`)
2. System Properties (`-Dmysql.*`)
3. Properties File (`config/database-mysql.properties`)
4. Default Values

## Quick Start

### Option 1: Automated Setup (Easiest)

```bash
./config/setup-mysql.sh
```

### Option 2: Manual Configuration

1. Edit `config/database-mysql.properties`
2. Databases are created automatically by the application
3. Start the application

### Option 3: Environment Variables

```bash
source config/database-mysql.env
java -jar mendelson-as2.jar
```

### Option 4: Docker

```bash
docker-compose -f config/docker-compose-mysql.yml up -d
```

## Key Configuration Parameters

| Parameter | Environment Variable | Default | Description |
|-----------|---------------------|---------|-------------|
| mysql.host | MYSQL_HOST | localhost | Database server hostname |
| mysql.port | MYSQL_PORT | 3306 | Database server port |
| mysql.user | MYSQL_USER | as2user | Database username |
| mysql.password | MYSQL_PASSWORD | as2password | Database password |
| mysql.db.config | MYSQL_DB_CONFIG | as2_db_config | Config database name |
| mysql.db.runtime | MYSQL_DB_RUNTIME | as2_db_runtime | Runtime database name |

## Connection Pool Parameters

| Parameter | Environment Variable | Default | Description |
|-----------|---------------------|---------|-------------|
| mysql.pool.maximumPoolSize | MYSQL_POOL_MAX_SIZE | 10 | Max connections |
| mysql.pool.minimumIdle | MYSQL_POOL_MIN_IDLE | 2 | Min idle connections |
| mysql.pool.connectionTimeout | MYSQL_POOL_CONN_TIMEOUT | 30000 | Connection timeout (ms) |
| mysql.pool.idleTimeout | MYSQL_POOL_IDLE_TIMEOUT | 600000 | Idle timeout (ms) |
| mysql.pool.maxLifetime | MYSQL_POOL_MAX_LIFETIME | 1800000 | Max lifetime (ms) |

## Examples

### Development (Local MySQL)

```properties
mysql.host=localhost
mysql.port=3306
mysql.user=as2dev
mysql.password=devpass123
```

### Production (Remote MySQL with SSL)

```properties
mysql.host=db.production.com
mysql.port=3306
mysql.user=as2prod
mysql.password=SecureP@ssw0rd
mysql.ssl.enabled=true
mysql.ssl.mode=REQUIRED
```

### High-Performance Setup

```properties
mysql.pool.maximumPoolSize=50
mysql.pool.minimumIdle=10
mysql.pool.connectionTimeout=10000
mysql.useServerPrepStmts=true
mysql.cachePrepStmts=true
mysql.prepStmtCacheSize=250
mysql.prepStmtCacheSqlLimit=2048
```

## Verification

After configuration, the application will log the settings at startup:

```
INFO: MySQL Configuration:
  Host: localhost
  Port: 3306
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
1. Verify environment variables are set: `echo $MYSQL_HOST`
2. Check properties file exists: `ls -l config/database-mysql.properties`
3. Enable debug logging to see what values are being used

### Connection refused?

- Verify MySQL is running: `mysqladmin -h localhost -P 3306 ping`
- Check firewall settings
- Verify host and port in configuration
- Check MySQL is listening on the correct interface: `netstat -an | grep 3306`

### Authentication failed?

- Check username and password in configuration
- Verify user exists in MySQL: `mysql -u root -p -e "SELECT user,host FROM mysql.user;"`
- Check user has correct permissions: `SHOW GRANTS FOR 'as2user'@'localhost';`
- Verify authentication plugin: MySQL 8.0+ uses `caching_sha2_password` by default

### Database creation failed?

- Verify user has CREATE DATABASE privilege: `GRANT CREATE ON *.* TO 'as2user'@'localhost';`
- Check MySQL error log for details
- Manually create databases if automatic creation fails:
  ```sql
  CREATE DATABASE as2_db_config CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  CREATE DATABASE as2_db_runtime CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  GRANT ALL PRIVILEGES ON as2_db_config.* TO 'as2user'@'localhost';
  GRANT ALL PRIVILEGES ON as2_db_runtime.* TO 'as2user'@'localhost';
  FLUSH PRIVILEGES;
  ```

### Character encoding issues?

- Ensure database uses UTF-8:
  ```sql
  ALTER DATABASE as2_db_config CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ALTER DATABASE as2_db_runtime CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```
- Set connection character set in properties:
  ```properties
  mysql.characterEncoding=UTF-8
  mysql.useUnicode=true
  ```

### Time zone issues?

- MySQL stores timestamps differently than PostgreSQL
- Set server timezone in properties:
  ```properties
  mysql.serverTimezone=UTC
  ```
- Or use system timezone:
  ```properties
  mysql.serverTimezone=Asia/Shanghai
  ```

## MySQL-Specific Considerations

### Storage Engine

The application uses **InnoDB** (default in MySQL 5.5+):
- Full ACID compliance
- Row-level locking
- Foreign key support
- Crash recovery

Verify engine: `SHOW TABLE STATUS WHERE Engine != 'InnoDB';`

### SQL Mode

Recommended SQL mode for compatibility:
```sql
SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
```

Add to `my.cnf` or `my.ini`:
```ini
[mysqld]
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
```

### Performance Tuning

Key MySQL parameters for AS2 workload:

```ini
[mysqld]
# Connection settings
max_connections=200
max_connect_errors=100

# Buffer pool (set to 70-80% of RAM for dedicated server)
innodb_buffer_pool_size=1G
innodb_log_file_size=256M
innodb_flush_log_at_trx_commit=2

# Query cache (deprecated in MySQL 8.0+)
query_cache_type=0
query_cache_size=0

# Binary logging (for replication/backups)
log_bin=mysql-bin
expire_logs_days=7
```

## Security Best Practices

1. **Use environment variables for production** - Don't commit passwords to git
2. **Enable SSL for remote connections** - Set `mysql.ssl.enabled=true`
3. **Use strong passwords** - Minimum 16 characters with mixed case/numbers/symbols
4. **Restrict network access** - Configure MySQL bind-address and firewall
5. **Regular backups** - Schedule automated backups using mysqldump or MySQL Enterprise Backup
6. **Monitor connections** - Use connection pool statistics and `SHOW PROCESSLIST` for optimization
7. **Disable remote root access** - Use dedicated application user
8. **Keep MySQL updated** - Apply security patches regularly
9. **Use authentication plugins** - Consider `caching_sha2_password` or `mysql_native_password`
10. **Enable audit logging** - Track database access and changes

## Backup and Recovery

### Automated Backup Script

```bash
#!/bin/bash
# backup-mysql.sh
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups/mysql"
mkdir -p $BACKUP_DIR

# Backup config database
mysqldump -u as2user -p'as2password' \
  --single-transaction \
  --routines --triggers --events \
  as2_db_config > $BACKUP_DIR/as2_config_$DATE.sql

# Backup runtime database
mysqldump -u as2user -p'as2password' \
  --single-transaction \
  --routines --triggers --events \
  as2_db_runtime > $BACKUP_DIR/as2_runtime_$DATE.sql

# Compress backups
gzip $BACKUP_DIR/as2_config_$DATE.sql
gzip $BACKUP_DIR/as2_runtime_$DATE.sql

# Delete backups older than 7 days
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete
```

### Restore from Backup

```bash
# Restore config database
gunzip < as2_config_20260416_120000.sql.gz | mysql -u as2user -p as2_db_config

# Restore runtime database
gunzip < as2_runtime_20260416_120000.sql.gz | mysql -u as2user -p as2_db_runtime
```

## Migration from PostgreSQL

If migrating from PostgreSQL:

1. **Export PostgreSQL data:**
   ```bash
   pg_dump -U as2user -d as2_db_config -F p > config_export.sql
   pg_dump -U as2user -d as2_db_runtime -F p > runtime_export.sql
   ```

2. **Convert SQL syntax** (PostgreSQL → MySQL differences):
   - `SERIAL` → `INT AUTO_INCREMENT`
   - `BYTEA` → `BLOB` or `LONGBLOB`
   - `TEXT` → `TEXT` or `LONGTEXT`
   - `BOOLEAN` → `TINYINT(1)`
   - Function syntax differences

3. **Use schema translation tool** (optional):
   - pgloader: `pgloader postgresql://... mysql://...`
   - AWS DMS (Database Migration Service)

4. **Verify data integrity** after migration

## Monitoring and Maintenance

### Check Database Size

```sql
SELECT 
    table_schema AS 'Database',
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)'
FROM information_schema.tables
WHERE table_schema IN ('as2_db_config', 'as2_db_runtime')
GROUP BY table_schema;
```

### Check Connection Pool Usage

```sql
SHOW STATUS LIKE 'Threads_connected';
SHOW STATUS LIKE 'Max_used_connections';
SHOW VARIABLES LIKE 'max_connections';
```

### Optimize Tables

```sql
OPTIMIZE TABLE messages;
OPTIMIZE TABLE mdn;
-- Run for all large tables periodically
```

### Analyze Tables

```sql
ANALYZE TABLE messages;
ANALYZE TABLE mdn;
-- Improves query performance
```

## See Also

- [README-mysql.MD](README-mysql.MD) - Full MySQL implementation guide
- [MySQL Connector/J Documentation](https://dev.mysql.com/doc/connector-j/en/)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
- [MySQL Performance Tuning](https://dev.mysql.com/doc/refman/8.0/en/optimization.html)
