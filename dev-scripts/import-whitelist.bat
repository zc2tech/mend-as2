@echo off
REM Import IP CIDR whitelist from JSON file to Global Whitelist
REM This script is a wrapper around the Java import utility
REM Usage: import-whitelist.bat <json_file> [target_type]
REM   json_file: Path to JSON file with CIDR entries
REM   target_type: AS2|TRACKER|WEBUI|API|ALL (default: ALL)

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_ROOT=%SCRIPT_DIR%..

REM Check arguments
if "%~1"=="" (
    echo Usage: %~nx0 ^<json_file^> [target_type]
    echo.
    echo Arguments:
    echo   json_file    Path to JSON file with CIDR entries
    echo   target_type  AS2^|TRACKER^|WEBUI^|API^|ALL ^(default: ALL^)
    echo.
    echo Example:
    echo   %~nx0 ..\private\public_ip_cidr.json
    echo   %~nx0 ..\private\public_ip_cidr.json AS2
    exit /b 1
)

set JSON_FILE=%~1
set TARGET_TYPE=%~2
if "%TARGET_TYPE%"=="" set TARGET_TYPE=ALL

REM Convert JSON file path to absolute path (before we change directory)
if not exist "%JSON_FILE%" (
    echo [ERROR] JSON file not found: %JSON_FILE%
    exit /b 1
)
REM Get absolute path using ~f (full path)
set JSON_FILE=%~f1

REM Find the JAR file
REM Priority:
REM 1. Parent directory (release structure: mend-as2-x.x.x/)
REM 2. Target directory (Maven build output)

set JAR_FILE=

REM Check parent directory for versioned or non-versioned JAR
if exist "%PROJECT_ROOT%\mend-as2.jar" (
    set JAR_FILE=%PROJECT_ROOT%\mend-as2.jar
    goto :found
)

REM Try to find versioned JAR in parent directory (e.g., mend-as2-1.1.0.jar)
for /f "delims=" %%i in ('dir /b "%PROJECT_ROOT%\mend-as2*.jar" 2^>nul ^| findstr /v "javadoc sources"') do (
    set JAR_FILE=%PROJECT_ROOT%\%%i
    goto :found
)

REM Check target directory (development)
if exist "%PROJECT_ROOT%\target\mend-as2.jar" (
    set JAR_FILE=%PROJECT_ROOT%\target\mend-as2.jar
    goto :found
)

REM Try to find versioned JAR in target directory
for /f "delims=" %%i in ('dir /b "%PROJECT_ROOT%\target\mend-as2*.jar" 2^>nul ^| findstr /v "javadoc sources"') do (
    set JAR_FILE=%PROJECT_ROOT%\target\%%i
    goto :found
)

:found
if "%JAR_FILE%"=="" (
    echo [ERROR] Could not find mend-as2.jar
    echo Please ensure the JAR file exists in one of these locations:
    echo   - %PROJECT_ROOT%\mend-as2.jar ^(release^)
    echo   - %PROJECT_ROOT%\mend-as2-x.x.x.jar ^(versioned release^)
    echo   - %PROJECT_ROOT%\target\mend-as2-x.x.x.jar ^(development build^)
    exit /b 1
)

echo [INFO] Using JAR: %JAR_FILE%
echo.

REM Build classpath
REM For development (thin JAR), need to include Maven dependencies
REM For release (fat JAR), JAR is self-contained
set CLASSPATH=%JAR_FILE%

REM Check if this is a thin JAR (deployment or development) - add dependencies
for %%A in ("%JAR_FILE%") do set JAR_SIZE=%%~zA
if %JAR_SIZE% LSS 50000000 (
    REM Thin JAR detected (less than 50MB), need to add dependencies

    REM Priority 1: Check for lib folder (thin release deployment)
    if exist "%PROJECT_ROOT%\lib\*" (
        set CLASSPATH=%CLASSPATH%;%PROJECT_ROOT%\lib\*
        echo [INFO] Using dependencies from lib folder
    ) else if exist "%PROJECT_ROOT%\target\dependency\*" (
        REM Priority 2: Maven dependency:copy-dependencies output (development)
        set CLASSPATH=%CLASSPATH%;%PROJECT_ROOT%\target\dependency\*
        echo [INFO] Using dependencies from target/dependency
    ) else if exist "%USERPROFILE%\.m2\repository" (
        REM Priority 3: Add common Maven dependencies directly from Maven repository
        set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\org\postgresql\postgresql\42.7.4\postgresql-42.7.4.jar
        set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\9.0.0\mysql-connector-j-9.0.0.jar
        set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.17.2\jackson-databind-2.17.2.jar
        set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.17.2\jackson-core-2.17.2.jar
        set CLASSPATH=%CLASSPATH%;%USERPROFILE%\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.17.2\jackson-annotations-2.17.2.jar
        echo [INFO] Using dependencies from Maven repository
    ) else (
        echo [WARNING] Thin JAR detected but no dependencies found.
        echo [WARNING] Please run start.bat first to download dependencies, or run: mvn dependency:copy-dependencies
    )
)

REM Change to project root directory so Java can find config folder
cd /d "%PROJECT_ROOT%"

REM Run Java import utility (now running from project root)
REM Suppress SLF4J warnings
java -Dorg.slf4j.simpleLogger.defaultLogLevel=off -cp "%CLASSPATH%" de.mendelson.comm.as2.tools.IPWhitelistImporter "%JSON_FILE%" "%TARGET_TYPE%"

endlocal
