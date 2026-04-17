# OAuth2 Removal Plan for JPanelPartner.java

## Overview
Remove all OAuth2-related components from the HTTP Authentication tab in Partner configuration.

## Files to Modify

### 1. JPanelPartner.java (Primary file - 210 OAuth2 references)

#### A. Remove Imports (Lines 37-39)
```java
// REMOVE these lines:
import de.mendelson.util.oauth2.OAuth2Config;
import de.mendelson.util.oauth2.gui.JDialogOAuth2Config;
import de.mendelson.util.oauth2.gui.JDialogOAuth2ConfigClientCredentials;
```

#### B. Remove Static Image Field (Lines 157-159)
```java
// REMOVE:
private final static MendelsonMultiResolutionImage IMAGE_OAUTH2
        = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/oauth2/gui/oauth2.svg", 
                AS2Gui.IMAGE_SIZE_TOOLBAR);
```

#### C. Remove Field Declarations (Around lines 4233-4236, 4381-4384)
```java
// REMOVE these component fields:
private javax.swing.JButton jButtonOAuth2AuthorizationCodeMDN;
private javax.swing.JButton jButtonOAuth2AuthorizationCodeMessage;
private javax.swing.JButton jButtonOAuth2ClientCredentialsMDN;
private javax.swing.JButton jButtonOAuth2ClientCredentialsMessage;
private javax.swing.JRadioButton jRadioButtonHttpAuthOAuth2AuthorizationCodeMDN;
private javax.swing.JRadioButton jRadioButtonHttpAuthOAuth2AuthorizationCodeMessage;
private javax.swing.JRadioButton jRadioButtonHttpAuthOAuth2ClientCredentialsMDN;
private javax.swing.JRadioButton jRadioButtonHttpAuthOAuth2ClientCredentialsMessage;
```

#### D. Remove OAuth2 Radio Button Initialization in setMultiresolutionIcons()  
(Lines 431-438)
```java
// REMOVE:
this.jButtonOAuth2AuthorizationCodeMessage.setIcon(...);
this.jButtonOAuth2ClientCredentialsMessage.setIcon(...);
this.jButtonOAuth2AuthorizationCodeMDN.setIcon(...);
this.jButtonOAuth2ClientCredentialsMDN.setIcon(...);
```

#### E. Remove OAuth2 Data Binding in setPartner() (Lines 667-691)
```java
// REMOVE OAuth2 selection logic:
if (this.partner.usesOAuth2Message() && this.isPluginActivated(ServerPlugins.PLUGIN_OAUTH2)) {
    OAuth2Config config = this.partner.getOAuth2Message();
    if (config.getRFCMethod() == OAuth2Config.METHOD_RFC6749_4_1) {
        this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthOAuth2AuthorizationCodeMessage, true);
    } else {
        this.setUIValueWithoutEvent(this.jRadioButtonHttpAuthOAuth2ClientCredentialsMessage, true);
    }
}
// ...similar for MDN OAuth2
this.displayOAuth2();  // REMOVE this call
```

#### F. Remove OAuth2 Enable/Disable Logic in setButtonState() (Lines 780-791)
```java
// REMOVE all OAuth2 button enable/disable code:
this.jRadioButtonHttpAuthOAuth2AuthorizationCodeMDN.setEnabled(oAuth2Enabled);
this.jRadioButtonHttpAuthOAuth2ClientCredentialsMDN.setEnabled(oAuth2Enabled);
this.jRadioButtonHttpAuthOAuth2AuthorizationCodeMessage.setEnabled(oAuth2Enabled);
this.jRadioButtonHttpAuthOAuth2ClientCredentialsMessage.setEnabled(oAuth2Enabled);
this.jButtonOAuth2AuthorizationCodeMessage.setEnabled(...);
this.jButtonOAuth2ClientCredentialsMessage.setEnabled(...);
this.jButtonOAuth2AuthorizationCodeMDN.setEnabled(...);
this.jButtonOAuth2ClientCredentialsMDN.setEnabled(...);
```

#### G. Remove displayOAuth2() Method (Search for "private void displayOAuth2")
```java
// REMOVE entire method that displays OAuth2 configuration info
```

#### H. Remove OAuth2 Component Initialization in initComponents()
Search for all lines creating OAuth2 components:
```java
// REMOVE lines like:
jButtonOAuth2AuthorizationCodeMDN = new javax.swing.JButton();
jButtonOAuth2ClientCredentialsMessage = new javax.swing.JButton();
jRadioButtonHttpAuthOAuth2AuthorizationCodeMDN = new javax.swing.JRadioButton();
// etc.
```

