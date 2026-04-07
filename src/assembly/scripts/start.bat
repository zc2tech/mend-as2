@echo off
REM Mend AS2 Server Startup Script (GUI Mode)
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

echo Starting Mend AS2 Server (GUI Mode)...
java -version
echo.

REM Find the JAR file
for %%f in (mend-as2-*-full.jar) do set JAR_FILE=%%f

if not defined JAR_FILE (
    echo ERROR: mend-as2-*-full.jar not found
    exit /b 1
)

REM Run the server
java %JAVA_OPTS% -jar "%JAR_FILE%" %*
