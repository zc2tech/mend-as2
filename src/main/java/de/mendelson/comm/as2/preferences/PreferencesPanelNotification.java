package de.mendelson.comm.as2.preferences;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.client.AS2StatusBar;
import de.mendelson.comm.as2.clientserver.message.PerformNotificationTestRequest;
import de.mendelson.comm.as2.server.ServerPlugins;
import de.mendelson.util.ButtonUtil;
import de.mendelson.util.JTextFieldLimitDocument;
import de.mendelson.util.systemevents.notification.NotificationData;
import de.mendelson.util.systemevents.notification.clientserver.NotificationGetRequest;
import de.mendelson.util.systemevents.notification.clientserver.NotificationGetResponse;
import de.mendelson.util.systemevents.notification.clientserver.NotificationSetMessage;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.TextOverlay;
import de.mendelson.util.balloontip.BalloonToolTip;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.mailautoconfig.MailServiceConfiguration;
import de.mendelson.util.mailautoconfig.gui.JDialogMailAutoConfigurationDetection;
import de.mendelson.util.oauth2.OAuth2Config;
import de.mendelson.util.oauth2.gui.JDialogOAuth2Config;
import de.mendelson.util.oauth2.gui.JDialogOAuth2ConfigClientCredentials;
import de.mendelson.util.passwordfield.PasswordOverlay;
import de.mendelson.util.systemevents.notification.NotificationDataImplAS2;
import de.mendelson.util.uinotification.UINotification;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Panel to define the directory preferences
 *
 * @author S.Heller
 * @version: $Revision: 71 $
 */
public class PreferencesPanelNotification extends PreferencesPanel {

    /**
     * Localize the GUI
     */
    private final static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    private final BaseClient baseClient;
    private final AS2StatusBar statusbar;
    private OAuth2Config oauth2Config = null;
    private NotificationDataImplAS2 serverSideNotificationData = null;

