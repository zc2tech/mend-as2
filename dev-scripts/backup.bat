@echo off
REM AS2 Database Backup Script (Windows)
REM Backs up:
REM   - Full as2_db_config database
REM   - Only version table from as2_db_runtime database
REM
REM Usage: backup.bat [backup_name]
REM   backup_name: Optional custom name (default: backup_YYYYMMDD_HHMMSS)
REM
REM Output: Creates backup file in dev-scripts\backups\ directory
REM

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_ROOT=%SCRIPT_DIR%..
set CONFIG_DIR=%PROJECT_ROOT%\config
set BACKUP_DIR=%SCRIPT_DIR%backups

REM Create backup directory if it doesn't exist
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

echo AS2 Database Backup Script
echo ===========================
echo.

REM Read database type from as2.properties
set DB_TYPE=postgresql
for /f "usebackq tokens=1,2 delims==" %%a in ("%CONFIG_DIR%\as2.properties") do (
    if "%%a"=="as2.database.type" set DB_TYPE=%%b
)
echo Detected database type: %DB_TYPE%

REM Read database configuration based on type
if "%DB_TYPE%"=="mysql" (
    set PROPS_FILE=%CONFIG_DIR%\database-mysql.properties
    call :read_config mysql
) else (
    set PROPS_FILE=%CONFIG_DIR%\database-postgresql.properties
    call :read_config postgresql
)

echo Database Configuration:
echo   Host: %DB_HOST%
echo   Port: %DB_PORT%
echo   User: %DB_USER%
echo   Config DB: %DB_CONFIG%
echo   Runtime DB: %DB_RUNTIME%
echo.

REM Generate backup filename
if "%~1"=="" (
    for /f "tokens=2-4 delims=/ " %%a in ('date /t') do set DATE=%%c%%a%%b
    for /f "tokens=1-3 delims=:." %%a in ('echo %time%') do set TIME=%%a%%b%%c
    set TIME=!TIME: =0!
    set BACKUP_NAME=backup_!DATE!_!TIME!
) else (
    set BACKUP_NAME=%~1
)

set BACKUP_FILE=%BACKUP_DIR%\%BACKUP_NAME%.sql
set BACKUP_INFO=%BACKUP_DIR%\%BACKUP_NAME%.info

echo Creating backup: %BACKUP_FILE%
echo.

REM Perform backup based on database type
if "%DB_TYPE%"=="mysql" (
    call :backup_mysql
) else (
    call :backup_postgresql
)

if errorlevel 1 (
    echo.
    echo ERROR: Backup failed!
    pause
    exit /b 1
)

REM Create backup info file
echo Backup Information > "%BACKUP_INFO%"
echo ================== >> "%BACKUP_INFO%"
echo Created: %date% %time% >> "%BACKUP_INFO%"
echo Database Type: %DB_TYPE% >> "%BACKUP_INFO%"
echo Hostname: %DB_HOST% >> "%BACKUP_INFO%"
echo Port: %DB_PORT% >> "%BACKUP_INFO%"
echo Config Database: %DB_CONFIG% >> "%BACKUP_INFO%"
echo Runtime Database: %DB_RUNTIME% >> "%BACKUP_INFO%"
echo Backup File: %BACKUP_FILE% >> "%BACKUP_INFO%"

echo.
echo ✓ Backup completed successfully!
echo   File: %BACKUP_FILE%
echo   Info: %BACKUP_INFO%
echo.
echo To restore this backup, run:
echo   restore.bat %BACKUP_NAME%.sql
echo.
pause
exit /b 0

REM ============================================================
REM Functions
REM ============================================================

:read_config
    set PREFIX=%~1
    set DB_HOST=localhost
    set DB_PORT=3306
    set DB_USER=as2user
    set DB_PASSWORD=as2password
    set DB_CONFIG=as2_db_config
    set DB_RUNTIME=as2_db_runtime

    if "%PREFIX%"=="postgresql" set DB_PORT=5432

    for /f "usebackq tokens=1,2 delims==" %%a in ("%PROPS_FILE%") do (
        if "%%a"=="%PREFIX%.host" set DB_HOST=%%b
        if "%%a"=="%PREFIX%.port" set DB_PORT=%%b
        if "%%a"=="%PREFIX%.user" set DB_USER=%%b
        if "%%a"=="%PREFIX%.password" set DB_PASSWORD=%%b
        if "%%a"=="%PREFIX%.db.config" set DB_CONFIG=%%b
        if "%%a"=="%PREFIX%.db.runtime" set DB_RUNTIME=%%b
    )
    exit /b 0

:backup_mysql
    echo === Backing up MySQL databases ===

    REM Check if mysqldump is available
    where mysqldump >nul 2>&1
    if errorlevel 1 (
        echo ERROR: mysqldump command not found. Please install MySQL client tools.
        exit /b 1
    )

    echo Dumping full config database: %DB_CONFIG%
    echo Dumping version table from runtime database: %DB_RUNTIME%

    (
        echo -- AS2 Database Backup
        echo -- Created: %date% %time%
        echo -- Database Type: MySQL
        echo -- Config Database: %DB_CONFIG%
        echo -- Runtime Database: %DB_RUNTIME% ^(version table only^)
        echo.
    ) > "%BACKUP_FILE%"

    REM Backup full config database
    mysqldump --host=%DB_HOST% --port=%DB_PORT% --user=%DB_USER% --password=%DB_PASSWORD% --single-transaction --routines --triggers --events --add-drop-table --databases %DB_CONFIG% >> "%BACKUP_FILE%"

    (
        echo.
        echo -- Version table from runtime database
        echo.
    ) >> "%BACKUP_FILE%"

    REM Backup only version table from runtime database
    mysqldump --host=%DB_HOST% --port=%DB_PORT% --user=%DB_USER% --password=%DB_PASSWORD% --single-transaction --add-drop-table %DB_RUNTIME% version >> "%BACKUP_FILE%"

    exit /b 0

:backup_postgresql
    echo === Backing up PostgreSQL databases ===

    REM Check if pg_dump is available
    where pg_dump >nul 2>&1
    if errorlevel 1 (
        echo ERROR: pg_dump command not found. Please install PostgreSQL client tools.
        exit /b 1
    )

    echo Dumping full config database: %DB_CONFIG%
    echo Dumping version table from runtime database: %DB_RUNTIME%

    set PGPASSWORD=%DB_PASSWORD%

    (
        echo -- AS2 Database Backup
        echo -- Created: %date% %time%
        echo -- Database Type: PostgreSQL
        echo -- Config Database: %DB_CONFIG%
        echo -- Runtime Database: %DB_RUNTIME% ^(version table only^)
        echo.
    ) > "%BACKUP_FILE%"

    REM Backup full config database
    pg_dump --host=%DB_HOST% --port=%DB_PORT% --username=%DB_USER% --clean --if-exists --create %DB_CONFIG% >> "%BACKUP_FILE%"

    (
        echo.
        echo -- Version table from runtime database
        echo.
    ) >> "%BACKUP_FILE%"

    REM Backup only version table from runtime database
    pg_dump --host=%DB_HOST% --port=%DB_PORT% --username=%DB_USER% --clean --if-exists --table=version %DB_RUNTIME% >> "%BACKUP_FILE%"

    set PGPASSWORD=
    exit /b 0
