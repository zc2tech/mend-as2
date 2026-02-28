//$Header: /oftp2/de/mendelson/util/security/cert/gui/TreeCellRendererTrustChain.java 14    20/11/24 13:18 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.TableModelCertificates;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * TreeCellRenderer that will display the icons of the trust chain tree
 *
 * @author S.Heller
 * @version $Revision: 14 $
 */
public class TreeCellRendererTrustChain extends DefaultTreeCellRenderer {

    public static final int ROW_HEIGHT = JDialogCertificates.IMAGE_SIZE_TREENODE + 3 + 2;
    protected static final int IMAGE_HEIGHT = JDialogCertificates.IMAGE_SIZE_TREENODE + 2;

    public static final ImageIcon ICON_ROOT
            = new ImageIcon(TableModelCertificates.IMAGE_ROOT_MULTIRESOLUTION
                    .toMinResolution(IMAGE_HEIGHT));
    public static final ImageIcon ICON_CERT
            = new ImageIcon(TableModelCertificates.IMAGE_CERTIFICATE_MULTIRESOLUTION
                    .toMinResolution(IMAGE_HEIGHT));
    public static final ImageIcon ICON_KEY
            = new ImageIcon(TableModelCertificates.IMAGE_KEY_MULTIRESOLUTION
                    .toMinResolution(IMAGE_HEIGHT));
    public static final ImageIcon ICON_CERTIFICATE_UNTRUSTED
            = new ImageIcon(TableModelCertificates.IMAGE_UNTRUSTED_MULTIRESOLUTION
                    .toMinResolution(IMAGE_HEIGHT));

    /**
     * Stores the selected node
     */
    private DefaultMutableTreeNode selectedNode = null;

    /**
     * Constructor to create Renderer for console tree
     */
    public TreeCellRendererTrustChain() {
        super();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree,
            Object selectedObject, boolean selected,
            boolean expanded,
            boolean leaf,
            int row, boolean hasFocus) {
        this.selectedNode = (DefaultMutableTreeNode) selectedObject;
        Component component = super.getTreeCellRendererComponent(tree, selectedObject, selected, expanded,
                leaf, row, hasFocus);
        Object object = null;
        if (this.selectedNode != null) {
            object = this.selectedNode.getUserObject();
        }
        if (object != null) {
            if (object instanceof KeystoreCertificate) {
                KeystoreCertificate certificate = (KeystoreCertificate) object;
                StringBuilder builder = new StringBuilder();
                builder.append(certificate.getAlias());
                String additionalInfo = certificate.getSubjectCN();
                if (additionalInfo == null) {
                    additionalInfo = certificate.getSubjectOrganization();
                } else if (certificate.getSubjectOrganization() != null) {
                    additionalInfo += ", " + certificate.getSubjectOrganization();
                }
                if (additionalInfo == null) {
                    additionalInfo = certificate.getSubjectOU();
                }
                if (additionalInfo != null) {
                    builder.append(" [")
                            .append(additionalInfo)
                            .append("]");
                }
                super.setText(builder.toString());
            } else if (object instanceof String) {
                //untrusted
                super.setText((String) object);
            }
        }
        return (component);
    }

    /**
     * Returns the defined Icon of the entry
     */
    private Icon getDefinedIcon() {
        Object object = null;
        if (this.selectedNode != null) {
            object = this.selectedNode.getUserObject();
        }
        if (object != null) {
            if (object instanceof KeystoreCertificate) {
                KeystoreCertificate certificate = (KeystoreCertificate) object;
                if (certificate.isRootCertificate()) {
                    return (ICON_ROOT);
                } else if (certificate.getIsKeyPair()) {
                    return (ICON_KEY);
                } else {
                    return (ICON_CERT);
                }
            } else if (object instanceof String) {
                return (ICON_CERTIFICATE_UNTRUSTED);
            }
        }
        //is this root node?
        return (null);
    }

    /**
     * Gets the Icon by the type of the object
     */
    @Override
    public Icon getLeafIcon() {
        Icon icon = this.getDefinedIcon();
        if (icon != null) {
            return (icon);
        }
        //nothing found: get default
        return (super.getLeafIcon());
    }

    @Override
    public Icon getOpenIcon() {
        Icon icon = this.getDefinedIcon();
        if (icon != null) {
            return (icon);
        }
        return (super.getOpenIcon());
    }

    @Override
    public Icon getClosedIcon() {
        Icon icon = this.getDefinedIcon();
        if (icon != null) {
            return (icon);
        }
        return (super.getClosedIcon());
    }

}
