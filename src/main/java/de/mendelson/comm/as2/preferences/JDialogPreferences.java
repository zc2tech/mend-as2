//$Header: /as2/de/mendelson/comm/as2/preferences/JDialogPreferences.java 65    9/12/24 16:02 Heller $
package de.mendelson.comm.as2.preferences;

import de.mendelson.util.ColorUtil;
import de.mendelson.util.DisplayMode;
import de.mendelson.util.ImageButtonBar;
import de.mendelson.util.ImageButtonBarUI;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.uinotification.UINotification;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Dialog to configure a single partner
 *
 * @author S.Heller
 * @version $Revision: 65 $
 */
public class JDialogPreferences extends JDialog {

    public static final int IMAGE_HEIGHT = ImageButtonBarUI.DEFAULT_IMAGE_HEIGHT;

    private final static MendelsonMultiResolutionImage IMAGE_LANGUAGE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/language.svg",
                    IMAGE_HEIGHT);
    private final static MendelsonMultiResolutionImage IMAGE_COLORBLIND
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/color_blindness.svg", 20,
                    36, MendelsonMultiResolutionImage.SVGScalingOption.KEEP_HEIGHT);
    private final static MendelsonMultiResolutionImage IMAGE_DARKMODE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/darkmode.svg", 20);
    private final static MendelsonMultiResolutionImage IMAGE_LIGHTMODE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/lightmode.svg", 20);
    private final static MendelsonMultiResolutionImage IMAGE_HICONTRASTMODE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/hicontrastmode.svg", 20);

    /**
     * ResourceBundle to localize the GUI
     */
    private static MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }
    /**
     * The language should be stored in the client preferences, no client-server
     * comm required here
     */
    private final PreferencesAS2 clientPreferences = new PreferencesAS2();
    private String preferencesStrAtLoadTime = "";
    /**
     * stores all available panels
     */
    private final List<PreferencesPanel> panelList = new ArrayList<PreferencesPanel>();
    private final ImageButtonBar buttonBar;
    private boolean okPressed = false;

    /**
     * Creates new form JDialogPartnerConfig
     *
     * @param activatedPlugins A String containing all activated plugins of the
     * system
     */
    public JDialogPreferences(JFrame parent, List<PreferencesPanel> panelList,
            String selectedTab, String activatedPlugins) {
        super(parent, true);
        this.panelList.addAll(panelList);
        initComponents();
        this.setMultiresolutionIcons();
        this.setupCountrySelection();
        this.setDisplayModeRadio();
        if (UIManager.getColor("Objects.RedStatus") != null) {
            this.jLabelLanguageInfo.setForeground(UIManager.getColor("Objects.RedStatus"));
        } else {
            ColorUtil.autoCorrectForegroundColor(this.jLabelLanguageInfo);
        }
        if (this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("de")) {
            this.jRadioButtonLangDE.setSelected(true);
        } else if (this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("en")) {
            this.jRadioButtonLangEN.setSelected(true);
        } else if (this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("fr")) {
            this.jRadioButtonLangFR.setSelected(true);
        } else if (this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("it")) {
            this.jRadioButtonLangIT.setSelected(true);
        }else if (this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("es")) {
            this.jRadioButtonLangES.setSelected(true);
        }else if (this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("pt")) {
            this.jRadioButtonLangPT.setSelected(true);
        }
        String selectedCountryCode = this.clientPreferences.get(PreferencesAS2.COUNTRY).toUpperCase();
        this.jListCountry.setSelectedValue(new DisplayCountry(selectedCountryCode), true);
        boolean colorBlindness = this.clientPreferences.getBoolean(PreferencesAS2.COLOR_BLINDNESS);
        this.switchColorBlindness.setSelected(colorBlindness);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        for (PreferencesPanel preferencePanel : this.panelList) {
            //initialize the panels
            preferencePanel.setActivatedPlugins(activatedPlugins);
            preferencePanel.loadPreferences();
            //add the panels to the layout
            this.jPanelEdit.add(preferencePanel, gridBagConstraints);
        }
        this.buttonBar = new ImageButtonBar(ImageButtonBar.HORIZONTAL)
                .setPreferredButtonSize(85, 84);
        boolean selected = selectedTab == null;
        for (PreferencesPanel preferencePanel : this.panelList) {
            if (selectedTab != null && preferencePanel.getTabResource().equals(selectedTab)) {
                selected = true;
            }
            String tabText = rb.getResourceString(preferencePanel.getTabResource());
            if (tabText.length() > 15) {
                int blankIndex = tabText.indexOf(" ");
                if (blankIndex > 0) {
                    tabText = tabText.substring(0, blankIndex);
                }
            }
            buttonBar.addButton(
                    preferencePanel.getIcon(),
                    tabText,
                    new JComponent[]{preferencePanel},
                    selected);
            selected = false;
        }
        buttonBar.addButton(
                new ImageIcon(IMAGE_LANGUAGE.toMinResolution(IMAGE_HEIGHT)),
                rb.getResourceString("tab.language"),
                new JComponent[]{this.jPanelLanguage},
                false)
                .build();
        //add button bar
        this.jPanelButtonBar.setLayout(new BorderLayout());
        this.jPanelButtonBar.add(buttonBar, BorderLayout.CENTER);
        this.preferencesStrAtLoadTime = this.captureSettingsToStr();
        this.getRootPane().setDefaultButton(this.jButtonOk);

    }

    private void setMultiresolutionIcons() {
        this.jLabelIconBlind.setIcon(new ImageIcon(IMAGE_COLORBLIND.toMinResolution(20)));
        this.jLabelDarkMode.setIcon(new ImageIcon(IMAGE_DARKMODE.toMinResolution(20)));
        this.jLabelLightMode.setIcon(new ImageIcon(IMAGE_LIGHTMODE.toMinResolution(20)));
        this.jLabelHiContrastMode.setIcon(new ImageIcon(IMAGE_HICONTRASTMODE.toMinResolution(20)));
    }

    private void captureGUIValues() {
        boolean clientRestartRequired = false;
        if (this.jRadioButtonLangDE.isSelected()) {
            if (!this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("de")) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.LANGUAGE, "de");
        } else if (this.jRadioButtonLangEN.isSelected()) {
            if (!this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("en")) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.LANGUAGE, "en");
        } else if (this.jRadioButtonLangFR.isSelected()) {
            if (!this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("fr")) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.LANGUAGE, "fr");
        } else if (this.jRadioButtonLangIT.isSelected()) {
            if (!this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("it")) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.LANGUAGE, "it");
        }else if (this.jRadioButtonLangES.isSelected()) {
            if (!this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("es")) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.LANGUAGE, "es");
        }else if (this.jRadioButtonLangPT.isSelected()) {
            if (!this.clientPreferences.get(PreferencesAS2.LANGUAGE).equals("pt")) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.LANGUAGE, "pt");
        }
        if (this.jListCountry.getSelectedValue() != null) {
            String newCountryCode = this.jListCountry.getSelectedValue().getCountryCode();
            if (!this.clientPreferences.get(PreferencesAS2.COUNTRY).equals(newCountryCode)) {
                clientRestartRequired = true;
            }
            this.clientPreferences.put(PreferencesAS2.COUNTRY, newCountryCode);
        }
        if (this.clientPreferences.getBoolean(PreferencesAS2.COLOR_BLINDNESS) != (this.switchColorBlindness.isSelected())) {
            clientRestartRequired = true;
        }
        this.clientPreferences.putBoolean(PreferencesAS2.COLOR_BLINDNESS, this.switchColorBlindness.isSelected());
        if ((this.jRadioButtonDarkMode.isSelected()
                && !this.clientPreferences.get(PreferencesAS2.DISPLAY_MODE_CLIENT).equalsIgnoreCase(DisplayMode.DARK))
                || (this.jRadioButtonLiteMode.isSelected()
                && !this.clientPreferences.get(PreferencesAS2.DISPLAY_MODE_CLIENT).equalsIgnoreCase(DisplayMode.LIGHT))
                || (this.jRadioButtonHiContrastMode.isSelected()
                && !this.clientPreferences.get(PreferencesAS2.DISPLAY_MODE_CLIENT).equalsIgnoreCase(DisplayMode.HICONTRAST))) {
            if (this.jRadioButtonDarkMode.isSelected()) {
                this.clientPreferences.put(PreferencesAS2.DISPLAY_MODE_CLIENT, DisplayMode.DARK);
            }
            if (this.jRadioButtonHiContrastMode.isSelected()) {
                this.clientPreferences.put(PreferencesAS2.DISPLAY_MODE_CLIENT, DisplayMode.HICONTRAST);
            }
            if (this.jRadioButtonLiteMode.isSelected()) {
                this.clientPreferences.put(PreferencesAS2.DISPLAY_MODE_CLIENT, "LIGHT");
            }
            clientRestartRequired = true;
        }
        if (clientRestartRequired) {
            UINotification.instance().addNotification(
                    PreferencesPanelMDN.IMAGE_PREFS,
                    UINotification.TYPE_INFORMATION,
                    rb.getResourceString("title"),
                    rb.getResourceString("warning.clientrestart.required"));
        }
    }

    /**
     * Helper method to find out if there are changes in the GUI before storing
     * them to the server
     */
    private String captureSettingsToStr() {
        StringBuilder builder = new StringBuilder();
        builder.append(PreferencesAS2.LANGUAGE).append("=");
        if (this.jRadioButtonLangDE.isSelected()) {
            builder.append("de");
        } else if (this.jRadioButtonLangEN.isSelected()) {
            builder.append("en");
        } else if (this.jRadioButtonLangFR.isSelected()) {
            builder.append("fr");
        } else if (this.jRadioButtonLangIT.isSelected()) {
            builder.append("it");
        }else if (this.jRadioButtonLangES.isSelected()) {
            builder.append("es");
        }else if (this.jRadioButtonLangPT.isSelected()) {
            builder.append("pt");
        }
        builder.append(";");
        builder.append(PreferencesAS2.DISPLAY_MODE_CLIENT).append("=");
        if (this.jRadioButtonDarkMode.isSelected()) {
            builder.append(DisplayMode.DARK);
        } else if (this.jRadioButtonLiteMode.isSelected()) {
            builder.append(DisplayMode.LIGHT);
        } else if (this.jRadioButtonHiContrastMode.isSelected()) {
            builder.append(DisplayMode.HICONTRAST);
        }
        builder.append(";");
        builder.append(PreferencesAS2.COLOR_BLINDNESS).append("=")
                .append(this.switchColorBlindness.isSelected()).append(";");
        String countryCode = this.jListCountry.getSelectedValue().getCountryCode();
        builder.append(PreferencesAS2.COUNTRY).append("=")
                .append(countryCode).append(";");
        return (builder.toString());
    }

    private boolean preferencesAreModified() {
        return (!this.preferencesStrAtLoadTime.equals(this.captureSettingsToStr()));
    }

    /**
     * Fills in the available countries of the system into the list
     */
    private void setupCountrySelection() {
        Set<String> countryCodes = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2);
        DefaultListModel listModel = (DefaultListModel) this.jListCountry.getModel();
        listModel.clear();
        List<DisplayCountry> displayList = new ArrayList<DisplayCountry>();
        for (String countryCode : countryCodes) {
            displayList.add(new DisplayCountry(countryCode));
        }
        //sort german special chars the right way if the locale is german...
        Collections.sort(displayList);
        DisplayCountry[] countryArray = new DisplayCountry[displayList.size()];
        displayList.toArray(countryArray);
        this.jListCountry.setListData(countryArray);
    }

    private void setDisplayModeRadio() {
        String displayMode = this.clientPreferences.get(PreferencesAS2.DISPLAY_MODE_CLIENT);
        if (displayMode.equalsIgnoreCase(DisplayMode.DARK)) {
            this.jRadioButtonDarkMode.setSelected(true);
        } else if (displayMode.equalsIgnoreCase(DisplayMode.HICONTRAST)) {
            this.jRadioButtonHiContrastMode.setSelected(true);
        } else {
            this.jRadioButtonLiteMode.setSelected(true);
        }
    }

    /**
     * Displays a warning if changes have been made to the settings and
     * afterwards the user pressed the cancel windows "X" or anything similar
     */
    private void showWarningOnPreferencesCanceled() {
        if (this.okPressed) {
            //no warning required, user pressed ok
            return;
        }
        boolean changesHaveBeenMade = false;
        for (PreferencesPanel preferencePanel : this.panelList) {
            if (preferencePanel.preferencesAreModified()) {
                changesHaveBeenMade = true;
                break;
            }
        }
        if (!changesHaveBeenMade) {
            changesHaveBeenMade = !this.captureSettingsToStr().equals(this.preferencesStrAtLoadTime);
        }
        if (changesHaveBeenMade) {
            UINotification.instance().addNotification(
                    PreferencesPanelMDN.IMAGE_PREFS,
                    UINotification.TYPE_WARNING,
                    rb.getResourceString("title"),
                    rb.getResourceString("warning.changes.canceled"));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupLanguage = new javax.swing.ButtonGroup();
        buttonGroupDarkMode = new javax.swing.ButtonGroup();
        jPanelEdit = new javax.swing.JPanel();
        jPanelLanguage = new javax.swing.JPanel();
        jRadioButtonLangDE = new javax.swing.JRadioButton();
        jRadioButtonLangEN = new javax.swing.JRadioButton();
        jRadioButtonLangFR = new javax.swing.JRadioButton();
        jRadioButtonLangIT = new javax.swing.JRadioButton();
        jPanelSpace = new javax.swing.JPanel();
        jLabelLanguageInfo = new javax.swing.JLabel();
        jScrollPaneCountry = new javax.swing.JScrollPane();
        jListCountry = new javax.swing.JList<>();
        jPanelSpace44 = new javax.swing.JPanel();
        jPanelColorBlindness = new javax.swing.JPanel();
        jLabelIconBlind = new javax.swing.JLabel();
        jLabelColorBlindness = new javax.swing.JLabel();
        switchColorBlindness = new de.mendelson.util.toggleswitch.ToggleSwitch();
        jPanelDarkMode = new javax.swing.JPanel();
        jRadioButtonDarkMode = new javax.swing.JRadioButton();
        jRadioButtonLiteMode = new javax.swing.JRadioButton();
        jLabelDarkMode = new javax.swing.JLabel();
        jLabelLightMode = new javax.swing.JLabel();
        jLabelHiContrastMode = new javax.swing.JLabel();
        jRadioButtonHiContrastMode = new javax.swing.JRadioButton();
        jPanelUIHelpLabelCountry = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelUIHelpLabelLanguage = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jPanelSpace544 = new javax.swing.JPanel();
        jPanelSpace545 = new javax.swing.JPanel();
        jPanelUIHelpLabelDisplayMode = new de.mendelson.util.balloontip.JPanelUIHelpLabel();
        jRadioButtonLangES = new javax.swing.JRadioButton();
        jRadioButtonLangPT = new javax.swing.JRadioButton();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jPanelButtonBar = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(this.rb.getResourceString( "title"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelEdit.setLayout(new java.awt.GridBagLayout());

        jPanelLanguage.setLayout(new java.awt.GridBagLayout());

        buttonGroupLanguage.add(jRadioButtonLangDE);
        jRadioButtonLangDE.setText("Deutsch");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 5, 5);
        jPanelLanguage.add(jRadioButtonLangDE, gridBagConstraints);

        buttonGroupLanguage.add(jRadioButtonLangEN);
        jRadioButtonLangEN.setText("English");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelLanguage.add(jRadioButtonLangEN, gridBagConstraints);

        buttonGroupLanguage.add(jRadioButtonLangFR);
        jRadioButtonLangFR.setText("<HTML>Fran&#231;ais</HTML>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelLanguage.add(jRadioButtonLangFR, gridBagConstraints);

        buttonGroupLanguage.add(jRadioButtonLangIT);
        jRadioButtonLangIT.setText("Italiano");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelLanguage.add(jRadioButtonLangIT, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        jPanelLanguage.add(jPanelSpace, gridBagConstraints);

        jLabelLanguageInfo.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabelLanguageInfo.setForeground(new java.awt.Color(255, 51, 0));
        jLabelLanguageInfo.setText(this.rb.getResourceString("info.restart.client"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        jPanelLanguage.add(jLabelLanguageInfo, gridBagConstraints);

        jScrollPaneCountry.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jListCountry.setModel(new DefaultListModel());
        jListCountry.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListCountry.setVisibleRowCount(15);
        jScrollPaneCountry.setViewportView(jListCountry);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 5);
        jPanelLanguage.add(jScrollPaneCountry, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanelLanguage.add(jPanelSpace44, gridBagConstraints);

        jPanelColorBlindness.setLayout(new java.awt.GridBagLayout());

        jLabelIconBlind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelColorBlindness.add(jLabelIconBlind, gridBagConstraints);

        jLabelColorBlindness.setText(this.rb.getResourceString( "label.colorblindness"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelColorBlindness.add(jLabelColorBlindness, gridBagConstraints);

        switchColorBlindness.setDisplayStatusText(true);
        switchColorBlindness.setHorizontalTextPosition(SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 5);
        jPanelColorBlindness.add(switchColorBlindness, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelLanguage.add(jPanelColorBlindness, gridBagConstraints);

        jPanelDarkMode.setLayout(new java.awt.GridBagLayout());

        buttonGroupDarkMode.add(jRadioButtonDarkMode);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDarkMode.add(jRadioButtonDarkMode, gridBagConstraints);

        buttonGroupDarkMode.add(jRadioButtonLiteMode);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDarkMode.add(jRadioButtonLiteMode, gridBagConstraints);

        jLabelDarkMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jLabelDarkMode.setText(this.rb.getResourceString( "label.darkmode"));
        jLabelDarkMode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelDarkModeMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDarkMode.add(jLabelDarkMode, gridBagConstraints);

        jLabelLightMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jLabelLightMode.setText(this.rb.getResourceString( "label.litemode"));
        jLabelLightMode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelLightModeMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDarkMode.add(jLabelLightMode, gridBagConstraints);

        jLabelHiContrastMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/preferences/missing_image24x24.gif"))); // NOI18N
        jLabelHiContrastMode.setText(this.rb.getResourceString( "label.hicontrastmode"));
        jLabelHiContrastMode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelHiContrastModeMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDarkMode.add(jLabelHiContrastMode, gridBagConstraints);

        buttonGroupDarkMode.add(jRadioButtonHiContrastMode);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelDarkMode.add(jRadioButtonHiContrastMode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 20, 5);
        jPanelLanguage.add(jPanelDarkMode, gridBagConstraints);

        jPanelUIHelpLabelCountry.setToolTipText(this.rb.getResourceString( "label.country.help"));
        jPanelUIHelpLabelCountry.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jPanelUIHelpLabelCountry.setText(this.rb.getResourceString( "label.country"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 0);
        jPanelLanguage.add(jPanelUIHelpLabelCountry, gridBagConstraints);

        jPanelUIHelpLabelLanguage.setToolTipText(this.rb.getResourceString( "label.language.help"));
        jPanelUIHelpLabelLanguage.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jPanelUIHelpLabelLanguage.setText(this.rb.getResourceString( "label.language"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 0);
        jPanelLanguage.add(jPanelUIHelpLabelLanguage, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        jPanelLanguage.add(jPanelSpace544, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        jPanelLanguage.add(jPanelSpace545, gridBagConstraints);

        jPanelUIHelpLabelDisplayMode.setToolTipText(this.rb.getResourceString( "label.displaymode.help"));
        jPanelUIHelpLabelDisplayMode.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jPanelUIHelpLabelDisplayMode.setText(this.rb.getResourceString( "label.displaymode"));
        jPanelUIHelpLabelDisplayMode.setTooltipWidth(150);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 0);
        jPanelLanguage.add(jPanelUIHelpLabelDisplayMode, gridBagConstraints);

        buttonGroupLanguage.add(jRadioButtonLangES);
        jRadioButtonLangES.setText("Español");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelLanguage.add(jRadioButtonLangES, gridBagConstraints);

        buttonGroupLanguage.add(jRadioButtonLangPT);
        jRadioButtonLangPT.setText("Português");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanelLanguage.add(jRadioButtonLangPT, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelEdit.add(jPanelLanguage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanelEdit, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(this.rb.getResourceString( "button.ok" ));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButtons.add(jButtonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtons, gridBagConstraints);

        jPanelButtonBar.setMinimumSize(new java.awt.Dimension(10, 63));
        jPanelButtonBar.setPreferredSize(new java.awt.Dimension(10, 63));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtonBar, gridBagConstraints);

        setSize(new java.awt.Dimension(1157, 771));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.okPressed = true;
        for (PreferencesPanel panel : this.panelList) {
            if (panel.preferencesAreModified()) {
                panel.savePreferences();
            }
        }
        this.setVisible(false);
        this.captureGUIValues();
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jLabelDarkModeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelDarkModeMouseClicked
        this.jRadioButtonDarkMode.setSelected(true);
    }//GEN-LAST:event_jLabelDarkModeMouseClicked

    private void jLabelLightModeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelLightModeMouseClicked
        this.jRadioButtonLiteMode.setSelected(true);
    }//GEN-LAST:event_jLabelLightModeMouseClicked

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        this.showWarningOnPreferencesCanceled();
    }//GEN-LAST:event_formWindowClosed

    private void jLabelHiContrastModeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelHiContrastModeMouseClicked
        this.jRadioButtonHiContrastMode.setSelected(true);
    }//GEN-LAST:event_jLabelHiContrastModeMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupDarkMode;
    private javax.swing.ButtonGroup buttonGroupLanguage;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelColorBlindness;
    private javax.swing.JLabel jLabelDarkMode;
    private javax.swing.JLabel jLabelHiContrastMode;
    private javax.swing.JLabel jLabelIconBlind;
    private javax.swing.JLabel jLabelLanguageInfo;
    private javax.swing.JLabel jLabelLightMode;
    private javax.swing.JList<DisplayCountry> jListCountry;
    private javax.swing.JPanel jPanelButtonBar;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelColorBlindness;
    private javax.swing.JPanel jPanelDarkMode;
    private javax.swing.JPanel jPanelEdit;
    private javax.swing.JPanel jPanelLanguage;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JPanel jPanelSpace44;
    private javax.swing.JPanel jPanelSpace544;
    private javax.swing.JPanel jPanelSpace545;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelCountry;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelDisplayMode;
    private de.mendelson.util.balloontip.JPanelUIHelpLabel jPanelUIHelpLabelLanguage;
    private javax.swing.JRadioButton jRadioButtonDarkMode;
    private javax.swing.JRadioButton jRadioButtonHiContrastMode;
    private javax.swing.JRadioButton jRadioButtonLangDE;
    private javax.swing.JRadioButton jRadioButtonLangEN;
    private javax.swing.JRadioButton jRadioButtonLangES;
    private javax.swing.JRadioButton jRadioButtonLangFR;
    private javax.swing.JRadioButton jRadioButtonLangIT;
    private javax.swing.JRadioButton jRadioButtonLangPT;
    private javax.swing.JRadioButton jRadioButtonLiteMode;
    private javax.swing.JScrollPane jScrollPaneCountry;
    private de.mendelson.util.toggleswitch.ToggleSwitch switchColorBlindness;
    // End of variables declaration//GEN-END:variables

    private static class DisplayCountry implements Comparable<DisplayCountry> {

        private final String countryCode;
        private final String displayString;

        public DisplayCountry(String countryCode) {
            this.countryCode = countryCode.toUpperCase();
            Locale locale = new Locale(Locale.getDefault().getLanguage(), countryCode);
            this.displayString = locale.getDisplayCountry() + " (" + countryCode + ")";
        }

        @Override
        public String toString() {
            return (this.displayString);
        }

        @Override
        public boolean equals(Object anObject) {
            if (anObject == this) {
                return (true);
            }
            if (anObject != null && anObject instanceof DisplayCountry) {
                DisplayCountry entry = (DisplayCountry) anObject;
                return (entry.getCountryCode().equals(this.getCountryCode()));
            }
            return (false);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + Objects.hashCode(this.getCountryCode());
            return hash;
        }

        @Override
        public int compareTo(DisplayCountry displayCountry) {
            Collator collator = Collator.getInstance(Locale.getDefault());
            //include french and german special chars into the sort mechanism
            return (collator.compare(this.displayString, displayCountry.displayString));
        }

        /**
         * @return the countryCode
         */
        public String getCountryCode() {
            return countryCode;
        }
    }
}
