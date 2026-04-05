package de.mendelson.comm.as2.preferences;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.comm.as2.usermanagement.clientserver.UserHttpAuthPreferenceRequest;
import de.mendelson.comm.as2.usermanagement.clientserver.UserHttpAuthPreferenceResponse;
import de.mendelson.comm.as2.usermanagement.clientserver.UserHttpAuthPreferenceSaveRequest;
import de.mendelson.comm.as2.usermanagement.clientserver.UserHttpAuthPreferenceSaveResponse;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.messages.ClientServerResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Panel to manage HTTP Authentication preferences for partners
 * Used in SwingUI preferences dialog (admin user context)
 */
public class PreferencesPanelHttpAuth extends PreferencesPanel {

    private static final int ADMIN_USER_ID = 1;

    protected final static MendelsonMultiResolutionImage IMAGE_HTTPAUTH
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/preferences/preferences.svg",
                    JDialogPreferences.IMAGE_HEIGHT);

    private JTable table;
    private DefaultTableModel tableModel;
    private BaseClient baseClient;
    private List<Partner> remotePartners;
    private MecResourceBundle rb;

    // Store preferences data (partnerId -> type -> field -> value)
    private Map<Integer, Map<String, Map<String, String>>> preferencesData;

    private static final int COL_PARTNER = 0;
    private static final int COL_MESSAGE_AUTH = 1;
    private static final int COL_MESSAGE_USER = 2;
    private static final int COL_MESSAGE_PASS = 3;
    private static final int COL_MDN_AUTH = 4;
    private static final int COL_MDN_USER = 5;
    private static final int COL_MDN_PASS = 6;

    public PreferencesPanelHttpAuth(BaseClient baseClient) {
        super();
        this.baseClient = baseClient;
        this.preferencesData = new HashMap<>();

        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePreferences.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info label
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel(this.rb.getResourceString("httpauth.info"));
        topPanel.add(infoLabel);
        this.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {
            this.rb.getResourceString("httpauth.col.partner"),
            this.rb.getResourceString("httpauth.col.message.auth"),
            this.rb.getResourceString("httpauth.col.message.user"),
            this.rb.getResourceString("httpauth.col.message.pass"),
            this.rb.getResourceString("httpauth.col.mdn.auth"),
            this.rb.getResourceString("httpauth.col.mdn.user"),
            this.rb.getResourceString("httpauth.col.mdn.pass")
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == COL_MESSAGE_AUTH || columnIndex == COL_MDN_AUTH) {
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // All columns editable except partner name
                return column != COL_PARTNER;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        TableColumn partnerCol = table.getColumnModel().getColumn(COL_PARTNER);
        partnerCol.setPreferredWidth(200);
        partnerCol.setMinWidth(150);

        TableColumn msgAuthCol = table.getColumnModel().getColumn(COL_MESSAGE_AUTH);
        msgAuthCol.setPreferredWidth(80);
        msgAuthCol.setMaxWidth(80);

        TableColumn msgUserCol = table.getColumnModel().getColumn(COL_MESSAGE_USER);
        msgUserCol.setPreferredWidth(150);

        TableColumn msgPassCol = table.getColumnModel().getColumn(COL_MESSAGE_PASS);
        msgPassCol.setPreferredWidth(150);

        TableColumn mdnAuthCol = table.getColumnModel().getColumn(COL_MDN_AUTH);
        mdnAuthCol.setPreferredWidth(80);
        mdnAuthCol.setMaxWidth(80);

        TableColumn mdnUserCol = table.getColumnModel().getColumn(COL_MDN_USER);
        mdnUserCol.setPreferredWidth(150);

        TableColumn mdnPassCol = table.getColumnModel().getColumn(COL_MDN_PASS);
        mdnPassCol.setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        try {
            // Load partners via client-server
            PartnerListRequest partnerRequest = new PartnerListRequest(PartnerListRequest.LIST_ALL);
            ClientServerResponse partnerResponse = this.baseClient.sendSync(partnerRequest);

            if (partnerResponse instanceof PartnerListResponse) {
                List<Partner> allPartners = ((PartnerListResponse) partnerResponse).getList();
                remotePartners = new ArrayList<>();

                // Filter to only remote partners
                for (Partner partner : allPartners) {
                    if (!partner.isLocalStation()) {
                        remotePartners.add(partner);
                    }
                }
            }

            // Load HTTP Auth preferences via client-server
            UserHttpAuthPreferenceRequest prefRequest = new UserHttpAuthPreferenceRequest(ADMIN_USER_ID);
            ClientServerResponse prefResponse = this.baseClient.sendSync(prefRequest);

            preferencesData = new HashMap<>();
            if (prefResponse instanceof UserHttpAuthPreferenceResponse) {
                preferencesData = ((UserHttpAuthPreferenceResponse) prefResponse).getPreferences();
            }

            // Clear existing rows
            tableModel.setRowCount(0);

            // Add row for each remote partner
            for (Partner partner : remotePartners) {
                String partnerName = partner.getName();
                int partnerId = partner.getDBId();

                // Get preferences for this partner
                String messageUser = "";
                String messagePass = "";
                String mdnUser = "";
                String mdnPass = "";

                if (preferencesData.containsKey(partnerId)) {
                    Map<String, Map<String, String>> types = preferencesData.get(partnerId);

                    if (types.containsKey("message")) {
                        Map<String, String> messageFields = types.get("message");
                        messageUser = messageFields.getOrDefault("username", "");
                        messagePass = messageFields.getOrDefault("password", "");
                    }

                    if (types.containsKey("mdn")) {
                        Map<String, String> mdnFields = types.get("mdn");
                        mdnUser = mdnFields.getOrDefault("username", "");
                        mdnPass = mdnFields.getOrDefault("password", "");
                    }
                }

                boolean messageAuthEnabled = messageUser != null && !messageUser.isEmpty();
                boolean mdnAuthEnabled = mdnUser != null && !mdnUser.isEmpty();

                Object[] row = {
                    partnerName,
                    messageAuthEnabled,
                    messageUser,
                    messagePass,
                    mdnAuthEnabled,
                    mdnUser,
                    mdnPass
                };

                tableModel.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load HTTP authentication preferences: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void savePreferences() {
        try {
            // Build preferences data structure from table
            UserHttpAuthPreferenceSaveRequest request = new UserHttpAuthPreferenceSaveRequest(ADMIN_USER_ID);

            for (int row = 0; row < tableModel.getRowCount(); row++) {
                Partner partner = remotePartners.get(row);
                int partnerId = partner.getDBId();

                boolean messageAuthEnabled = (Boolean) tableModel.getValueAt(row, COL_MESSAGE_AUTH);
                String messageUser = (String) tableModel.getValueAt(row, COL_MESSAGE_USER);
                String messagePass = (String) tableModel.getValueAt(row, COL_MESSAGE_PASS);

                boolean mdnAuthEnabled = (Boolean) tableModel.getValueAt(row, COL_MDN_AUTH);
                String mdnUser = (String) tableModel.getValueAt(row, COL_MDN_USER);
                String mdnPass = (String) tableModel.getValueAt(row, COL_MDN_PASS);

                // Only save if auth is enabled and username is provided
                if (messageAuthEnabled && messageUser != null && !messageUser.trim().isEmpty()) {
                    request.addPreference(partnerId, "message", "username", messageUser.trim());
                    request.addPreference(partnerId, "message", "password", messagePass != null ? messagePass : "");
                }

                if (mdnAuthEnabled && mdnUser != null && !mdnUser.trim().isEmpty()) {
                    request.addPreference(partnerId, "mdn", "username", mdnUser.trim());
                    request.addPreference(partnerId, "mdn", "password", mdnPass != null ? mdnPass : "");
                }
            }

            // Send save request to server
            ClientServerResponse response = this.baseClient.sendSync(request);

            if (response instanceof UserHttpAuthPreferenceSaveResponse) {
                UserHttpAuthPreferenceSaveResponse saveResponse = (UserHttpAuthPreferenceSaveResponse) response;
                if (!saveResponse.isSuccess()) {
                    JOptionPane.showMessageDialog(this,
                            "Failed to save preferences: " + saveResponse.getErrorMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to save HTTP authentication preferences: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void loadPreferences() {
        // Reload data from database
        loadData();
    }

    @Override
    public ImageIcon getIcon() {
        return new ImageIcon(IMAGE_HTTPAUTH.toMinResolution(JDialogPreferences.IMAGE_HEIGHT));
    }

    @Override
    public String getTabResource() {
        return "tab.httpauth";
    }

    @Override
    public boolean preferencesAreModified() {
        // Since we don't persist yet, return false
        return false;
    }
}
