/*
 * Copyright (C) 2026 Julian Xu
 */

package de.mendelson.comm.as2.security.ipwhitelist.gui;

import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistEntry;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for global IP whitelist entries
 *
 * COLUMNS:
 * 0: IP Pattern
 * 1: Target Type (AS2, TRACKER, WEBUI, API, ALL)
 * 2: Description
 * 3: Enabled (Boolean)
 * 4: Created At
 * 5: Created By
 *
 * TODO: Complete implementation with:
 * - Column names array
 * - getRowCount(), getColumnCount(), getValueAt()
 * - getColumnClass() for Boolean column
 * - getColumnName()
 * - setData(List<IPWhitelistEntry>)
 * - getEntryAt(int row)
 */
public class TableModelGlobalWhitelist extends AbstractTableModel {

    private List<IPWhitelistEntry> entries = new ArrayList<>();
    private final String[] columnNames = {
        "IP Pattern",
        "Target Type",
        "Description",
        "Enabled",
        "Created",
        "Created By"
    };

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if (column == 3) return Boolean.class; // Enabled column
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (row < 0 || row >= entries.size()) {
            return null;
        }

        IPWhitelistEntry entry = entries.get(row);
        switch(column) {
            case 0: return entry.getIpPattern();
            case 1: return entry.getTargetType();
            case 2: return entry.getDescription() != null ? entry.getDescription() : "";
            case 3: return entry.isEnabled();
            case 4: return formatDate(entry.getCreatedAt());
            case 5: return entry.getCreatedBy() != null ? entry.getCreatedBy() : "";
            default: return null;
        }
    }

    public void setData(List<IPWhitelistEntry> entries) {
        this.entries = entries != null ? entries : new ArrayList<>();
        fireTableDataChanged();
    }

    public IPWhitelistEntry getEntryAt(int row) {
        if (row >= 0 && row < entries.size()) {
            return entries.get(row);
        }
        return null;
    }

    private String formatDate(java.util.Date date) {
        if (date == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }
}