#### I. Remove OAuth2 Layout Code
Search for all panel additions:
```java
// REMOVE lines adding OAuth2 panels to layout:
jPanelHttpAuthData.add(jPanelOAuth2AuthorizationCodeMDN, gridBagConstraints);
jPanelHttpAuthData.add(jPanelOAuth2AuthorizationCodeMessage, gridBagConstraints);
jPanelHttpAuthData.add(jPanelOAuth2ClientCredentialsMDN, gridBagConstraints);
jPanelHttpAuthData.add(jPanelOAuth2ClientCredentialsMessage, gridBagConstraints);
```

#### J. Remove OAuth2 Button Action Handlers
Search for methods like:
```java
// REMOVE these methods:
private void jButtonOAuth2AuthorizationCodeMessageActionPerformed(java.awt.event.ActionEvent evt)
private void jButtonOAuth2ClientCredentialsMessageActionPerformed(java.awt.event.ActionEvent evt)
private void jButtonOAuth2AuthorizationCodeMDNActionPerformed(java.awt.event.ActionEvent evt)
private void jButtonOAuth2ClientCredentialsMDNActionPerformed(java.awt.event.ActionEvent evt)
```

#### K. Remove OAuth2 Radio Button Listeners
Search for ActionListener additions:
```java
// REMOVE ActionListeners for OAuth2 radio buttons
jRadioButtonHttpAuthOAuth2AuthorizationCodeMessage.addActionListener(...)
jRadioButtonHttpAuthOAuth2ClientCredentialsMessage.addActionListener(...)
jRadioButtonHttpAuthOAuth2AuthorizationCodeMDN.addActionListener(...)
jRadioButtonHttpAuthOAuth2ClientCredentialsMDN.addActionListener(...)
```

#### L. Remove OAuth2 from ButtonGroup
Search for ButtonGroup additions:
```java
// REMOVE from button groups:
buttonGroupHttpAuthenticationMessage.add(jRadioButtonHttpAuthOAuth2AuthorizationCodeMessage);
buttonGroupHttpAuthenticationMessage.add(jRadioButtonHttpAuthOAuth2ClientCredentialsMessage);
buttonGroupHttpAuthenticationMDN.add(jRadioButtonHttpAuthOAuth2AuthorizationCodeMDN);
buttonGroupHttpAuthenticationMDN.add(jRadioButtonHttpAuthOAuth2ClientCredentialsMDN);
```

#### M. Update getPartnerForGUI() Method
Remove OAuth2 value extraction:
```java
// REMOVE OAuth2 state checking code that sets OAuth2 config on partner
```

### 2. Partner.java - Remove OAuth2 Methods

Check and remove these methods if they exist:
```java
public boolean usesOAuth2Message()
public boolean usesOAuth2MDN()
public OAuth2Config getOAuth2Message()
public OAuth2Config getOAuth2MDN()
public void setOAuth2Message(OAuth2Config config)
public void setOAuth2MDN(OAuth2Config config)
```

### 3. HTTPAuthentication.java - Remove OAuth2 Constants

Check for OAuth2 authentication type constants:
```java
public static final int TYPE_OAUTH2_RFC6749_4_1 = ...
public static final int TYPE_OAUTH2_RFC6749_4_4 = ...
```

### 4. Resource Bundle Files

Remove OAuth2-related strings from:
- ResourceBundlePartnerConfig.java
- ResourceBundlePartnerConfig_de.properties (if exists)

### 5. Database Schema (Optional - for future cleanup)

Consider removing OAuth2 columns from partner table:
```sql
-- These columns might exist:
oauth2_message_enabled
oauth2_message_config
oauth2_mdn_enabled
oauth2_mdn_config
```

## Testing After Removal

1. **Compile**: Ensure no compilation errors
2. **UI Test**: Open Partner config dialog, check HTTP Authentication tab displays correctly
3. **Functionality**: Verify basic/no auth still works for messages and MDN
4. **Save/Load**: Create new partner, save, reload - ensure HTTP auth settings persist

## Benefits

✅ Cleaner codebase - removes complex OAuth2 integration  
✅ Simpler UI - HTTP auth tab only shows Basic/None options  
✅ Reduced dependencies - removes OAuth2 utility classes  
✅ Better maintainability - less code to maintain  

## Alternative: Keep OAuth2 But Hide

If you want to keep OAuth2 code for potential future use:
1. Comment out OAuth2 UI components instead of deleting
2. Add `if (false)` guards around OAuth2 code blocks
3. Keep Partner class OAuth2 methods but mark as deprecated

Would you like me to proceed with the full removal, or would you prefer the "hide but keep" approach?
