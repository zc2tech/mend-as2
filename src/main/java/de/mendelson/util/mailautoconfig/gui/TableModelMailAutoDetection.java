package de.mendelson.util.mailautoconfig.gui;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.mailautoconfig.MailServiceConfiguration;
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
/**
 * Model to display all services that are available for a given mail address
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class TableModelMailAutoDetection extends AbstractTableModel {

    protected final static int ROW_HEIGHT = 24;
    protected final static int IMAGE_HEIGHT = ROW_HEIGHT - 3;

    private final List<MailServiceConfiguration> configurationList = Collections.synchronizedList(new ArrayList<MailServiceConfiguration>());

    private final MecResourceBundle rb;

    /**
     * Load resources
     */
    public TableModelMailAutoDetection() {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMailAutoConfigurationDetection.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    public void passNewData(List<MailServiceConfiguration> configurationList) {
        synchronized (this.configurationList) {
            this.configurationList.clear();
            this.configurationList.addAll(configurationList);
        }
        ((AbstractTableModel) this).fireTableDataChanged();
    }

    /**
     * Number of rows to display
     */
    @Override
    public int getRowCount() {
        synchronized (this.configurationList) {
            return (this.configurationList.size());
        }
    }

    /**
     * Number of cols to display
     */
    @Override
    public int getColumnCount() {
        return (4);
    }

    /**
     * Returns a value at a specific position in the grid
     */
    @Override
    public Object getValueAt(int row, int col) {
        MailServiceConfiguration configuration = null;
        synchronized (this.configurationList) {
            configuration = this.configurationList.get(row);
        }
        if (col == 0) {
            return (configuration.getService().toUpperCase());
        }
        if (col == 1) {
            return (configuration.getServerHost());            
        }
        if (col == 2) {
            return (configuration.getPort());            
        }
        return (configuration.getSecurityAsString());            
    }

    public MailServiceConfiguration getConfigurationAt( int row){
        synchronized( this.configurationList ){
            return( this.configurationList.get(row));
        }
    }
    
    
    /**
     * Returns the name of every column
     *
     * @param col Column to get the header name of
     */
    @Override
    public String getColumnName(int col) {
        return (new String[]{
            this.rb.getResourceString("header.service"),
            this.rb.getResourceString("header.host"),
            this.rb.getResourceString("header.port"),
            this.rb.getResourceString("header.security"),}[col]);
    }

    /**
     * Set how to display the grid elements
     *
     * @param col requested column
     */
    @Override
    public Class getColumnClass(int col) {
        return (new Class[]{
            String.class,
            String.class,
            String.class,
            String.class
        }[col]);
    }

    /**
     * Swing GUI checks which cols are editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        return (false);
    }

}
