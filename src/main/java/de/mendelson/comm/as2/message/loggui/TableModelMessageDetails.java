package de.mendelson.comm.as2.message.loggui;

import de.mendelson.comm.as2.client.AS2Gui;
import de.mendelson.comm.as2.message.AS2Info;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.ResourceBundleAS2Message;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Model to display the message overview
 *
 * @author S.Heller
 * @version $Revision: 20 $
 */
public class TableModelMessageDetails extends AbstractTableModel {

    protected static final int IMAGE_HEIGHT = AS2Gui.IMAGE_SIZE_TABLE;
    public static final int ROW_HEIGHT = IMAGE_HEIGHT + 2;

    public static final ImageIcon ICON_IN
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/in.svg", IMAGE_HEIGHT));
    public static final ImageIcon ICON_OUT
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/out.svg", IMAGE_HEIGHT));
    public static final ImageIcon ICON_MESSAGE
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/message.svg", IMAGE_HEIGHT));
    public static final ImageIcon ICON_SIGNAL_OK
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/signal_ok.svg", IMAGE_HEIGHT));
    public static final ImageIcon ICON_SIGNAL_FAILURE
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/signal_failure.svg", IMAGE_HEIGHT));
    /**
     * ResourceBundle to localize the headers
     */
    private final static MecResourceBundle rb;
    /**
     * ResourceBundle to localize the headers
     */
    private final static MecResourceBundle rbMessage;

    static {
        //load resource bundle
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMessageDetails.class.getName());
            rbMessage = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Message.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }
    /**
     * Format the date output
     */
    private final DateFormat FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    private final List<AS2Info> DATA = Collections.synchronizedList(new ArrayList<AS2Info>());

    /**
     * Creates new LogTableModel
     */
    public TableModelMessageDetails() {
        super();
    }

    /**
     * Passes data to the model and fires a table data update
     */
    public void passNewData(List<AS2Info> newData) {
        if (newData != null) {
            synchronized (this.DATA) {
                this.DATA.clear();
                this.DATA.addAll(newData);
            }
            this.fireTableDataChanged();
        }

    }

    /**
     * Returns the data stored in the specific row
     *
     * @param row Row to look into
     */
    public AS2Info getRow(int row) {
        synchronized (this.DATA) {
            if (row > this.DATA.size() - 1) {
                return (null);
            }
            return (this.DATA.get(row));
        }
    }

    /**
     * Number of rows to display
     */
    @Override
    public int getRowCount() {
        synchronized (this.DATA) {
            return (this.DATA.size());
        }
    }

    /**
     * Number of cols to display
     */
    @Override
    public int getColumnCount() {
        return (8);
    }

    /**
     * Returns a value at a specific position in the grid
     */
    @Override
    public Object getValueAt(int row, int col) {
        AS2Info detailRow = null;
        synchronized (this.DATA) {
            detailRow = this.DATA.get(row);
        }
        switch (col) {
            case 0:
                if (detailRow.getDirection() == AS2MessageInfo.DIRECTION_IN) {
                    return (ICON_IN);
                } else {
                    return (ICON_OUT);
                }
            case 1:
                return (this.FORMAT.format(detailRow.getInitDate()));
            case 2:
                if (detailRow.isMDN()) {
                    if (detailRow.getState() == AS2Message.STATE_FINISHED) {
                        return (ICON_SIGNAL_OK);
                    } else {
                        return (ICON_SIGNAL_FAILURE);
                    }
                } else {
                    return (ICON_MESSAGE);
                }
            case 3:
                return (detailRow.getMessageId());
            case 4:
                return (TableModelMessageDetails.rbMessage.getResourceString("signature." + detailRow.getSignType()));
            case 5:
                if (detailRow.isMDN()) {
                    return ("--");
                } else {
                    AS2MessageInfo messageInfo = (AS2MessageInfo) detailRow;
                    return (TableModelMessageDetails.rbMessage.getResourceString("encryption." + messageInfo.getEncryptionType()));
                }
            case 6:
                if (detailRow.getSenderHost() != null) {
                    return (detailRow.getSenderHost());
                } else {
                    return ("");
                }
            case 7:
                if (detailRow.getUserAgent() != null) {
                    return (detailRow.getUserAgent());
                } else {
                    return ("");
                }
        }
        return (null);
    }

    /**
     * Returns the name of every column
     *
     * @param col Column to get the header name of
     */
    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return (" ");
            case 1:
                return (rb.getResourceString("header.timestamp"));
            case 2:
                return ("  ");
            case 3:
                return (rb.getResourceString("header.messageid"));
            case 4:
                return (rb.getResourceString("header.signature"));
            case 5:
                return (rb.getResourceString("header.encryption"));
            case 6:
                return (rb.getResourceString("header.senderhost"));
            case 7:
                return (rb.getResourceString("header.useragent"));
        }
        return (null);
    }

    /**
     * Set how to display the grid elements
     *
     * @param col requested column
     */
    @Override
    public Class<?> getColumnClass(int col) {
        return (new Class<?>[]{
            ImageIcon.class,
            String.class,
            ImageIcon.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,}[col]);
    }
}
