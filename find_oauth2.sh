#!/bin/bash
# OAuth2 Removal Script for JPanelPartner.java
# This script identifies all OAuth2-related lines for manual review

FILE="/Users/I572958/SAPDevelop/github/mend-as2/src/main/java/de/mendelson/comm/as2/partner/gui/JPanelPartner.java"

echo "=== OAuth2 References in JPanelPartner.java ==="
echo ""

echo "1. Imports (lines 37-39):"
grep -n "import.*oauth2\|import.*OAuth2" "$FILE"
echo ""

echo "2. Static fields:"
grep -n "IMAGE_OAUTH2" "$FILE"
echo ""

echo "3. Component declarations:"
grep -n "jButtonOAuth2\|jRadioButtonHttpAuthOAuth2\|jTextFieldOAuth2\|jPanelOAuth2" "$FILE" | grep "private javax"
echo ""

echo "4. Setup methods (should remove entirely):"
grep -n "private void setupOAuth2\|private void displayOAuth2" "$FILE"
echo ""

echo "5. Action handlers (should remove entirely):"
grep -n "OAuth2.*ActionPerformed\|OAuth2.*ItemStateChanged" "$FILE" | head -20
echo ""

echo "6. Total OAuth2 references:"
grep -c -i "oauth2\|OAuth2" "$FILE"
