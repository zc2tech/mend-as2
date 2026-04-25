@echo off
REM Release Preparation Script
REM Updates version numbers across all documentation and source files

echo ===================================================
echo   Mend AS2 Release Preparation Tool
echo ===================================================
echo.

REM Get the project root directory (parent of dev-scripts)
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%\.."

REM Check if version argument provided
if "%~1"=="" (
    echo Usage: %0 ^<new-version^>
    echo.
    echo Example:
    echo   %0 1.2.0
    echo.
    echo This will update version numbers in:
    echo   - pom.xml
    echo   - AS2ServerVersion.java
    echo   - RELEASE.md ^(version-specific examples only^)
    echo.
    exit /b 1
)

set NEW_VERSION=%~1

REM Validate version format (X.Y.Z)
echo %NEW_VERSION% | findstr /R "^[0-9][0-9]*\.[0-9][0-9]*\.[0-9][0-9]*$" >nul
if errorlevel 1 (
    echo ERROR: Invalid version format. Expected X.Y.Z ^(e.g., 1.2.0^)
    exit /b 1
)

REM Extract current version from pom.xml
for /f "tokens=2 delims=<>" %%a in ('findstr /C:"<version>" pom.xml') do (
    set CURRENT_VERSION=%%a
    goto :found_version
)
:found_version

echo Current version: %CURRENT_VERSION%
echo New version:     %NEW_VERSION%
echo.

if "%CURRENT_VERSION%"=="%NEW_VERSION%" (
    echo WARNING: New version is same as current version
)

set /p CONFIRM="Continue with version update? (yes/no): "
if /i not "%CONFIRM%"=="yes" if /i not "%CONFIRM%"=="y" (
    echo Operation cancelled.
    exit /b 0
)

echo.
echo Updating version numbers...
echo.

REM 1. Update pom.xml
echo 1. Updating pom.xml...
powershell -Command "(Get-Content pom.xml) -replace '<version>%CURRENT_VERSION%</version>', '<version>%NEW_VERSION%</version>' | Set-Content pom.xml"

REM 2. Update AS2ServerVersion.java
echo 2. Updating AS2ServerVersion.java...
set VERSION_FILE=src\main\java\de\mendelson\comm\as2\AS2ServerVersion.java

REM Extract version components
for /f "tokens=1 delims=." %%a in ("%NEW_VERSION%") do set MAJOR=%%a
for /f "tokens=2 delims=." %%a in ("%NEW_VERSION%") do set MINOR=%%a
for /f "tokens=3 delims=." %%a in ("%NEW_VERSION%") do set BUILD=%%a

REM Update version constants using PowerShell
powershell -Command "(Get-Content '%VERSION_FILE%') -replace 'VERSION_MAJOR = [0-9]+;', 'VERSION_MAJOR = %MAJOR%;' | Set-Content '%VERSION_FILE%'"
powershell -Command "(Get-Content '%VERSION_FILE%') -replace 'VERSION_MINOR = [0-9]+;', 'VERSION_MINOR = %MINOR%;' | Set-Content '%VERSION_FILE%'"
powershell -Command "(Get-Content '%VERSION_FILE%') -replace 'VERSION_BUILD = [0-9]+;', 'VERSION_BUILD = %BUILD%;' | Set-Content '%VERSION_FILE%'"

REM 3. Update RELEASE.md (only version-specific examples)
echo 3. Updating RELEASE.md...
powershell -Command "(Get-Content RELEASE.md) -replace '%CURRENT_VERSION%', '%NEW_VERSION%' | Set-Content RELEASE.md"

echo.
echo ===================================================
echo   Version Update Complete!
echo ===================================================
echo.
echo Updated files:
echo   * pom.xml
echo   * src\main\java\de\mendelson\comm\as2\AS2ServerVersion.java
echo   * RELEASE.md
echo.
echo Next steps:
echo   1. Update CHANGELOG.md with release notes
echo   2. Review changes: git diff
echo   3. Commit changes: git add . ^&^& git commit -m "Bump version to %NEW_VERSION%"
echo   4. Create tag: git tag -a v%NEW_VERSION% -m "Release version %NEW_VERSION%"
echo   5. Push: git push origin main ^&^& git push origin v%NEW_VERSION%
echo   6. Build distributions: mvn clean package -Pfull ^&^& mvn package -Pheadless
echo   7. Create GitHub release with artifacts
echo.
pause
