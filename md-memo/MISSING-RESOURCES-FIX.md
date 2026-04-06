# Missing Resources Issue - Fixed

## The Problem

```
Exception in thread "main" java.lang.RuntimeException:
MendelsonMultiResolutionImage:fromSVG(..): [IllegalArgumentException]
Resource missing: /de/mendelson/comm/as2/client/splash_mendelson_opensource_as2.svg
```

## Root Cause

Maven was not copying non-Java resource files (SVG, PNG, properties, etc.) from the `src/main/java` directory to the `target/classes` directory during compilation.

By default, Maven only processes the `src/main/resources` directory for resources, but this project stores some resources (like SVG files) alongside the Java source files in `src/main/java`.

## The Solution

Added a `<resources>` configuration to `pom.xml` to include non-Java files from the source directory:

```xml
<build>
    <resources>
        <!-- Standard resources directory -->
        <resource>
            <directory>src/main/resources</directory>
        </resource>
        <!-- Include non-Java files from java source directory -->
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.svg</include>
                <include>**/*.png</include>
                <include>**/*.gif</include>
                <include>**/*.jpg</include>
                <include>**/*.jpeg</include>
                <include>**/*.properties</include>
                <include>**/*.xml</include>
                <include>**/*.txt</include>
            </includes>
        </resource>
    </resources>
    ...
</build>
```

## What Changed

**Before:**
- Only 2 resources copied from `src/main/resources`
- SVG files in `src/main/java` were ignored

**After:**
- 2 resources from `src/main/resources`
- 222 resources from `src/main/java` (including SVG files)

**Build output shows:**
```
[INFO] Copying 2 resources from src/main/resources to target/classes
[INFO] Copying 222 resources from src/main/java to target/classes
```

## Verification

Check that resources are now in the compiled classes:

```bash
find target/classes -name "*.svg"
```

Should show:
```
target/classes/de/mendelson/comm/as2/client/splash_mendelson_opensource_as2.svg
target/classes/de/mendelson/comm/as2/client/splash_comm_protocols_as2.svg
```

## Resources Now Included

The following file types are now automatically copied from `src/main/java`:
- ✅ SVG images
- ✅ PNG images
- ✅ GIF images
- ✅ JPG/JPEG images
- ✅ Properties files
- ✅ XML files
- ✅ Text files

## How to Apply

**1. Rebuild the project:**
```bash
mvn clean compile
```

**2. Verify resources are copied:**
```bash
ls -la target/classes/de/mendelson/comm/as2/client/*.svg
```

**3. Run the application:**
```bash
mvn exec:java
# Or
java -jar target/mend-as2-1.0b0.jar
```

## Related Issues Fixed

This fix resolves several potential issues:
- Missing splash screen images
- Missing icon files
- Missing properties files embedded in Java packages
- Missing XML configuration files

## Why Resources in src/main/java?

This is an older Java project convention where resources were stored alongside source files. Modern projects typically use:
- `src/main/resources` - All non-Java resources
- `src/main/java` - Only Java source files

This project uses a hybrid approach, which is now properly configured in Maven.

## Alternative Solution

Instead of modifying `pom.xml`, you could move all resources to `src/main/resources` with the same package structure:

```bash
# Move SVG files
mkdir -p src/main/resources/de/mendelson/comm/as2/client
mv src/main/java/de/mendelson/comm/as2/client/*.svg \
   src/main/resources/de/mendelson/comm/as2/client/
```

However, the `pom.xml` solution is easier and doesn't require restructuring the entire project.

## Testing

After rebuilding, the application should:
1. ✅ Load splash screen without errors
2. ✅ Display icons correctly
3. ✅ Find all embedded resources

## Summary

**What was changed:** `pom.xml` - Added resource configuration to include non-Java files from source directory

**What to do:** Run `mvn clean compile` to rebuild with the new configuration

**Result:** All resources (SVG, PNG, properties, etc.) are now properly packaged and accessible at runtime
