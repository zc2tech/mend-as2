# COMPLETE OAuth2 Removal Guide for JPanelPartner.java

## ⚠️ IMPORTANT: Backup First
```bash
cp src/main/java/de/mendelson/comm/as2/partner/gui/JPanelPartner.java src/main/java/de/mendelson/comm/as2/partner/gui/JPanelPartner.java.backup
```

## Removal Strategy

Due to the scale (210 OAuth2 references in 4462 lines), use your IDE's refactoring tools:

### Step 1: Remove OAuth2 Imports (COMPLETED ✓)
Already removed:
- `import de.mendelson.util.oauth2.OAuth2Config;`
- `import de.mendelson.util.oauth2.gui.JDialogOAuth2Config;`
- `import de.mendelson.util.oauth2.gui.JDialogOAuth2ConfigClientCredentials;`

### Step 2: Remove Static Image Field (COMPLETED ✓)
Already removed `IMAGE_OAUTH2`

### Step 3: Remove Private Method Blocks

Use your IDE to delete these entire methods (lines 1044-1187):

```java
// DELETE from line 1044 to 1070
private void setupOAuth2ClientCredentialsMessage() { ... }

// DELETE from line 1072 to 1100  
private void setupOAuth2AuthorizationCodeMessage() { ... }

// DELETE from line 1102 to 1130
private void setupOAuth2AuthorizationCodeMDN() { ... }

// DELETE from line 1132 to 1158
private void setupOAuth2ClientCredentialsMDN() { ... }

// DELETE from line 1160 to 1187
private void displayOAuth2() { ... }
```

### Step 4: Remove Action Handler Methods (lines ~4045-4079)

Delete these methods:
```java
private void jButtonOAuth2AuthorizationCodeMDNActionPerformed(...)
private void jButtonOAuth2AuthorizationCodeMessageActionPerformed(...)
private void jButtonOAuth2ClientCredentialsMessageActionPerformed(...)
private void jButtonOAuth2ClientCredentialsMDNActionPerformed(...)
private void jRadioButtonHttpAuthOAuth2ClientCredentialsMessageItemStateChanged(...)
private void jRadioButtonHttpAuthOAuth2ClientCredentialsMDNItemStateChanged(...)
private void jRadioButtonHttpAuthOAuth2AuthorizationCodeMessageActionPerformed(...)
// ... and any other OAuth2 event handlers
```

### Step 5: Remove Component Declarations (lines ~4233-4236, 4381-4384, etc.)

Search for and delete all lines containing these patterns:
- `private javax.swing.JButton jButtonOAuth2`
- `private javax.swing.JRadioButton jRadioButtonHttpAuthOAuth2`
- `private javax.swing.JTextField jTextFieldOAuth2`
- `private javax.swing.JPanel jPanelOAuth2`

### Step 6: Remove OAuth2 UI Initialization in initComponents()

In the `initComponents()` method (starts around line 1195):

1. Delete all lines creating OAuth2 components:
```java
jButtonOAuth2AuthorizationCodeMDN = new javax.swing.JButton();
jButtonOAuth2ClientCredentialsMessage = new javax.swing.JButton();
jRadioButtonHttpAuthOAuth2... = new javax.swing.JRadioButton();
jTextFieldOAuth2... = new javax.swing.JTextField();
jPanelOAuth2... = new javax.swing.JPanel();
```

2. Delete OAuth2 icon setting (lines ~431-438 in `setMultiresolutionIcons`):
```java
this.jButtonOAuth2AuthorizationCodeMessage.setIcon(...);
this.jButtonOAuth2ClientCredentialsMessage.setIcon(...);
this.jButtonOAuth2AuthorizationCodeMDN.setIcon(...);
this.jButtonOAuth2ClientCredentialsMDN.setIcon(...);
```

3. Delete OAuth2 layout additions:
Search for `jPanelHttpAuthData.add(` and delete any lines adding OAuth2 panels:
```java
jPanelHttpAuthData.add(jPanelOAuth2AuthorizationCodeMDN, gridBagConstraints);
jPanelHttpAuthData.add(jPanelOAuth2AuthorizationCodeMessage, gridBagConstraints);
jPanelHttpAuthData.add(jPanelOAuth2ClientCredentialsMDN, gridBagConstraints);
jPanelHttpAuthData.add(jPanelOAuth2ClientCredentialsMessage, gridBagConstraints);
```

