package de.mendelson.util.security.cert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 * Licensed under GPL-2.0. See LICENSE file for details.
 */

/**
 * Extended table model that includes owner information for certificates
 * Used in admin mode to display certificates from all users
 *
 * @author Julian Xu
 */
public class TableModelCertificatesWithOwner extends TableModelCertificates {

    private final List<CertificateWithOwner> certificatesWithOwner =
        Collections.synchronizedList(new ArrayList<CertificateWithOwner>());

    private static final Class<?>[] COLUMN_CLASSES_WITH_OWNER = new Class<?>[]{
        ImageIcon.class,
        ImageIcon.class,
        String.class,
        Date.class,
        String.class,
        String.class,
        String.class,
        String.class,
        String.class  // Owner column
    };

    /**
     * Set data with owner information
     */
    public void setNewDataWithOwner(List<CertificateWithOwner> data) {
        synchronized (this.certificatesWithOwner) {
            this.certificatesWithOwner.clear();
            this.certificatesWithOwner.addAll(data);
        }

        // Extract certificate list for parent class
        List<KeystoreCertificate> certificates = new ArrayList<>();
        for (CertificateWithOwner certWithOwner : data) {
            certificates.add(certWithOwner.getCertificate());
        }
        super.setNewData(certificates);
    }

    /**
     * Returns the number of columns in the table (8 standard + 1 owner)
     */
    @Override
    public int getColumnCount() {
        return 9;
    }

    /**
     * Returns the name of every column
     *
     * @param col Column to get the header name of
     */
    @Override
    public String getColumnName(int col) {
        if (col < 8) {
            return super.getColumnName(col);
        }
        if (col == 8) {
            return "Owner";
        }
        return "";
    }

    /**
     * Returns the grid value
     */
    @Override
    public Object getValueAt(int row, int col) {
        if (col < 8) {
            return super.getValueAt(row, col);
        }

        // Owner column (column 8)
        if (col == 8) {
            synchronized (this.certificatesWithOwner) {
                if (row < this.certificatesWithOwner.size()) {
                    CertificateWithOwner certWithOwner = this.certificatesWithOwner.get(row);
                    return certWithOwner.getUsername();
                }
            }
        }

        return "";
    }

    /**
     * Set how to display the grid elements
     *
     * @param col requested column
     */
    @Override
    public Class<?> getColumnClass(int col) {
        return COLUMN_CLASSES_WITH_OWNER[col];
    }

    /**
     * Get the certificate with owner information at the specified row
     */
    public CertificateWithOwner getCertificateWithOwner(int row) {
        synchronized (this.certificatesWithOwner) {
            if (row >= 0 && row < this.certificatesWithOwner.size()) {
                return this.certificatesWithOwner.get(row);
            }
        }
        return null;
    }
}
