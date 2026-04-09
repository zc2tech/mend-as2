/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 *
 * Enhancement for partner visibility control by Julian Xu (2026)
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 */

package de.mendelson.comm.as2.partner.gui;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.usermanagement.WebUIUser;
import de.mendelson.comm.as2.usermanagement.clientserver.UserListRequest;
import de.mendelson.comm.as2.usermanagement.clientserver.UserListResponse;
import de.mendelson.util.clientserver.BaseClient;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * Panel for configuring partner visibility to WebUI users
 */
public class JPanelPartnerVisibility extends JPanel {

    private Partner partner;
    private BaseClient baseClient;

    private JRadioButton jRadioVisibleToAll;
    private JRadioButton jRadioVisibleToSpecific;
    private ButtonGroup visibilityGroup;
    private JPanel jPanelUserList;
    private JScrollPane jScrollPaneUsers;
    private Map<Integer, JCheckBox> userCheckboxes;
    private List<WebUIUser> allUsers;

    public JPanelPartnerVisibility(BaseClient baseClient, Partner partner) {
        this.baseClient = baseClient;
        this.partner = partner;
        this.userCheckboxes = new HashMap<>();

        initComponents();
        loadUsers();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Info panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel infoLabel = new JLabel(
            "<html>Control which WebUI users can see and use this partner when sending messages.</html>"
        );
        topPanel.add(infoLabel);

        // Radio buttons panel
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        radioPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        jRadioVisibleToAll = new JRadioButton("Visible to all WebUI users");
        jRadioVisibleToSpecific = new JRadioButton("Visible to specific users only");

        visibilityGroup = new ButtonGroup();
        visibilityGroup.add(jRadioVisibleToAll);
        visibilityGroup.add(jRadioVisibleToSpecific);

        jRadioVisibleToAll.setSelected(true); // Default

        radioPanel.add(jRadioVisibleToAll);
        radioPanel.add(Box.createVerticalStrut(5));
        radioPanel.add(jRadioVisibleToSpecific);

        topPanel.add(radioPanel);
        add(topPanel, BorderLayout.NORTH);

        // User list panel
        jPanelUserList = new JPanel();
        jPanelUserList.setLayout(new BoxLayout(jPanelUserList, BoxLayout.Y_AXIS));
        jPanelUserList.setBorder(BorderFactory.createTitledBorder("Select Users"));

        jScrollPaneUsers = new JScrollPane(jPanelUserList);
        jScrollPaneUsers.setPreferredSize(new Dimension(400, 300));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(jScrollPaneUsers, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Enable/disable user list based on radio selection
        jRadioVisibleToAll.addActionListener(e -> {
            setUserListEnabled(false);
            // Update partner immediately when selection changes
            if (partner != null) {
                partner.setVisibleToUserIds(new ArrayList<>());
            }
        });
        jRadioVisibleToSpecific.addActionListener(e -> {
            setUserListEnabled(true);
            // Update partner with currently selected users
            updatePartnerFromSelection();
        });

        setUserListEnabled(false); // Initially disabled
    }

    private void setUserListEnabled(boolean enabled) {
        jScrollPaneUsers.setEnabled(enabled);
        jPanelUserList.setEnabled(enabled);
        for (JCheckBox checkbox : userCheckboxes.values()) {
            checkbox.setEnabled(enabled);
        }
    }

    private void loadUsers() {
        Thread loadThread = new Thread(() -> {
            try {
                UserListRequest request = new UserListRequest();
                UserListResponse response = (UserListResponse) baseClient.sendSync(request);

                if (response != null && response.getException() == null) {
                    allUsers = response.getUsers();

                    SwingUtilities.invokeLater(() -> {
                        populateUserList();
                        loadVisibilitySettings();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        loadThread.setDaemon(true);
        loadThread.start();
    }

    private void populateUserList() {
        jPanelUserList.removeAll();
        userCheckboxes.clear();

        if (allUsers == null || allUsers.isEmpty()) {
            jPanelUserList.add(new JLabel("No users available"));
            jPanelUserList.revalidate();
            jPanelUserList.repaint();
            return;
        }

        for (WebUIUser user : allUsers) {
            String displayText = user.getUsername();
            if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) {
                displayText = String.format("%s (%s)", user.getUsername(), user.getFullName());
            }
            JCheckBox checkbox = new JCheckBox(displayText);
            checkbox.addActionListener(e -> {
                // Update partner immediately when checkbox changes
                updatePartnerFromSelection();
            });
            userCheckboxes.put(user.getId(), checkbox);
            jPanelUserList.add(checkbox);
            jPanelUserList.add(Box.createVerticalStrut(5));
        }

        jPanelUserList.revalidate();
        jPanelUserList.repaint();
    }

    private void loadVisibilitySettings() {
        if (partner != null && partner.getVisibleToUserIds() != null) {
            List<Integer> visibleUserIds = partner.getVisibleToUserIds();

            if (visibleUserIds.isEmpty()) {
                jRadioVisibleToAll.setSelected(true);
                setUserListEnabled(false);
            } else {
                jRadioVisibleToSpecific.setSelected(true);
                setUserListEnabled(true);

                for (Integer userId : visibleUserIds) {
                    JCheckBox checkbox = userCheckboxes.get(userId);
                    if (checkbox != null) {
                        checkbox.setSelected(true);
                    }
                }
            }
        }
    }

    /**
     * Update partner object with current selections
     */
    private void updatePartnerFromSelection() {
        if (partner != null) {
            List<Integer> selectedUserIds = new ArrayList<>();

            if (jRadioVisibleToSpecific.isSelected()) {
                for (Map.Entry<Integer, JCheckBox> entry : userCheckboxes.entrySet()) {
                    if (entry.getValue().isSelected()) {
                        selectedUserIds.add(entry.getKey());
                    }
                }
            }

            partner.setVisibleToUserIds(selectedUserIds);
        }
    }

    /**
     * Save visibility settings to the partner object
     */
    public void saveToPartner(Partner partner) {
        List<Integer> selectedUserIds = new ArrayList<>();

        if (jRadioVisibleToSpecific.isSelected()) {
            for (Map.Entry<Integer, JCheckBox> entry : userCheckboxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    selectedUserIds.add(entry.getKey());
                }
            }
        }

        partner.setVisibleToUserIds(selectedUserIds);
    }
}