    private final static MendelsonMultiResolutionImage IMAGE_NOTIFICATION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/notification.svg",
                    JDialogPreferences.IMAGE_HEIGHT);
    private final static MendelsonMultiResolutionImage IMAGE_TESTCONNECTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/testconnection.svg", 24);
    private final static MendelsonMultiResolutionImage IMAGE_OAUTH2
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/oauth2/gui/oauth2.svg", 24);
    private final static MendelsonMultiResolutionImage IMAGE_MAILSERVERDETECTION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/mailautoconfig/gui/detect.svg", 24);

    /**
     * Creates new form PreferencesPanelDirectories
     */
    public PreferencesPanelNotification(BaseClient baseClient, AS2StatusBar statusbar) {
        this.baseClient = baseClient;
        this.statusbar = statusbar;
        this.initComponents();
        ButtonUtil.reformatButtonText(this.jButtonSendTestMail);
        ButtonUtil.reformatButtonText(this.jButtonMailAutoConfig);
        PasswordOverlay.addTo(this.jPasswordFieldSMTPPass,
                rb.getResourceString("label.smtpauthorization.pass.hint"));
        TextOverlay.addTo(this.jTextFieldSMTPUser,
                rb.getResourceString("label.smtpauthorization.user.hint"));
        TextOverlay.addTo(this.jTextFieldHost,
                rb.getResourceString("label.mailhost.hint"));
        TextOverlay.addTo(this.jTextFieldPort,
                rb.getResourceString("label.mailport.hint"));
        this.setMultiresolutionIcons();
        this.jComboBoxSecurity.removeAllItems();
        this.jComboBoxSecurity.addItem(new SecurityEntry(NotificationData.SECURITY_PLAIN));
        this.jComboBoxSecurity.addItem(new SecurityEntry(NotificationData.SECURITY_START_TLS));
        this.jComboBoxSecurity.addItem(new SecurityEntry(NotificationData.SECURITY_TLS));
        // Set combo box font to match text fields
        this.jComboBoxSecurity.setFont(this.jTextFieldHost.getFont());
        this.jTextFieldPort.setDocument(new JTextFieldLimitDocument(5));
        this.initializeHelp();
    }

    private void setMultiresolutionIcons() {
        this.jButtonSendTestMail.setIcon(new ImageIcon(IMAGE_TESTCONNECTION.toMinResolution(24)));
        this.jButtonOAuth2AuthorizationCode.setIcon(new ImageIcon(IMAGE_OAUTH2.toMinResolution(24)));
        this.jButtonOAuth2ClientCredentials.setIcon(new ImageIcon(IMAGE_OAUTH2.toMinResolution(24)));
        this.jButtonMailAutoConfig.setIcon(new ImageIcon(IMAGE_MAILSERVERDETECTION.toMinResolution(24)));        
    }

    private void initializeHelp() {
        this.jPanelUIHelpMaxMailsPerMin.setToolTip(this.rb, "label.maxmailspermin.help");
        this.jPanelUIHelpSMTPPort.setToolTip(this.rb, "label.mailport.help");
    }

    /**
     * Sets new preferences to this panel to changes/modify
     */
    @Override
    public void loadPreferences() {
        // Hide OAuth2 components completely
        this.jRadioButtonOAuth2AuthorizationCode.setVisible(false);
        this.jButtonOAuth2AuthorizationCode.setVisible(false);
        this.jTextFieldOAuth2AuthorizationCode.setVisible(false);
        this.jPanelOAuth2AuthorizationCode.setVisible(false);
        this.jRadioButtonOAuth2ClientCredentials.setVisible(false);
        this.jButtonOAuth2ClientCredentials.setVisible(false);
        this.jTextFieldOAuth2ClientCredentials.setVisible(false);
        this.jPanelOAuth2AuthorizationCode1.setVisible(false);

        if (!this.isPluginActivated(ServerPlugins.PLUGIN_OAUTH2)) {
            this.jRadioButtonOAuth2AuthorizationCode.setEnabled(false);
            this.jButtonOAuth2AuthorizationCode.setEnabled(false);
            this.jTextFieldOAuth2AuthorizationCode.setEnabled(false);
            this.jTextFieldOAuth2AuthorizationCode.setEditable(false);
            this.jRadioButtonOAuth2ClientCredentials.setEnabled(false);
            this.jButtonOAuth2ClientCredentials.setEnabled(false);
            this.jTextFieldOAuth2ClientCredentials.setEnabled(false);
            this.jTextFieldOAuth2ClientCredentials.setEditable(false);
        }
        this.serverSideNotificationData
                = (NotificationDataImplAS2) ((NotificationGetResponse) this.baseClient.sendSync(new NotificationGetRequest())).getData();
        this.jTextFieldHost.setText(this.serverSideNotificationData.getMailServer());
        this.jTextFieldNotificationMail.setText(this.serverSideNotificationData.getNotificationMail());
        this.jTextFieldPort.setText(String.valueOf(this.serverSideNotificationData.getMailServerPort()));
        this.jTextFieldReplyTo.setText(this.serverSideNotificationData.getReplyTo());
        this.switchNotifyCert.setSelected(this.serverSideNotificationData.notifyCertExpire());
        this.switchNotifyTransactionError.setSelected(this.serverSideNotificationData.notifyTransactionError());
        this.switchNotifyCEM.setSelected(this.serverSideNotificationData.notifyCEM());
        this.switchNotifySystemFailure.setSelected(this.serverSideNotificationData.notifySystemFailure());
        this.switchNotifyResend.setSelected(this.serverSideNotificationData.notifyResendDetected());
        this.switchNotifyClientServerProblem.setSelected(this.serverSideNotificationData.notifyClientServerProblem());
        if (this.serverSideNotificationData.usesSMTPAuthCredentials()) {
            this.jRadioButtonAuthorizationCredentials.setSelected(true);
        } else if (this.serverSideNotificationData.usesSMTPAuthOAuth2()) {
            if (this.serverSideNotificationData.getOAuth2Config().getRFCMethod() == OAuth2Config.METHOD_RFC6749_4_1) {
                this.jRadioButtonOAuth2AuthorizationCode.setSelected(true);
            } else {
                this.jRadioButtonOAuth2ClientCredentials.setSelected(true);
            }
        } else {
            this.jRadioButtonAuthorizationNone.setSelected(true);
        }
        this.switchNotifyConnectionProblem.setSelected(this.serverSideNotificationData.notifyConnectionProblem());
        this.switchNotifyPostprocessing.setSelected(this.serverSideNotificationData.notifyPostprocessingProblem());
        if (this.serverSideNotificationData.getSMTPUser() != null) {
            this.jTextFieldSMTPUser.setText(this.serverSideNotificationData.getSMTPUser());
        } else {
            this.jTextFieldSMTPUser.setText("");
        }
        if (this.serverSideNotificationData.getSMTPPass() != null) {
            this.jPasswordFieldSMTPPass.setText(String.valueOf(this.serverSideNotificationData.getSMTPPass()));
        } else {
            this.jPasswordFieldSMTPPass.setText("");
        }
        this.jComboBoxSecurity.setSelectedItem(new SecurityEntry(this.serverSideNotificationData.getConnectionSecurity()));
        this.jTextFieldMaxMailsPerMin.setText(String.valueOf(this.serverSideNotificationData.getMaxNotificationsPerMin()));
        try {
            if (this.serverSideNotificationData.getOAuth2Config() != null) {
                this.oauth2Config = (OAuth2Config) (this.serverSideNotificationData.getOAuth2Config().clone());
            }
        } catch (Throwable e) {
            UINotification.instance().addNotification(e);
        }
        this.displayCurrentOAuth2();
        this.setButtonState();
    }

    private void displayCurrentOAuth2() {
        if (this.oauth2Config == null) {
            this.jTextFieldOAuth2AuthorizationCode.setText("--");
            this.jTextFieldOAuth2ClientCredentials.setText("--");
        } else {
            if (this.oauth2Config.getRFCMethod() == OAuth2Config.METHOD_RFC6749_4_1) {
                this.jTextFieldOAuth2AuthorizationCode.setText(this.oauth2Config.toString());
                this.jTextFieldOAuth2ClientCredentials.setText("--");
            } else {
                this.jTextFieldOAuth2AuthorizationCode.setText("--");
                this.jTextFieldOAuth2ClientCredentials.setText(this.oauth2Config.toString());
            }
        }
    }

    private void setButtonState() {
        this.jTextFieldSMTPUser.setEnabled(this.jRadioButtonAuthorizationCredentials.isSelected());
        this.jPasswordFieldSMTPPass.setEnabled(this.jRadioButtonAuthorizationCredentials.isSelected());
        this.jTextFieldOAuth2AuthorizationCode.setEnabled(this.jRadioButtonOAuth2AuthorizationCode.isSelected());
        this.jTextFieldOAuth2ClientCredentials.setEnabled(this.jRadioButtonOAuth2ClientCredentials.isSelected());
        this.jButtonSendTestMail.setEnabled(
                (!this.jRadioButtonOAuth2AuthorizationCode.isSelected()
                && !this.jRadioButtonOAuth2ClientCredentials.isSelected())
                || (this.jRadioButtonOAuth2AuthorizationCode.isSelected() && this.oauth2Config != null)
                || (this.jRadioButtonOAuth2ClientCredentials.isSelected() && this.oauth2Config != null)
        );
    }

    /**
     * Captures the gui settings, creates a notification data object from these
     * settings and returns this
     */
    private NotificationDataImplAS2 captureGUIData() {
        NotificationDataImplAS2 data = new NotificationDataImplAS2();
        data.setMailServer(this.jTextFieldHost.getText());
        try {
            data.setMailServerPort(Integer.parseInt(this.jTextFieldPort.getText()));
        } catch (NumberFormatException e) {
            //if there is nonsense in this field just take the default value of the object
        }
        data.setNotifyCertExpire(this.switchNotifyCert.isSelected());
        data.setNotifyTransactionError(this.switchNotifyTransactionError.isSelected());
        data.setNotifyCEM(this.switchNotifyCEM.isSelected());
        data.setNotifySystemFailure(this.switchNotifySystemFailure.isSelected());
        data.setNotificationMail(this.jTextFieldNotificationMail.getText());
        data.setUsesSMTPAuthCredentials(this.jRadioButtonAuthorizationCredentials.isSelected());
        data.setUsesSMTPAuthOAuth2(this.jRadioButtonOAuth2AuthorizationCode.isSelected()
                || this.jRadioButtonOAuth2ClientCredentials.isSelected());
        data.setOAuth2Config(this.oauth2Config);
        data.setSMTPUser(this.jTextFieldSMTPUser.getText());
        data.setSMTPPass(this.jPasswordFieldSMTPPass.getPassword());
        data.setReplyTo(this.jTextFieldReplyTo.getText());
        data.setNotifyResendDetected(this.switchNotifyResend.isSelected());
        data.setNotifyConnectionProblem(this.switchNotifyConnectionProblem.isSelected());
        data.setConnectionSecurity(((SecurityEntry) this.jComboBoxSecurity.getSelectedItem()).getValue());
        data.setNotifyPostprocessingProblem(this.switchNotifyPostprocessing.isSelected());
        data.setNotifyClientServerProblem(this.switchNotifyClientServerProblem.isSelected());
        try {
            data.setMaxNotificationsPerMin(Integer.parseInt(this.jTextFieldMaxMailsPerMin.getText()));
        } catch (Exception e) {
            //nop, ignore
        }
        return (data);
    }

    /**
     * Capture the GUI values and store them in the database
     *
     */
    @Override
    public void savePreferences() {
        NotificationData data = this.captureGUIData();
        NotificationSetMessage message = new NotificationSetMessage();
        message.setData(data);
        this.baseClient.sendAsync(message);

    }

    private void sendTestMail() {
        final String uniqueId = this.getClass().getName() + ".sendTestMail." + System.currentTimeMillis();
        Runnable test = new Runnable() {

            @Override
            public void run() {
                try {
                    PreferencesPanelNotification.this.statusbar.startProgressIndeterminate(
                            PreferencesPanelNotification.rb.getResourceString("testmail"), uniqueId);
                    NotificationData data = PreferencesPanelNotification.this.captureGUIData();
                    PerformNotificationTestRequest message = new PerformNotificationTestRequest(data);
                    ClientServerResponse response = PreferencesPanelNotification.this.baseClient.sendSync(message);
                    PreferencesPanelNotification.this.statusbar.stopProgressIfExists(uniqueId);
                    if (response == null) {
                        UINotification.instance().addNotification(
                                null,
                                UINotification.TYPE_ERROR,
                                PreferencesPanelNotification.rb.getResourceString("testmail.title"),
                                PreferencesPanelNotification.rb.getResourceString("testmail.message.error", "Timeout")
                        );
                        return;
                    }
                    if (response.getException() != null) {
                        String body = PreferencesPanelNotification.rb.getResourceString("testmail.message.error",
                                response.getException().getMessage());
                        UINotification.instance().addNotification(
                                null,
                                UINotification.TYPE_ERROR,
                                PreferencesPanelNotification.rb.getResourceString("testmail.title"),
                                body
                        );
                    } else {
                        UINotification.instance().addNotification(
                                null,
                                UINotification.TYPE_SUCCESS,
                                PreferencesPanelNotification.rb.getResourceString("testmail.title"),
                                PreferencesPanelNotification.rb.getResourceString("testmail.message.success",
                                        data.getNotificationMail())
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UINotification.instance().addNotification(e);
                } finally {
                    PreferencesPanelNotification.this.statusbar.stopProgressIfExists(uniqueId);
                }
            }
        };
        GUIClient.submit(test);
    }

    private void setupOAuth2AuthorizationCode() {
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        OAuth2Config config = null;
        if (this.oauth2Config == null) {
            config = new OAuth2Config();
        } else {
            config = this.oauth2Config;
        }
        PreferencesAS2 preferencesClient = new PreferencesAS2();
        String displayMode = preferencesClient.get(PreferencesAS2.DISPLAY_MODE_CLIENT);
        JDialogOAuth2Config dialog = new JDialogOAuth2Config(parentFrame,
                this.baseClient,
                config,
                AS2ServerVersion.getProductName(),
                JDialogOAuth2Config.DIALOG_TYPE_SMTP,
                displayMode
        );
        dialog.setVisible(true);
        if (dialog.okPressed()) {
            this.oauth2Config = config;
        }
        dialog.dispose();
        this.displayCurrentOAuth2();
        this.setButtonState();
    }

    private void setupOAuth2ClientCredentials() {
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        OAuth2Config config = null;
        if (this.oauth2Config == null) {
            config = new OAuth2Config();
        } else {
            config = this.oauth2Config;
        }
        JDialogOAuth2ConfigClientCredentials dialog
                = new JDialogOAuth2ConfigClientCredentials(parentFrame,
                        this.baseClient,
                        config,
                        AS2ServerVersion.getProductName()
                );
        dialog.setVisible(true);
        if (dialog.okPressed()) {
            this.oauth2Config = config;
        }
        dialog.dispose();
        this.displayCurrentOAuth2();
        this.setButtonState();
    }

    /**
     * Tries to find out the mail server settings for a mail address and will
     * take this data into account
     *
     */
    private void performMailAutoConfigurationDetection() {
        JFrame parentFrame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        Runnable detectionThread = new Runnable() {

            @Override
            public void run() {
                try {
                    JDialogMailAutoConfigurationDetection dialog = new JDialogMailAutoConfigurationDetection(parentFrame,
                            PreferencesPanelNotification.this.jTextFieldReplyTo.getText(),
                            PreferencesPanelNotification.this.baseClient,
                            PreferencesPanelNotification.this.statusbar
                    );
                    dialog.setVisible(true);
                    if (dialog.useConfiguration()) {
                        String mailAddress = dialog.getMailAddress();
                        MailServiceConfiguration configuration = dialog.getSelectedConfiguration();
                        dialog.dispose();
                        PreferencesPanelNotification.this.jTextFieldHost.setText(configuration.getServerHost());
                        PreferencesPanelNotification.this.jTextFieldReplyTo.setText(mailAddress);
                        PreferencesPanelNotification.this.jTextFieldPort.setText(String.valueOf(configuration.getPort()));
                        PreferencesPanelNotification.this.jComboBoxSecurity.setSelectedItem(
                                new SecurityEntry(configuration.getSecurity()));
                    }
                } catch (Exception e) {
                    UINotification.instance().addNotification(e);
                }
            }
        };
        GUIClient.submit(detectionThread);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupAuthorization = new javax.swing.ButtonGroup();
        jPanelMargin = new javax.swing.JPanel();
        jPanelSpace = new javax.swing.JPanel();
        jLabelHost = new javax.swing.JLabel();
        jTextFieldHost = new javax.swing.JTextField();
        jLabelPort = new javax.swing.JLabel();
        jTextFieldPort = new javax.swing.JTextField();
        jTextFieldNotificationMail = new javax.swing.JTextField();
        jLabelReplyTo = new javax.swing.JLabel();
        jTextFieldReplyTo = new javax.swing.JTextField();
        jButtonSendTestMail = new javax.swing.JButton();
        jPanelSep = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jPasswordFieldSMTPPass = new javax.swing.JPasswordField();
        jTextFieldSMTPUser = new javax.swing.JTextField();
        jLabelUser = new javax.swing.JLabel();
        jLabelPass = new javax.swing.JLabel();
        jLabelSecurity = new javax.swing.JLabel();
        jComboBoxSecurity = new javax.swing.JComboBox();
        jTextFieldMaxMailsPerMin = new javax.swing.JTextField();
        jPanelNotificationSelection = new javax.swing.JPanel();
        jPanelNotificationSwitch = new javax.swing.JPanel();
        switchNotifyCert = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelNotifyCert = new javax.swing.JLabel();
        jPanelNotificationSwitch1 = new javax.swing.JPanel();
        switchNotifyTransactionError = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelNotifyTransactionError = new javax.swing.JLabel();
        jPanelNotificationSwitch2 = new javax.swing.JPanel();
        switchNotifyCEM = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelNotifyCEM = new javax.swing.JLabel();
        jPanelNotificationSwitch3 = new javax.swing.JPanel();
        switchNotifyConnectionProblem = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelNotifyConnectionProblem = new javax.swing.JLabel();
        jPanelNotificationSwitch4 = new javax.swing.JPanel();
        switchNotifySystemFailure = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelNotifySystemFailure = new javax.swing.JLabel();
        jPanelNotificationSwitch5 = new javax.swing.JPanel();
        switchNotifyResend = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelNotifyResend = new javax.swing.JLabel();
        jPanelNotificationSwitch6 = new javax.swing.JPanel();
        switchNotifyPostprocessing = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelNotifyPostprocessing = new javax.swing.JLabel();
        jPanelNotificationSwitch7 = new javax.swing.JPanel();
        switchNotifyClientServerProblem = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jLabelNotifyClientServerProblem = new javax.swing.JLabel();
        jPanelSpace765 = new javax.swing.JPanel();
        jPanelSpace766 = new javax.swing.JPanel();
        jPanelMaxMailsPerMin = new javax.swing.JPanel();
        jLabelMaxMailsPerMain = new javax.swing.JLabel();
        jPanelUIHelpMaxMailsPerMin = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelSpacer = new javax.swing.JPanel();
        jPanelUIHelpSMTPPort = new de.mendelson.util.balloontip.JPanelUIHelp();
        jPanelUIHelpLabelNotificationMailReceiver = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jRadioButtonAuthorizationNone = new javax.swing.JRadioButton();
        jRadioButtonAuthorizationCredentials = new javax.swing.JRadioButton();
        jRadioButtonOAuth2AuthorizationCode = new javax.swing.JRadioButton();
        jLabelSMTPAuthorization = new javax.swing.JLabel();
        jPanelSpace3434 = new javax.swing.JPanel();
        jPanelOAuth2AuthorizationCode = new javax.swing.JPanel();
        jTextFieldOAuth2AuthorizationCode = new javax.swing.JTextField();
        jButtonOAuth2AuthorizationCode = new javax.swing.JButton();
        jButtonMailAutoConfig = new javax.swing.JButton();
        jPanelOAuth2AuthorizationCode1 = new javax.swing.JPanel();
        jTextFieldOAuth2ClientCredentials = new javax.swing.JTextField();
        jButtonOAuth2ClientCredentials = new javax.swing.JButton();
        jRadioButtonOAuth2ClientCredentials = new javax.swing.JRadioButton();

        setLayout(new java.awt.GridBagLayout());

        jPanelMargin.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelMargin.add(jPanelSpace, gridBagConstraints);

        jLabelHost.setText(this.rb.getResourceString("label.mailhost"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelMargin.add(jLabelHost, gridBagConstraints);

        jTextFieldHost.setMinimumSize(new java.awt.Dimension(180, 22));
        jTextFieldHost.setPreferredSize(new java.awt.Dimension(180, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMargin.add(jTextFieldHost, gridBagConstraints);

        jLabelPort.setText(this.rb.getResourceString( "label.mailport"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelPort, gridBagConstraints);

        jTextFieldPort.setMinimumSize(new java.awt.Dimension(60, 22));
        jTextFieldPort.setPreferredSize(new java.awt.Dimension(60, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jTextFieldPort, gridBagConstraints);

        jTextFieldNotificationMail.setMinimumSize(new java.awt.Dimension(220, 22));
        jTextFieldNotificationMail.setPreferredSize(new java.awt.Dimension(220, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.gridwidth = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMargin.add(jTextFieldNotificationMail, gridBagConstraints);

        jLabelReplyTo.setText(this.rb.getResourceString( "label.replyto"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelMargin.add(jLabelReplyTo, gridBagConstraints);

        jTextFieldReplyTo.setMinimumSize(new java.awt.Dimension(180, 22));
        jTextFieldReplyTo.setPreferredSize(new java.awt.Dimension(180, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMargin.add(jTextFieldReplyTo, gridBagConstraints);

        jButtonSendTestMail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jButtonSendTestMail.setText(this.rb.getResourceString("button.testmail"));
        jButtonSendTestMail.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSendTestMail.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonSendTestMail.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSendTestMail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendTestMailActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 21;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanelMargin.add(jButtonSendTestMail, gridBagConstraints);

        jPanelSep.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelSep.add(jSeparator1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 27;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanelMargin.add(jPanelSep, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 27;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanelMargin.add(jSeparator2, gridBagConstraints);

        jPasswordFieldSMTPPass.setMinimumSize(new java.awt.Dimension(180, 22));
        jPasswordFieldSMTPPass.setPreferredSize(new java.awt.Dimension(180, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelMargin.add(jPasswordFieldSMTPPass, gridBagConstraints);

        jTextFieldSMTPUser.setMinimumSize(new java.awt.Dimension(180, 22));
        jTextFieldSMTPUser.setPreferredSize(new java.awt.Dimension(180, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanelMargin.add(jTextFieldSMTPUser, gridBagConstraints);

        jLabelUser.setText(this.rb.getResourceString( "label.smtpauthorization.user"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelUser, gridBagConstraints);

        jLabelPass.setText(this.rb.getResourceString( "label.smtpauthorization.pass"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jLabelPass, gridBagConstraints);

        jLabelSecurity.setText(this.rb.getResourceString( "label.security"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelMargin.add(jLabelSecurity, gridBagConstraints);

        jComboBoxSecurity.setPreferredSize(new java.awt.Dimension(180, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMargin.add(jComboBoxSecurity, gridBagConstraints);

        jTextFieldMaxMailsPerMin.setMinimumSize(new java.awt.Dimension(40, 22));
        jTextFieldMaxMailsPerMin.setPreferredSize(new java.awt.Dimension(40, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 26;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanelMargin.add(jTextFieldMaxMailsPerMin, gridBagConstraints);

        jPanelNotificationSelection.setLayout(new java.awt.GridBagLayout());

        jPanelNotificationSwitch.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch.add(switchNotifyCert, gridBagConstraints);

        jLabelNotifyCert.setText(this.rb.getResourceString( "checkbox.notifycertexpire"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch.add(jLabelNotifyCert, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jPanelNotificationSwitch, gridBagConstraints);

        jPanelNotificationSwitch1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch1.add(switchNotifyTransactionError, gridBagConstraints);

        jLabelNotifyTransactionError.setText(this.rb.getResourceString( "checkbox.notifycertexpire"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch1.add(jLabelNotifyTransactionError, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jPanelNotificationSwitch1, gridBagConstraints);

        jPanelNotificationSwitch2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch2.add(switchNotifyCEM, gridBagConstraints);

        jLabelNotifyCEM.setText(this.rb.getResourceString("checkbox.notifycem"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch2.add(jLabelNotifyCEM, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jPanelNotificationSwitch2, gridBagConstraints);

        jPanelNotificationSwitch3.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch3.add(switchNotifyConnectionProblem, gridBagConstraints);

        jLabelNotifyConnectionProblem.setText(this.rb.getResourceString( "checkbox.notifyconnectionproblem"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch3.add(jLabelNotifyConnectionProblem, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jPanelNotificationSwitch3, gridBagConstraints);

        jPanelNotificationSwitch4.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch4.add(switchNotifySystemFailure, gridBagConstraints);

        jLabelNotifySystemFailure.setText(this.rb.getResourceString( "checkbox.notifyfailure"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch4.add(jLabelNotifySystemFailure, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jPanelNotificationSwitch4, gridBagConstraints);

        jPanelNotificationSwitch5.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch5.add(switchNotifyResend, gridBagConstraints);

        jLabelNotifyResend.setText(this.rb.getResourceString( "checkbox.notifyresend"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch5.add(jLabelNotifyResend, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jPanelNotificationSwitch5, gridBagConstraints);

        jPanelNotificationSwitch6.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch6.add(switchNotifyPostprocessing, gridBagConstraints);

        jLabelNotifyPostprocessing.setText(this.rb.getResourceString( "checkbox.notifypostprocessing"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch6.add(jLabelNotifyPostprocessing, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jPanelNotificationSwitch6, gridBagConstraints);

        jPanelNotificationSwitch7.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch7.add(switchNotifyClientServerProblem, gridBagConstraints);

        jLabelNotifyClientServerProblem.setText(this.rb.getResourceString( "checkbox.notifyclientserver"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        jPanelNotificationSwitch7.add(jLabelNotifyClientServerProblem, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelNotificationSelection.add(jPanelNotificationSwitch7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelNotificationSelection.add(jPanelSpace765, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelNotificationSelection.add(jPanelSpace766, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 21;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 0);
        jPanelMargin.add(jPanelNotificationSelection, gridBagConstraints);

        jPanelMaxMailsPerMin.setLayout(new java.awt.GridBagLayout());

        jLabelMaxMailsPerMain.setText(this.rb.getResourceString("label.maxmailspermin"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMaxMailsPerMin.add(jLabelMaxMailsPerMain, gridBagConstraints);

        jPanelUIHelpMaxMailsPerMin.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        jPanelMaxMailsPerMin.add(jPanelUIHelpMaxMailsPerMin, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelMaxMailsPerMin.add(jPanelSpacer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanelMargin.add(jPanelMaxMailsPerMin, gridBagConstraints);

        jPanelUIHelpSMTPPort.setPreferredSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 6;
        jPanelMargin.add(jPanelUIHelpSMTPPort, gridBagConstraints);

        jPanelUIHelpLabelNotificationMailReceiver.setToolTipText(this.rb.getResourceString( "label.notificationmail.help"));
        jPanelUIHelpLabelNotificationMailReceiver.setText(this.rb.getResourceString( "label.notificationmail"));
        jPanelUIHelpLabelNotificationMailReceiver.setTriangleAlignment(BalloonToolTip.TRIANGLE_ALIGNMENT_BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanelMargin.add(jPanelUIHelpLabelNotificationMailReceiver, gridBagConstraints);

        buttonGroupAuthorization.add(jRadioButtonAuthorizationNone);
        jRadioButtonAuthorizationNone.setText(this.rb.getResourceString("label.smtpauthorization.none"));
        jRadioButtonAuthorizationNone.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonAuthorizationNoneItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jRadioButtonAuthorizationNone, gridBagConstraints);

        buttonGroupAuthorization.add(jRadioButtonAuthorizationCredentials);
        jRadioButtonAuthorizationCredentials.setText(this.rb.getResourceString("label.smtpauthorization.credentials"));
        jRadioButtonAuthorizationCredentials.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonAuthorizationCredentialsItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jRadioButtonAuthorizationCredentials, gridBagConstraints);

        buttonGroupAuthorization.add(jRadioButtonOAuth2AuthorizationCode);
        jRadioButtonOAuth2AuthorizationCode.setText(this.rb.getResourceString("label.smtpauthorization.oauth2.authorizationcode"));
        jRadioButtonOAuth2AuthorizationCode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonOAuth2AuthorizationCodeItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jRadioButtonOAuth2AuthorizationCode, gridBagConstraints);

        jLabelSMTPAuthorization.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabelSMTPAuthorization.setText(this.rb.getResourceString("label.smtpauthorization.header"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelMargin.add(jLabelSMTPAuthorization, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 27;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jPanelSpace3434, gridBagConstraints);

        jPanelOAuth2AuthorizationCode.setLayout(new java.awt.GridBagLayout());

        jTextFieldOAuth2AuthorizationCode.setEditable(false);
        jTextFieldOAuth2AuthorizationCode.setText("--");
        jTextFieldOAuth2AuthorizationCode.setMinimumSize(new java.awt.Dimension(400, 22));
        jTextFieldOAuth2AuthorizationCode.setPreferredSize(new java.awt.Dimension(400, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelOAuth2AuthorizationCode.add(jTextFieldOAuth2AuthorizationCode, gridBagConstraints);

        jButtonOAuth2AuthorizationCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jButtonOAuth2AuthorizationCode.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonOAuth2AuthorizationCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOAuth2AuthorizationCodeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 4;
        jPanelOAuth2AuthorizationCode.add(jButtonOAuth2AuthorizationCode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelMargin.add(jPanelOAuth2AuthorizationCode, gridBagConstraints);

        jButtonMailAutoConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jButtonMailAutoConfig.setText(this.rb.getResourceString("button.mailserverdetection"));
        jButtonMailAutoConfig.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMailAutoConfig.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButtonMailAutoConfig.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonMailAutoConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMailAutoConfigActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 21;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanelMargin.add(jButtonMailAutoConfig, gridBagConstraints);

        jPanelOAuth2AuthorizationCode1.setLayout(new java.awt.GridBagLayout());

        jTextFieldOAuth2ClientCredentials.setEditable(false);
        jTextFieldOAuth2ClientCredentials.setText("--");
        jTextFieldOAuth2ClientCredentials.setMinimumSize(new java.awt.Dimension(400, 22));
        jTextFieldOAuth2ClientCredentials.setPreferredSize(new java.awt.Dimension(400, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelOAuth2AuthorizationCode1.add(jTextFieldOAuth2ClientCredentials, gridBagConstraints);

        jButtonOAuth2ClientCredentials.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jButtonOAuth2ClientCredentials.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButtonOAuth2ClientCredentials.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOAuth2ClientCredentialsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 4;
        jPanelOAuth2AuthorizationCode1.add(jButtonOAuth2ClientCredentials, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanelMargin.add(jPanelOAuth2AuthorizationCode1, gridBagConstraints);

        buttonGroupAuthorization.add(jRadioButtonOAuth2ClientCredentials);
        jRadioButtonOAuth2ClientCredentials.setText(this.rb.getResourceString("label.smtpauthorization.oauth2.clientcredentials"));
        jRadioButtonOAuth2ClientCredentials.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonOAuth2ClientCredentialsItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMargin.add(jRadioButtonOAuth2ClientCredentials, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanelMargin, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSendTestMailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendTestMailActionPerformed
        this.sendTestMail();
    }//GEN-LAST:event_jButtonSendTestMailActionPerformed

    private void jButtonOAuth2AuthorizationCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOAuth2AuthorizationCodeActionPerformed
        this.setupOAuth2AuthorizationCode();
    }//GEN-LAST:event_jButtonOAuth2AuthorizationCodeActionPerformed

    private void jRadioButtonAuthorizationNoneItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonAuthorizationNoneItemStateChanged
        this.setButtonState();
    }//GEN-LAST:event_jRadioButtonAuthorizationNoneItemStateChanged

    private void jRadioButtonAuthorizationCredentialsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonAuthorizationCredentialsItemStateChanged
        this.setButtonState();
    }//GEN-LAST:event_jRadioButtonAuthorizationCredentialsItemStateChanged

    private void jRadioButtonOAuth2AuthorizationCodeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonOAuth2AuthorizationCodeItemStateChanged
        this.setButtonState();
    }//GEN-LAST:event_jRadioButtonOAuth2AuthorizationCodeItemStateChanged

    private void jButtonMailAutoConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMailAutoConfigActionPerformed
        this.performMailAutoConfigurationDetection();
    }//GEN-LAST:event_jButtonMailAutoConfigActionPerformed

    private void jButtonOAuth2ClientCredentialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOAuth2ClientCredentialsActionPerformed
        this.setupOAuth2ClientCredentials();
    }//GEN-LAST:event_jButtonOAuth2ClientCredentialsActionPerformed

    private void jRadioButtonOAuth2ClientCredentialsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonOAuth2ClientCredentialsItemStateChanged
        this.setButtonState();
    }//GEN-LAST:event_jRadioButtonOAuth2ClientCredentialsItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupAuthorization;
    private javax.swing.JButton jButtonMailAutoConfig;
    private javax.swing.JButton jButtonOAuth2AuthorizationCode;
    private javax.swing.JButton jButtonOAuth2ClientCredentials;
    private javax.swing.JButton jButtonSendTestMail;
    private javax.swing.JComboBox jComboBoxSecurity;
    private javax.swing.JLabel jLabelHost;
    private javax.swing.JLabel jLabelMaxMailsPerMain;
    private javax.swing.JLabel jLabelNotifyCEM;
    private javax.swing.JLabel jLabelNotifyCert;
    private javax.swing.JLabel jLabelNotifyClientServerProblem;
    private javax.swing.JLabel jLabelNotifyConnectionProblem;
    private javax.swing.JLabel jLabelNotifyPostprocessing;
    private javax.swing.JLabel jLabelNotifyResend;
    private javax.swing.JLabel jLabelNotifySystemFailure;
    private javax.swing.JLabel jLabelNotifyTransactionError;
    private javax.swing.JLabel jLabelPass;
    private javax.swing.JLabel jLabelPort;
    private javax.swing.JLabel jLabelReplyTo;
    private javax.swing.JLabel jLabelSMTPAuthorization;
    private javax.swing.JLabel jLabelSecurity;
    private javax.swing.JLabel jLabelUser;
    private javax.swing.JPanel jPanelMargin;
    private javax.swing.JPanel jPanelMaxMailsPerMin;
    private javax.swing.JPanel jPanelNotificationSelection;
    private javax.swing.JPanel jPanelNotificationSwitch;
    private javax.swing.JPanel jPanelNotificationSwitch1;
    private javax.swing.JPanel jPanelNotificationSwitch2;
    private javax.swing.JPanel jPanelNotificationSwitch3;
    private javax.swing.JPanel jPanelNotificationSwitch4;
    private javax.swing.JPanel jPanelNotificationSwitch5;
    private javax.swing.JPanel jPanelNotificationSwitch6;
    private javax.swing.JPanel jPanelNotificationSwitch7;
    private javax.swing.JPanel jPanelOAuth2AuthorizationCode;
    private javax.swing.JPanel jPanelOAuth2AuthorizationCode1;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpace3434;
    private javax.swing.JPanel jPanelSpace765;
    private javax.swing.JPanel jPanelSpace766;
    private javax.swing.JPanel jPanelSpacer;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelNotificationMailReceiver;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpMaxMailsPerMin;
    private de.mendelson.util.balloontip.JPanelUIHelp jPanelUIHelpSMTPPort;
    private javax.swing.JPasswordField jPasswordFieldSMTPPass;
    private javax.swing.JRadioButton jRadioButtonAuthorizationCredentials;
    private javax.swing.JRadioButton jRadioButtonAuthorizationNone;
    private javax.swing.JRadioButton jRadioButtonOAuth2AuthorizationCode;
    private javax.swing.JRadioButton jRadioButtonOAuth2ClientCredentials;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextFieldHost;
    private javax.swing.JTextField jTextFieldMaxMailsPerMin;
    private javax.swing.JTextField jTextFieldNotificationMail;
    private javax.swing.JTextField jTextFieldOAuth2AuthorizationCode;
    private javax.swing.JTextField jTextFieldOAuth2ClientCredentials;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTextField jTextFieldReplyTo;
    private javax.swing.JTextField jTextFieldSMTPUser;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchNotifyCEM;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchNotifyCert;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchNotifyClientServerProblem;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchNotifyConnectionProblem;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchNotifyPostprocessing;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchNotifyResend;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchNotifySystemFailure;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchNotifyTransactionError;
    // End of variables declaration//GEN-END:variables

    @Override
    public ImageIcon getIcon() {
        return (new ImageIcon(IMAGE_NOTIFICATION));
    }

    @Override
    public String getTabResource() {
        return ("tab.notification");
    }

    @Override
    public boolean preferencesAreModified() {
        if (this.serverSideNotificationData == null) {
            return (false);
        }
        String serverSidePreferencesStr = this.serverSideNotificationData.toXML(0);
        String newPreferencesStr = this.captureGUIData().toXML(0);
        return (!serverSidePreferencesStr.equals(newPreferencesStr));
    }

    private static class SecurityEntry {

        private int value = NotificationData.SECURITY_PLAIN;

        public SecurityEntry(int value) {
            this.value = value;
        }

        public int getDefaultPort() {
            if (this.value == NotificationData.SECURITY_TLS) {
                return (465);
            } else {
                return (25);
            }
        }

        @Override
        public String toString() {
            if (this.getValue() == NotificationData.SECURITY_PLAIN) {
                return ("--");
            } else if (this.getValue() == NotificationData.SECURITY_START_TLS) {
                return ("STARTTLS");
            } else {
                return ("TLS");
            }
        }

        /**
         * @return the value
         */
        public int getValue() {
            return value;
        }

        /**
         * Overwrite the equal method of object
         *
         * @param anObject object ot compare
         */
        @Override
        public boolean equals(Object anObject) {
            if (anObject == this) {
                return (true);
            }
            if (anObject != null && anObject instanceof SecurityEntry) {
                SecurityEntry entry = (SecurityEntry) anObject;
                return (entry.value == this.value);
            }
            return (false);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 29 * hash + this.value;
            return hash;
        }
    }
}
