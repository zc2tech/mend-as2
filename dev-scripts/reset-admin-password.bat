@echo off
REM Admin Password Reset Tool
REM Use this script to reset the admin user's password

echo ===================================================
echo   AS2 Admin Password Reset Tool
echo ===================================================
echo.

REM Get the project root directory (parent of dev-scripts)
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%\.."

REM Detect environment (development vs production)
if exist "pom.xml" (
    REM Development environment - use Maven
    set ENV_TYPE=development
    echo Environment: Development ^(Maven available^)
) else (
    REM Production environment - use JAR
    set ENV_TYPE=production
    echo Environment: Production ^(deployed^)

    REM Find the main JAR file
    for %%f in (mend-as2-*.jar) do set MAIN_JAR=%%f
    if not defined MAIN_JAR (
        echo ERROR: Cannot find mend-as2-*.jar in current directory
        echo Current directory: %CD%
        echo Please run this script from the AS2 installation directory.
        pause
        exit /b 1
    )
    echo Found JAR: %MAIN_JAR%
)

echo.

echo WARNING: Make sure the AS2 server is NOT running!
echo This tool must be used when the server is stopped.
echo.
set /p CONFIRM="Do you want to continue? (yes/no): "

if /i not "%CONFIRM%"=="yes" if /i not "%CONFIRM%"=="y" (
    echo Operation cancelled.
    exit /b 0
)

echo.
echo Starting password reset tool...
echo.

REM Run the password reset tool based on environment
if "%ENV_TYPE%"=="development" (
    REM Development: Compile first, then run with java -cp
    echo Compiling project...
    mvn compile -q -DskipTests

    if errorlevel 1 (
        echo ERROR: Maven compilation failed
        pause
        exit /b 1
    )

    echo Building classpath...
    REM Get Maven's full classpath including all dependencies
    REM Use temp file to capture output
    mvn dependency:build-classpath -DincludeScope=runtime -q > %TEMP%\maven_cp.txt 2>nul

    REM Read last line (the classpath) from temp file
    setlocal enabledelayedexpansion
    set MAVEN_CP=
    for /f "delims=" %%i in (%TEMP%\maven_cp.txt) do (
        set "line=%%i"
        if not "!line:~0,1!"=="[" set MAVEN_CP=!line!
    )
    del %TEMP%\maven_cp.txt

    if "!MAVEN_CP!"=="" (
        echo ERROR: Failed to build Maven classpath
        pause
        exit /b 1
    )

    REM Build full classpath: compiled classes + Maven dependencies
    set CLASSPATH=target\classes;!MAVEN_CP!

    echo Running password reset tool...
    echo.

    REM Run the tool
    java -cp "!CLASSPATH!" de.mendelson.comm.as2.usermanagement.AdminPasswordResetTool
    endlocal
) else (
    REM Production: Use java -jar with classpath
    REM Build classpath including main JAR and all lib/*.jar files
    setlocal enabledelayedexpansion
    set CLASSPATH=%MAIN_JAR%
    for %%j in (lib\*.jar) do (
        set CLASSPATH=!CLASSPATH!;%%j
    )

    REM Run the tool
    java -cp "!CLASSPATH!" de.mendelson.comm.as2.usermanagement.AdminPasswordResetTool
    endlocal
)

echo.
echo Done.
pause
