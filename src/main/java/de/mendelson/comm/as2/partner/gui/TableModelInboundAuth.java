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
 * Table model for inbound authentication credentials
 *
 * @author Julian Xu
 * @version $Revision: 1 $
 */
public class TableModelInboundAuth extends AbstractTableModel {

    public static final int COL_TYPE = 0;
    public static final int COL_USERNAME = 1;
    public static final int COL_PASSWORD = 2;
    public static final int COL_CERT_FINGERPRINT = 3;
    public static final int COL_ENABLED = 4;

    private final List<PartnerInboundAuthCredential> data =
        Collections.synchronizedList(new ArrayList<PartnerInboundAuthCredential>());
    private Partner partner;
    private final MecResourceBundle rb;

    public TableModelInboundAuth() {
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                ResourceBundlePartnerPanel.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    @Override
    public int getColumnCount() {
        return 5;
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
            case COL_TYPE:
                return this.rb.getResourceString("inboundauth.table.col.type");
            case COL_USERNAME:
                return this.rb.getResourceString("inboundauth.table.col.username");
            case COL_PASSWORD:
                return this.rb.getResourceString("inboundauth.table.col.password");
            case COL_CERT_FINGERPRINT:
                return this.rb.getResourceString("inboundauth.table.col.certificate");
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
        synchronized (data) {
            PartnerInboundAuthCredential credential = data.get(row);
            // Type column is not editable
            if (column == COL_TYPE) {
                return false;
            }
            // Username and Password only editable for basic auth
            if ((column == COL_USERNAME || column == COL_PASSWORD) &&
                credential.getAuthType() != PartnerInboundAuthCredential.AUTH_TYPE_BASIC) {
                return false;
            }
            // Certificate only editable for cert auth
            if (column == COL_CERT_FINGERPRINT &&
                credential.getAuthType() != PartnerInboundAuthCredential.AUTH_TYPE_CERTIFICATE) {
                return false;
            }
            return true;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        synchronized (data) {
            PartnerInboundAuthCredential credential = data.get(row);
            switch (column) {
                case COL_TYPE:
                    if (credential.getAuthType() == PartnerInboundAuthCredential.AUTH_TYPE_BASIC) {
                        return this.rb.getResourceString("inboundauth.type.basic");
                    } else {
                        return this.rb.getResourceString("inboundauth.type.certificate");
                    }
                case COL_USERNAME:
                    return credential.getUsername() != null ? credential.getUsername() : "";
                case COL_PASSWORD:
                    // Show masked password
                    String password = credential.getPassword();
                    if (password != null && !password.isEmpty()) {
                        return "•".repeat(password.length());
                    }
                    return "";
                case COL_CERT_FINGERPRINT:
                    return credential.getCertFingerprint() != null ? credential.getCertFingerprint() : "";
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
                case COL_CERT_FINGERPRINT:
                    credential.setCertFingerprint((String) value);
                    break;
                case COL_ENABLED:
                    credential.setEnabled((Boolean) value);
                    break;
            }
            if (partner != null) {
                partner.setInboundAuthCredentialsList(data);
            }
        }
        fireTableCellUpdated(row, column);
    }

    public void passNewData(Partner partner) {
        this.partner = partner;
        synchronized (data) {
            data.clear();
            if (partner != null) {
                data.addAll(partner.getInboundAuthCredentialsList());
            }
        }
        fireTableDataChanged();
    }

    public void addRow(PartnerInboundAuthCredential credential) {
        synchronized (data) {
            data.add(credential);
            if (partner != null) {
                partner.setInboundAuthCredentialsList(data);
            }
        }
        fireTableDataChanged();
    }

    public void deleteRow(int row) {
        synchronized (data) {
            if (row >= 0 && row < data.size()) {
                data.remove(row);
                if (partner != null) {
                    partner.setInboundAuthCredentialsList(data);
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
}
