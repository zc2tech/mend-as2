@echo off
REM Mend AS2 Server Startup Script (Headless Mode)
REM Java 17+ required

setlocal

cd /d "%~dp0"

REM Check Java version
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%g
)
set JAVA_VERSION=%JAVA_VERSION:"=%
for /f "tokens=1 delims=." %%a in ("%JAVA_VERSION%") do set JAVA_MAJOR=%%a
if %JAVA_MAJOR% LSS 17 (
    echo ERROR: Java 17 or higher is required. Current version: %JAVA_VERSION%
    exit /b 1
)

REM Set JVM options
set JAVA_OPTS=-Xms512m -Xmx2g

echo Starting Mend AS2 Server (Headless Mode)...
java -version
echo WebUI will be available at: http://localhost:8080/as2/webui/
echo.

REM Find the JAR file (try both headless and full)
for %%f in (mend-as2-*-headless.jar) do set JAR_FILE=%%f
if not defined JAR_FILE (
    for %%f in (mend-as2-*-full.jar) do set JAR_FILE=%%f
)

if not defined JAR_FILE (
    echo ERROR: mend-as2 JAR file not found
    exit /b 1
)

REM Run the server in headless mode
java %JAVA_OPTS% -jar "%JAR_FILE%" -nogui %*
