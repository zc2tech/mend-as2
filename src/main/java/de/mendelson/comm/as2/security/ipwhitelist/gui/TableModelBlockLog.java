/*
 * Copyright (C) 2026 Julian Xu
 */

package de.mendelson.comm.as2.security.ipwhitelist.gui;

import de.mendelson.comm.as2.security.ipwhitelist.IPWhitelistBlockLog;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for IP whitelist block log
 *
 * COLUMNS:
 * 0: Block Time
 * 1: Blocked IP
 * 2: Target Type
 * 3: Attempted User
 * 4: Attempted Partner
 * 5: User Agent
 * 6: Request Path
 *
 * Read-only table (no editing)
 */
public class TableModelBlockLog extends AbstractTableModel {

    private List<IPWhitelistBlockLog> logEntries = new ArrayList<>();
    private final String[] columnNames = {
        "Time",
        "Blocked IP",
        "Target",
        "User",
        "Partner",
        "User Agent",
        "Path"
    };

    // TODO: Implement similar to other table models
    @Override
    public int getRowCount() {
        return logEntries.size();
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
    public Object getValueAt(int row, int column) {
        if (row < 0 || row >= logEntries.size()) {
            return null;
        }

        IPWhitelistBlockLog log = logEntries.get(row);
        switch(column) {
            case 0: return formatTimestamp(log.getBlockTime());
            case 1: return log.getBlockedIp();
            case 2: return log.getTargetType();
            case 3: return log.getAttemptedUser() != null ? log.getAttemptedUser() : "";
            case 4: return log.getAttemptedPartner() != null ? log.getAttemptedPartner() : "";
            case 5: return log.getUserAgent() != null ? log.getUserAgent() : "";
            case 6: return log.getRequestPath() != null ? log.getRequestPath() : "";
            default: return null;
        }
    }

    public void setData(List<IPWhitelistBlockLog> logEntries) {
        this.logEntries = logEntries != null ? logEntries : new ArrayList<>();
        fireTableDataChanged();
    }

    public IPWhitelistBlockLog getLogEntryAt(int row) {
        if (row >= 0 && row < logEntries.size()) {
            return logEntries.get(row);
        }
        return null;
    }

    private String formatTimestamp(java.util.Date timestamp) {
        if (timestamp == null) {
            return "";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }
}
