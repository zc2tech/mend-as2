#!/bin/sh

# ==========================================================
#  Portable installer for ALL Oracle Help (OHJ) JARs
#  Compatible with sh, dash, bash, zsh
# ==========================================================

REPO="./maven-repo"
LIB="./lib/help"

echo "Creating local Maven repository at: $REPO"
mkdir -p "$REPO"

install_jar () {
  ARTIFACT="$1"
  JARFILE="$2"
  FULLPATH="$LIB/$JARFILE"

  if [ -f "$FULLPATH" ]; then
    echo " - Installing $JARFILE as com.oracle:$ARTIFACT:1.0.0"
    mvn install:install-file \
      -Dfile="$FULLPATH" \
      -DgroupId="com.oracle" \
      -DartifactId="$ARTIFACT" \
      -Dversion="1.0.0" \
      -Dpackaging="jar" \
      -DlocalRepositoryPath="$REPO" \
      -DgeneratePom=true > /dev/null
  else
    echo " ! WARNING: Missing: $FULLPATH"
  fi
}

# Install each JAR
install_jar "help"        "ohj.jar"
install_jar "help-share"  "help-share.jar"
install_jar "jewt4"       "jewt.jar"
install_jar "icebrowser"  "oracle_ice.jar"
install_jar "help-utils"  "help-utils.jar"   # optional; skip if not present

echo "Done!"
echo ""
echo "Add this to pom.xml:"
echo ""
echo "<repositories>"
echo "    <repository>"
echo "        <id>local-ohj-repo</id>"
echo "        <url>file://\${project.basedir}/maven-repo</url>"
echo "    </repository>"
echo "</repositories>"
echo ""
