@echo off
REM Start mend-as2 in headless mode (thin JAR version)
REM This script will download dependencies from Maven Central on first run

SETLOCAL EnableDelayedExpansion

REM Get the directory where this script is located
set SCRIPT_DIR=%~dp0

REM Create lib directory if it doesn't exist
if not exist "%SCRIPT_DIR%lib" mkdir "%SCRIPT_DIR%lib"

REM Check if dependencies need to be downloaded
if not exist "%SCRIPT_DIR%lib\.dependencies_complete" (
    echo First run detected - downloading dependencies from Maven Central...
    echo This may take a few minutes...
    echo.

    REM Check if Maven is installed
    where mvn >nul 2>&1
    if errorlevel 1 (
        echo ERROR: Maven is not installed or not in PATH!
        echo.
        echo Please install Maven first:
        echo   1. Download from: https://maven.apache.org/download.cgi
        echo   2. Extract and add bin\ to your PATH
        echo   3. Run this script again
        echo.
        echo Alternative: Download the fat JAR distribution instead:
        echo   mend-as2-1.1.0-dist.zip ^(includes all dependencies^)
        echo.
        pause
        exit /b 1
    )

    echo Maven found, downloading dependencies...
    echo.

    REM Create temporary pom.xml for dependency download
    call :create_pom

    REM Use Maven to download all dependencies from Maven Central
    mvn dependency:copy-dependencies -f "%SCRIPT_DIR%lib\temp-pom.xml" -DoutputDirectory="%SCRIPT_DIR%lib" -DincludeScope=runtime -q

    if errorlevel 1 (
        echo ERROR: Failed to download dependencies
        echo Check your internet connection and try again
        del "%SCRIPT_DIR%lib\temp-pom.xml" >nul 2>&1
        pause
        exit /b 1
    )

    echo All dependencies downloaded successfully!
    REM Clean up temporary pom
    del "%SCRIPT_DIR%lib\temp-pom.xml" >nul 2>&1
    REM Create marker file
    echo. > "%SCRIPT_DIR%lib\.dependencies_complete"
    echo.
    echo Dependencies are ready. Starting the application...
    echo.
)

REM Set classpath with thin JAR and all libs
set CLASSPATH=%SCRIPT_DIR%mend-as2-1.1.0-thin.jar;%SCRIPT_DIR%lib\*

REM Start the application in headless mode
echo Starting Mend AS2 Server in headless mode...
java -cp "%CLASSPATH%" de.mendelson.comm.as2.AS2 -Dmend-as2.headless=true %*

REM Exit script
exit /b

:create_pom
    REM Create a minimal pom.xml that lists all dependencies
    (
        echo ^<?xml version="1.0" encoding="UTF-8"?^>
        echo ^<project xmlns="http://maven.apache.org/POM/4.0.0"^>
        echo     ^<modelVersion^>4.0.0^</modelVersion^>
        echo     ^<groupId^>temp^</groupId^>
        echo     ^<artifactId^>dependency-downloader^</artifactId^>
        echo     ^<version^>1.0^</version^>
        echo.
        echo     ^<dependencies^>
    ) > "%SCRIPT_DIR%lib\temp-pom.xml"

    REM Read dependencies-downloadable.txt and add each to pom
    for /f "tokens=1,2,3 delims=:" %%a in (%SCRIPT_DIR%dependencies-downloadable.txt) do (
        (
            echo         ^<dependency^>
            echo             ^<groupId^>%%a^</groupId^>
            echo             ^<artifactId^>%%b^</artifactId^>
            echo             ^<version^>%%c^</version^>
            echo         ^</dependency^>
        ) >> "%SCRIPT_DIR%lib\temp-pom.xml"
    )

    (
        echo     ^</dependencies^>
        echo ^</project^>
    ) >> "%SCRIPT_DIR%lib\temp-pom.xml"

    exit /b
