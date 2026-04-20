/*
 * Copyright (C) 2026 Julian Xu
 */

package de.mendelson.comm.as2.security.ipwhitelist.gui;

import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistEntry;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for partner-specific IP whitelist entries
 *
 * COLUMNS:
 * 0: IP Pattern
 * 1: Description
 * 2: Enabled (Boolean)
 * 3: Created At
 *
 * Simpler than global - no Target Type or Created By columns
 */
public class TableModelPartnerWhitelist extends AbstractTableModel {

    private List<IPWhitelistEntry> entries = new ArrayList<>();
    private final String[] columnNames = {"IP Pattern", "Description", "Enabled", "Created"};

    // TODO: Implement similar to TableModelGlobalWhitelist but with fewer columns
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
        if (column == 2) return Boolean.class;
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
            case 1: return entry.getDescription() != null ? entry.getDescription() : "";
            case 2: return entry.isEnabled();
            case 3: return formatDate(entry.getCreatedAt());
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
