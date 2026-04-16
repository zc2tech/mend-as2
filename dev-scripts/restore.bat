@echo off
REM AS2 Database Restore Script (Windows)
REM Restores:
REM   - Clears all tables in as2_db_config database
REM   - Clears all tables in as2_db_runtime database
REM   - Restores full as2_db_config from backup
REM   - Restores version table to as2_db_runtime from backup
REM
REM Usage: restore.bat <backup_file>
REM   backup_file: Name of backup file (e.g., backup_20260416_120000.sql)
REM
REM WARNING: This will DELETE all existing data!
REM

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_ROOT=%SCRIPT_DIR%..
set CONFIG_DIR=%PROJECT_ROOT%\config
set BACKUP_DIR=%SCRIPT_DIR%backups

echo AS2 Database Restore Script
echo ============================
echo.

REM Check if backup file is specified
if "%~1"=="" (
    echo ERROR: Backup file not specified
    echo.
    echo Usage: %~nx0 ^<backup_file^>
    echo.
    echo Available backups:
    if exist "%BACKUP_DIR%\*.sql" (
        dir /b "%BACKUP_DIR%\*.sql"
    ) else (
        echo   ^(none found^)
    )
    pause
    exit /b 1
)

REM Determine backup file path
set BACKUP_FILE=%~1
if not exist "%BACKUP_FILE%" (
    set BACKUP_FILE=%BACKUP_DIR%\%~1
    if not exist "!BACKUP_FILE!" (
        echo ERROR: Backup file not found: %~1
        echo.
        echo Available backups:
        if exist "%BACKUP_DIR%\*.sql" (
            dir /b "%BACKUP_DIR%\*.sql"
        ) else (
            echo   ^(none found^)
        )
        pause
        exit /b 1
    )
)

echo Restore file: %BACKUP_FILE%
for %%A in ("%BACKUP_FILE%") do echo File size: %%~zA bytes
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

REM WARNING prompt
echo ⚠️  WARNING: This will DELETE ALL existing data in:
echo   - %DB_CONFIG% ^(all tables^)
echo   - %DB_RUNTIME% ^(all tables^)
echo.
echo This action CANNOT be undone!
echo.
set /p CONFIRM="Type 'YES' to continue: "

if not "%CONFIRM%"=="YES" (
    echo Restore cancelled.
    pause
    exit /b 0
)

echo.
echo Starting restore...
echo.

REM Perform restore based on database type
if "%DB_TYPE%"=="mysql" (
    call :restore_mysql
) else (
    call :restore_postgresql
)

if errorlevel 1 (
    echo.
    echo ERROR: Restore failed!
    pause
    exit /b 1
)

echo.
echo ✓ Restore completed successfully!
echo.
echo Database has been restored to backup state:
echo   Backup file: %~nx1
echo   Config DB: %DB_CONFIG% ^(full restore^)
echo   Runtime DB: %DB_RUNTIME% ^(version table only^)
echo.
echo Note: Runtime database has only the version table restored.
echo       All message data ^(messages, MDN, logs^) has been cleared.
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

:restore_mysql
    echo === Restoring MySQL databases ===

    REM Check if mysql client is available
    where mysql >nul 2>&1
    if errorlevel 1 (
        echo ERROR: mysql command not found. Please install MySQL client tools.
        exit /b 1
    )

    echo Step 1: Clearing all tables in %DB_CONFIG%...
    (
        echo DROP DATABASE IF EXISTS %DB_CONFIG%;
        echo CREATE DATABASE %DB_CONFIG% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ) | mysql --host=%DB_HOST% --port=%DB_PORT% --user=%DB_USER% --password=%DB_PASSWORD%

    echo Step 2: Clearing all tables in %DB_RUNTIME%...
    (
        echo DROP DATABASE IF EXISTS %DB_RUNTIME%;
        echo CREATE DATABASE %DB_RUNTIME% CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ) | mysql --host=%DB_HOST% --port=%DB_PORT% --user=%DB_USER% --password=%DB_PASSWORD%

    echo Step 3: Restoring from backup file...
    mysql --host=%DB_HOST% --port=%DB_PORT% --user=%DB_USER% --password=%DB_PASSWORD% < "%BACKUP_FILE%"

    exit /b 0

:restore_postgresql
    echo === Restoring PostgreSQL databases ===

    REM Check if psql is available
    where psql >nul 2>&1
    if errorlevel 1 (
        echo ERROR: psql command not found. Please install PostgreSQL client tools.
        exit /b 1
    )

    set PGPASSWORD=%DB_PASSWORD%

    echo Step 1: Clearing all tables in %DB_CONFIG%...
    (
        echo DROP DATABASE IF EXISTS %DB_CONFIG%;
        echo CREATE DATABASE %DB_CONFIG% OWNER %DB_USER%;
    ) | psql --host=%DB_HOST% --port=%DB_PORT% --username=%DB_USER% --dbname=postgres

    echo Step 2: Clearing all tables in %DB_RUNTIME%...
    (
        echo DROP DATABASE IF EXISTS %DB_RUNTIME%;
        echo CREATE DATABASE %DB_RUNTIME% OWNER %DB_USER%;
    ) | psql --host=%DB_HOST% --port=%DB_PORT% --username=%DB_USER% --dbname=postgres

    echo Step 3: Restoring from backup file...
    psql --host=%DB_HOST% --port=%DB_PORT% --username=%DB_USER% --dbname=postgres < "%BACKUP_FILE%"

    set PGPASSWORD=
    exit /b 0
