package de.mendelson.util.security.cert.gui;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.security.cert.CertificateInUseInfo;
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
 * Model to display all files that are open and save/close them
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class TableModelCertificateReference extends AbstractTableModel {

    protected final static int ROW_HEIGHT = JDialogCertificates.IMAGE_SIZE_TABLE + 3;
    protected final static int IMAGE_HEIGHT = JDialogCertificates.IMAGE_SIZE_TABLE;

    public final static MendelsonMultiResolutionImage IMAGE_PARTNER
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/singlepartner.svg", 
                    IMAGE_HEIGHT);
    public final static MendelsonMultiResolutionImage IMAGE_PARTNER_GATEWAY
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/singlepartner_gateway.svg", 
                    IMAGE_HEIGHT);
    public final static MendelsonMultiResolutionImage IMAGE_PARTNER_ROUTED
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/singlepartner_routed.svg", 
                    IMAGE_HEIGHT);
    public final static MendelsonMultiResolutionImage IMAGE_PARTNER_LOCALSTATION
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/localstation.svg", 
                    IMAGE_HEIGHT);
    public final static MendelsonMultiResolutionImage IMAGE_PARTNER_LOCALSTATION_VIRTUAL
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/localidentity.svg", 
                    IMAGE_HEIGHT);

    private final List<CertificateInUseInfo.SingleCertificateInUseInfo> useList = Collections.synchronizedList(new ArrayList<CertificateInUseInfo.SingleCertificateInUseInfo>());
    
    public TableModelCertificateReference() {
    }

    public void passNewData(List<CertificateInUseInfo.SingleCertificateInUseInfo> useInfoList) {
        synchronized (this.useList) {
            this.useList.clear();
            this.useList.addAll(useInfoList);
        }
        ((AbstractTableModel) this).fireTableDataChanged();
    }

    /**
     * Number of rows to display
     */
    @Override
    public int getRowCount() {
        synchronized (this.useList) {
            return (this.useList.size());
        }
    }

    /**
     * Number of cols to display
     */
    @Override
    public int getColumnCount() {
        return (3);
    }

    /**
     * Returns a value at a specific position in the grid
     */
    @Override
    public Object getValueAt(int row, int col) {
        CertificateInUseInfo.SingleCertificateInUseInfo info = null;
        synchronized (this.useList) {
            info = this.useList.get(row);
        }
        if (col == 0) {
            int type = info.getType();
            if (type == CertificateInUseInfo.PARTNER_REMOTE) {
                return (new ImageIcon(IMAGE_PARTNER.toMinResolution(IMAGE_HEIGHT)));
            } else if (type == CertificateInUseInfo.PARTNER_GATEWAY) {
                return (new ImageIcon(IMAGE_PARTNER_GATEWAY.toMinResolution(IMAGE_HEIGHT)));
            } else if (type == CertificateInUseInfo.PARTNER_ROUTED) {
                return (new ImageIcon(IMAGE_PARTNER_ROUTED.toMinResolution(IMAGE_HEIGHT)));
            } else if (type == CertificateInUseInfo.PARTNER_LOCALSTATION) {
                return (new ImageIcon(IMAGE_PARTNER_LOCALSTATION.toMinResolution(IMAGE_HEIGHT)));
            } else if (type == CertificateInUseInfo.PARTNER_LOCALSTATION_VIRTUAL) {
                return (new ImageIcon(IMAGE_PARTNER_LOCALSTATION_VIRTUAL.toMinResolution(IMAGE_HEIGHT)));
            }
            return (null);
        }
        if (col == 1) {
            return (info.getPartnerName());
        }
        return (info.getDetails());
    }

    /**
     * Returns the name of every column
     *
     * @param col Column to get the header name of
     */
    @Override
    public String getColumnName(int col) {
        return (new String[]{
            "", "", ""
        }[col]);
    }

    /**
     * Set how to display the grid elements
     *
     * @param col requested column
     */
    @Override
    public Class getColumnClass(int col) {
        return (new Class[]{
            ImageIcon.class,
            String.class,
            String.class,}[col]);
    }

    /**
     * Swing GUI checks which cols are editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        return (false);
    }

}
