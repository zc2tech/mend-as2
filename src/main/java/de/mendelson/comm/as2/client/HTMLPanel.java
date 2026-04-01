//$Header: /mec_as2/de/mendelson/comm/as2/client/HTMLPanel.java 9     2/28/17 12:29p Heller $
package de.mendelson.comm.as2.client;

import java.awt.Cursor;
import java.awt.Desktop;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.net.URI;
import java.net.URL;
import jakarta.servlet.http.HttpServletResponse;
import javax.swing.JPanel;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;

/**
 * Panel that allows to display simple HTML pages
 *
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class HTMLPanel extends JPanel implements HyperlinkListener, PropertyChangeListener {

    private String fallbackOnErrorURL = null;

    /**
     * Creates new form HTMLPanel
     */
    public HTMLPanel() {
        initComponents();
        if (Desktop.isDesktopSupported()) {
            this.jEditorPane.addHyperlinkListener(this);
        }
        //add property listener that is informed once the passed page is loaded
        this.jEditorPane.addPropertyChangeListener("page", this);
    }

    /**
     * Is informed once the page is loaded - stores the downloaded content
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (this.fallbackOnErrorURL == null) {
            return;
        }
        //now store the new data for next (offline) attempt
        FileWriter downloadedDataWriter = null;
        try {
            downloadedDataWriter = new FileWriter(new URL(this.fallbackOnErrorURL).getFile());
            downloadedDataWriter.write(this.jEditorPane.getText());
        } catch (Exception e) {
        } finally {
            if (downloadedDataWriter != null) {
                try {
                    downloadedDataWriter.flush();
                    downloadedDataWriter.close();
                } catch (Exception ex) {
                    //nop
                }
            }
        }
    }

    /**
     * Sets a new page to the viewer
     */
    public void setPage(String url) {
        try {
            this.jEditorPane.setPage(url);
        } catch (Exception e) {
            //nop
        }
    }

    /**
     * Sets a new page to the viewer and handles the Stack update
     *
     * @param url URL to move to
     */
    public Header[] setURL(String urlStr, String userAgent, String fallbackOnErrorURL) {
        this.fallbackOnErrorURL = fallbackOnErrorURL;
        Header[] header = new Header[0];
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //post for header data
            HttpPost filePost = new HttpPost(new URL(urlStr).toExternalForm());
            filePost.addHeader("User-Agent", userAgent);
            try (CloseableHttpResponse httpResponse = httpClient.execute(filePost)) {
                header = httpResponse.getHeaders();
                int status = httpResponse.getCode();
                if (status != HttpServletResponse.SC_OK) {
                    throw new Exception("HTTP " + status);
                }
            }
            //now get for body data
            this.jEditorPane.setPage(new URL(urlStr));
        } catch (Throwable e) {
            try {
                this.setPage(fallbackOnErrorURL);
            } catch (Exception ex) {
                //nop
            }
        }
        return (header);
    }

    /**
     * Listen to be a HyperlinkListener
     */
    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                URI uri = new URI(e.getURL().toExternalForm());
                Desktop.getDesktop().browse(uri);
            } catch (Exception ex) {
                //nop
            }
        }
        if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
            jEditorPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
            jEditorPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane = new javax.swing.JScrollPane();
        jEditorPane = new javax.swing.JEditorPane();

        setLayout(new java.awt.GridBagLayout());

        jEditorPane.setContentType("text/html"); // NOI18N
        jEditorPane.setEditable(false);
        jScrollPane.setViewportView(jEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane;
    private javax.swing.JScrollPane jScrollPane;
    // End of variables declaration//GEN-END:variables
}
