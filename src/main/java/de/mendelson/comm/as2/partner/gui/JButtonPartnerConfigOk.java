package de.mendelson.comm.as2.partner.gui;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.util.ColorUtil;
import java.awt.Color;
import java.net.URL;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/*
 * Modifications Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */
/**
 * Ok Button for the partner config. This checks if there is any error in the
 * partner config and renders the fields that are erroneous
 *
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class JButtonPartnerConfigOk extends JButton {

    //HEX #FFCCCC. Do not just modify this as the partner icons have the same
    //background color in an error case. This has to reworked, too if this value is changed.
    private final Color errorColor = new Color(255, 204, 204);
    private JTreePartner tree;
    private JTextField jTextFieldName;
    private JTextField jTextFieldReceiptURL;
    private JTextField jTextFieldMDNURL;
    private JTextField jTextFieldAS2Id;
    private JPanel jPanelErrorInConfig;
    private boolean changesAllowed;

    private Partner remotePartner;

    public void initialize(JTreePartner tree, JTextField jTextFieldName, JTextField jTextFieldAS2Id,
            JTextField jTextFieldURL, JTextField jTextFieldMDNURL, boolean changesAllowed,
            JPanel jPanelErrorInConfig) {
        this.tree = tree;
        this.changesAllowed = changesAllowed;
        this.jTextFieldName = jTextFieldName;
        this.jTextFieldAS2Id = jTextFieldAS2Id;
        this.jTextFieldReceiptURL = jTextFieldURL;
        this.jTextFieldMDNURL = jTextFieldMDNURL;
        this.jPanelErrorInConfig = jPanelErrorInConfig;
    }

    public void setPartner(Partner remotePartner) {
        this.remotePartner = remotePartner;
    }

    /**
     * Checks if the passed URLs contain a leading protocol entry
     *
     */
    private boolean checkURLProtocol(Partner checkPartner) {
        String receiverURL = checkPartner.getURL();
        String mdnURL = checkPartner.getMdnURL();
        boolean error = false;
        if (!checkPartner.isLocalStation()) {
            //no local station
            if (receiverURL == null || (!receiverURL.startsWith("http://")
                    && !receiverURL.startsWith("https://"))) {
                //graphical modifications for current displayed partner only!
                if (this.remotePartner.equals(checkPartner)) {
                    this.markErrorInTextField(this.jTextFieldReceiptURL);
                }
                error = true;
            } else {
                try {
                    URL testURL = java.net.URI.create(receiverURL).toURL();
                    // Port validation: Accept explicit port OR default port (80 for HTTP, 443 for HTTPS)
                    int port = testURL.getPort();
                    int defaultPort = testURL.getDefaultPort();
                    // Error only if no port AND no default port available
                    if (port == -1 && defaultPort == -1) {
                        //graphical modifications for current displayed partner only!
                        if (this.remotePartner.equals(checkPartner)) {
                            this.markErrorInTextField(this.jTextFieldReceiptURL);
                        }
                        error = true;
                    }
                } catch (Exception e) {
                    //graphical modifications for current displayed partner only!
                    if (this.remotePartner.equals(checkPartner)) {
                        this.markErrorInTextField(this.jTextFieldReceiptURL);
                    }
                    error = true;
                }
            }
            if (!error) {
                //graphical modifications for current displayed partner only!
                if (this.remotePartner.equals(checkPartner)) {
                    this.markNoErrorInTextField(this.jTextFieldReceiptURL);
                }
            }
        } else {
            //local station
            if (mdnURL == null
                    || (!mdnURL.startsWith("http://") && !mdnURL.startsWith("https://"))) {
                //graphical modifications for current displayed partner only!
                if (this.remotePartner.equals(checkPartner)) {
                    this.markErrorInTextField(this.jTextFieldMDNURL);
                }
                error = true;
            } else {
                try {
                    URL testURL = java.net.URI.create(mdnURL).toURL();
                    // Port validation: Accept explicit port OR default port (80 for HTTP, 443 for HTTPS)
                    int port = testURL.getPort();
                    int defaultPort = testURL.getDefaultPort();
                    // Error only if no port AND no default port available
                    if (port == -1 && defaultPort == -1) {
                        //graphical modifications for current displayed partner only!
                        if (this.remotePartner.equals(checkPartner)) {
                            this.markErrorInTextField(this.jTextFieldMDNURL);
                        }
                        error = true;
                    }
                } catch (Exception e) {
                    //graphical modifications for current displayed partner only!
                    if (this.remotePartner.equals(checkPartner)) {
                        this.markErrorInTextField(this.jTextFieldMDNURL);
                    }
                    error = true;
                }
            }
            if (!error) {
                //graphical modifications for current displayed partner only!
                if (this.remotePartner.equals(checkPartner)) {
                    this.markNoErrorInTextField(this.jTextFieldMDNURL);
                }
            }
        }
        return (error);
    }

    /**
     * Returns the number of partner names found in the passed partner list
     */
    private int getNameCountInList(String partnerName, List<Partner> partnerList) {
        int count = 0;
        for (Partner partner : partnerList) {
            if (partner.getName().equals(partnerName)) {
                count++;
            }
        }
        return (count);
    }

    /**
     * Returns the number of as2 ids names found in the passed partner list
     */
    private int getAS2IdCountInList(String as2Id, List<Partner> partnerList) {
        int count = 0;
        for (Partner partner : partnerList) {
            if (partner.getAS2Identification().equals(as2Id)) {
                count++;
            }
        }
        return (count);
    }

    /**
     * Checks if new name is unique and changes color in textfield if not
     */
    private boolean checkForNonUniqueValues(Partner checkPartner, List<Partner> partnerList) {
        boolean error = false;
        String newName = checkPartner.getName();
        int nameCount = this.getNameCountInList(newName, partnerList);
        if (newName != null && !newName.trim().isEmpty() && nameCount == 1) {
            //graphical modifications for current displayed partner only!
            if (this.remotePartner.equals(checkPartner)) {
                this.markNoErrorInTextField(this.jTextFieldName);
            }
        } else {
            //graphical modifications for current displayed partner only!
            if (this.remotePartner.equals(checkPartner)) {
                this.markErrorInTextField(this.jTextFieldName);
            }
            error = true;
        }
        String newAS2Id = checkPartner.getAS2Identification();
        int idCount = this.getAS2IdCountInList(newAS2Id, partnerList);
        if (newAS2Id != null && !newAS2Id.trim().isEmpty() && idCount == 1) {
            //graphical modifications for current displayed partner only!
            if (this.remotePartner.equals(checkPartner)) {
                this.markNoErrorInTextField(this.jTextFieldAS2Id);                
            }
        } else {
            //graphical modifications for current displayed partner only!
            if (this.remotePartner.equals(checkPartner)) {
                this.markErrorInTextField(this.jTextFieldAS2Id);
            }
            error = true;
        }
        return (error);
    }

    /**
     * Checks if new name is unique and changes color in textfield if not
     */
    private boolean checkForNonUniqueOrInvalidValues(Partner checkPartner, List<Partner> partnerList) {
        boolean error = false;
        error = error || this.checkForNonUniqueValues(checkPartner, partnerList);
        error = error || this.checkURLProtocol(checkPartner);
        // error = error || this.checkLocalhostAsURL(checkPartner);
        return (error);
    }

    private void markErrorInTextField(JTextField textfield) {
        textfield.setBackground(this.errorColor);
        ColorUtil.autoCorrectForegroundColor(textfield);
    }

    private void markNoErrorInTextField(JTextField textfield) {
        textfield.setBackground(UIManager.getDefaults().getColor("TextField.background"));
        textfield.setForeground(UIManager.getDefaults().getColor("TextField.foreground"));
    }

    public void computeErrorState() {
        if (!this.changesAllowed) {
            this.setEnabled(false);
            return;
        } else {
            final List<Partner> partnerList = this.tree.getAllPartner();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    boolean errorInConfig = false;
                    for (Partner checkPartner : partnerList) {
                        boolean error = JButtonPartnerConfigOk.this.checkForNonUniqueOrInvalidValues(
                                checkPartner, partnerList);
                        boolean hasErrorBefore = checkPartner.hasConfigError();
                        if (error != hasErrorBefore) {
                            checkPartner.setConfigError(error);
                            JButtonPartnerConfigOk.this.tree.partnerChanged(checkPartner);
                        }
                        if (error) {
                            errorInConfig = true;
                        }
                    }
                    JButtonPartnerConfigOk.this.setEnabled(!errorInConfig);
                    JButtonPartnerConfigOk.this.jPanelErrorInConfig.setVisible(errorInConfig);
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }

}