4. Delete OAuth2 ActionListener additions:
```java
jButtonOAuth2....addActionListener(new java.awt.event.ActionListener() { ... });
jRadioButtonHttpAuthOAuth2....addActionListener(...);
jRadioButtonHttpAuthOAuth2....addItemListener(...);
```

5. Delete OAuth2 ButtonGroup additions:
```java
buttonGroupAuthenticationMessage.add(jRadioButtonHttpAuthOAuth2...);
buttonGroupAuthenticationMDN.add(jRadioButtonHttpAuthOAuth2...);
```

### Step 7: Remove OAuth2 Data Binding in setPartner() (lines ~667-691)

Delete OAuth2 selection code:
```java
// DELETE these blocks:
if (this.partner.usesOAuth2Message() && this.isPluginActivated(ServerPlugins.PLUGIN_OAUTH2)) {
    OAuth2Config config = this.partner.getOAuth2Message();
    if (config.getRFCMethod() == OAuth2Config.METHOD_RFC6749_4_1) {
        this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthOAuth2AuthorizationCodeMessage, true);
    } else {
        this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthOAuth2ClientCredentialsMessage, true);
    }
}
// ...similar MDN block...
this.displayOAuth2();  // DELETE this line
```

### Step 8: Remove OAuth2 from setButtonState() (lines ~780-791)

Delete all OAuth2 enable/disable code:
```java
// DELETE:
this.jRadioButtonHttpAuthOAuth2AuthorizationCodeMDN.setEnabled(oAuth2Enabled);
this.jRadioButtonHttpAuthOAuth2ClientCredentialsMDN.setEnabled(oAuth2Enabled);
this.jRadioButtonHttpAuthOAuth2AuthorizationCodeMessage.setEnabled(oAuth2Enabled);
this.jRadioButtonHttpAuthOAuth2ClientCredentialsMessage.setEnabled(oAuth2Enabled);
this.jButtonOAuth2AuthorizationCodeMessage.setEnabled(...);
this.jButtonOAuth2ClientCredentialsMessage.setEnabled(...);
this.jButtonOAuth2AuthorizationCodeMDN.setEnabled(...);
this.jButtonOAuth2ClientCredentialsMDN.setEnabled(...);
```

### Step 9: Remove OAuth2 from getPartnerForGUI()

Search for method `getPartnerForGUI()` and remove any OAuth2 state extraction code.

### Step 10: Use IDE Find/Replace

**Final cleanup using IDE Find & Replace (Regex):**

1. Find: `.*OAuth2.*\n` (any line containing OAuth2)
2. Review each match carefully
3. Delete only OAuth2-specific lines

**Important:** Do NOT delete lines that contain both OAuth2 AND non-OAuth2 code!

## After Removal - Compile & Test

```bash
# Compile
mvn clean compile

# Expected: Some compilation errors from Partner.java (OAuth2 methods)
# Those will be fixed next
```

## Next: Clean Partner.java

After JPanelPartner.java is cleaned, remove OAuth2 methods from Partner.java:
- `usesOAuth2Message()`
- `usesOAuth2MDN()`
- `getOAuth2Message()`
- `getOAuth2MDN()`
- `setOAuth2Message(OAuth2Config)`
- `setOAuth2MDN(OAuth2Config)`
- `setUseOAuth2Message(boolean)`
- `setUseOAuth2MDN(boolean)`

## Verification Checklist

- [ ] File compiles without OAuth2 import errors
- [ ] Partner config dialog opens
- [ ] HTTP Authentication tab displays correctly
- [ ] Only "None" and "Basic" authentication options visible
- [ ] Can still configure basic auth for Message and MDN
- [ ] Partner save/load works correctly

## Estimated Time

- **Automated (IDE refactoring)**: 30-60 minutes
- **Manual line-by-line**: 2-4 hours

## Recommendation

Use IntelliJ IDEA or Eclipse's **Safe Delete** refactoring:
1. Right-click on each OAuth2 component field
2. Select "Refactor → Safe Delete"
3. IDE will automatically remove all references
4. Much faster and safer than manual editing
