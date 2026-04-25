@echo off
REM Start mend-as2 with GUI (thin JAR version)
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

    REM Check if dependencies-downloadable.txt exists
    if not exist "%SCRIPT_DIR%dependencies-downloadable.txt" (
        echo ERROR: dependencies-downloadable.txt not found!
        echo This file should be in the same directory as start.bat
        pause
        exit /b 1
    )

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
        echo   mend-as2-dist.zip ^(includes all dependencies^)
        echo.
        pause
        exit /b 1
    )

    echo Maven found, downloading dependencies...
    echo.

    REM Create temporary pom.xml for dependency download
    call :create_pom

    if not exist "%SCRIPT_DIR%lib\temp-pom.xml" (
        echo ERROR: Failed to create temporary pom.xml
        pause
        exit /b 1
    )

    REM Use Maven to download all dependencies from Maven Central
    echo Running: mvn dependency:copy-dependencies
    echo This may take a few minutes on first run...
    echo.
    call mvn dependency:copy-dependencies -f "%SCRIPT_DIR%lib\temp-pom.xml" -DoutputDirectory="%SCRIPT_DIR%lib" -DincludeScope=runtime

    if errorlevel 1 (
        echo.
        echo ERROR: Failed to download dependencies
        echo.
        echo Possible causes:
        echo   1. Internet connection issue
        echo   2. Maven Central is unreachable
        echo   3. Proxy settings needed
        echo   4. Invalid dependency in dependencies-downloadable.txt
        echo.
        echo The temp-pom.xml file is at: %SCRIPT_DIR%lib\temp-pom.xml
        echo You can check it and try running Maven manually:
        echo   mvn dependency:copy-dependencies -f "%SCRIPT_DIR%lib\temp-pom.xml" -DoutputDirectory="%SCRIPT_DIR%lib"
        echo.
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
    REM Reset errorlevel to ensure script continues
    ver >nul
)

REM Find the thin JAR file
set THIN_JAR=
for %%f in (%SCRIPT_DIR%mend-as2-*-thin.jar) do set THIN_JAR=%%f

if not defined THIN_JAR (
    echo ERROR: mend-as2-*-thin.jar not found in %SCRIPT_DIR%
    pause
    exit /b 1
)

REM Set classpath with thin JAR and all libs
set CLASSPATH=%THIN_JAR%;%SCRIPT_DIR%lib\*

REM Start the application
echo Starting Mend AS2 Server...
java -cp "%CLASSPATH%" de.mendelson.comm.as2.AS2 %*

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
