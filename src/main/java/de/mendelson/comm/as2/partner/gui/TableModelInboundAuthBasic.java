package de.mendelson.comm.as2.partner.gui;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerInboundAuthCredential;
import de.mendelson.util.MecResourceBundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

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
 * Table model for basic authentication credentials only
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class TableModelInboundAuthBasic extends AbstractTableModel {

    public static final int COL_USERNAME = 0;
    public static final int COL_PASSWORD = 1;
    public static final int COL_ENABLED = 2;

    private final List<PartnerInboundAuthCredential> data =
        Collections.synchronizedList(new ArrayList<PartnerInboundAuthCredential>());
    private Partner partner;
    private final MecResourceBundle rb;

    public TableModelInboundAuthBasic() {
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                ResourceBundlePartnerPanel.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getRowCount() {
        synchronized (data) {
            return data.size();
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case COL_USERNAME:
                return this.rb.getResourceString("inboundauth.table.col.username");
            case COL_PASSWORD:
                return this.rb.getResourceString("inboundauth.table.col.password");
            case COL_ENABLED:
                return this.rb.getResourceString("inboundauth.table.col.enabled");
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column == COL_ENABLED) {
            return Boolean.class;
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Object getValueAt(int row, int column) {
        synchronized (data) {
            PartnerInboundAuthCredential credential = data.get(row);
            switch (column) {
                case COL_USERNAME:
                    return credential.getUsername() != null ? credential.getUsername() : "";
                case COL_PASSWORD:
                    // Show password in plain text for editing
                    return credential.getPassword() != null ? credential.getPassword() : "";
                case COL_ENABLED:
                    return credential.isEnabled();
            }
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        synchronized (data) {
            PartnerInboundAuthCredential credential = data.get(row);
            switch (column) {
                case COL_USERNAME:
                    credential.setUsername((String) value);
                    break;
                case COL_PASSWORD:
                    credential.setPassword((String) value);
                    break;
                case COL_ENABLED:
                    credential.setEnabled((Boolean) value);
                    break;
            }
            if (partner != null) {
                updatePartnerCredentials();
            }
        }
        fireTableCellUpdated(row, column);
    }

    public void passNewData(Partner partner) {
        this.partner = partner;
        synchronized (data) {
            data.clear();
            if (partner != null) {
                // Only load basic auth credentials
                for (PartnerInboundAuthCredential cred : partner.getInboundAuthCredentialsList()) {
                    if (cred.getAuthType() == PartnerInboundAuthCredential.AUTH_TYPE_BASIC) {
                        data.add(cred);
                    }
                }
            }
        }
        fireTableDataChanged();
    }

    public void addRow(PartnerInboundAuthCredential credential) {
        synchronized (data) {
            data.add(credential);
            if (partner != null) {
                updatePartnerCredentials();
            }
        }
        fireTableDataChanged();
    }

    public void deleteRow(int row) {
        synchronized (data) {
            if (row >= 0 && row < data.size()) {
                data.remove(row);
                if (partner != null) {
                    updatePartnerCredentials();
                }
            }
        }
        fireTableDataChanged();
    }

    public PartnerInboundAuthCredential getRow(int row) {
        synchronized (data) {
            if (row >= 0 && row < data.size()) {
                return data.get(row);
            }
        }
        return null;
    }

    /**
     * Update partner's credential list with all credentials (basic + cert)
     * This merges basic auth credentials from this table with certificate credentials from partner
     */
    private void updatePartnerCredentials() {
        if (partner == null) {
            return;
        }

        // Get current full list from partner
        List<PartnerInboundAuthCredential> currentList = partner.getInboundAuthCredentialsList();
        List<PartnerInboundAuthCredential> allCredentials = new ArrayList<>();

        // Add all basic auth credentials from THIS table
        synchronized (data) {
            allCredentials.addAll(data);
        }

        // Add all certificate credentials from current partner list (preserve them)
        for (PartnerInboundAuthCredential cred : currentList) {
            if (cred.getAuthType() == PartnerInboundAuthCredential.AUTH_TYPE_CERTIFICATE) {
                allCredentials.add(cred);
            }
        }

        partner.setInboundAuthCredentialsList(allCredentials);
    }
}
