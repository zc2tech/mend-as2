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
    private static final int COL_MESSAGE_USER = 1;
    private static final int COL_MESSAGE_PASS = 2;
    private static final int COL_MDN_USER = 3;
    private static final int COL_MDN_PASS = 4;

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
            this.rb.getResourceString("httpauth.col.message.user"),
            this.rb.getResourceString("httpauth.col.message.pass"),
            this.rb.getResourceString("httpauth.col.mdn.user"),
            this.rb.getResourceString("httpauth.col.mdn.pass")
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
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

        TableColumn msgUserCol = table.getColumnModel().getColumn(COL_MESSAGE_USER);
        msgUserCol.setPreferredWidth(150);

        TableColumn msgPassCol = table.getColumnModel().getColumn(COL_MESSAGE_PASS);
        msgPassCol.setPreferredWidth(150);

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

                Object[] row = {
                    partnerName,
                    messageUser,
                    messagePass,
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

                String messageUser = (String) tableModel.getValueAt(row, COL_MESSAGE_USER);
                String messagePass = (String) tableModel.getValueAt(row, COL_MESSAGE_PASS);

                String mdnUser = (String) tableModel.getValueAt(row, COL_MDN_USER);
                String mdnPass = (String) tableModel.getValueAt(row, COL_MDN_PASS);

                // Only save if username is provided (non-empty)
                if (messageUser != null && !messageUser.trim().isEmpty()) {
                    request.addPreference(partnerId, "message", "username", messageUser.trim());
                    request.addPreference(partnerId, "message", "password", messagePass != null ? messagePass : "");
                }

                if (mdnUser != null && !mdnUser.trim().isEmpty()) {
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
