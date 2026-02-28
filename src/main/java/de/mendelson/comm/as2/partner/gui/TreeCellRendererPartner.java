//$Header: /as2/de/mendelson/comm/as2/partner/gui/TreeCellRendererPartner.java 15    3/07/24 9:54 Heller $
package de.mendelson.comm.as2.partner.gui;

import de.mendelson.comm.as2.client.AS2Gui;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.util.ColorUtil;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
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
 * TreeCellRenderer that will display the icons of the partner tree
 *
 * @author S.Heller
 * @version $Revision: 15 $
 */
public class TreeCellRendererPartner extends DefaultTreeCellRenderer {

    public final static int ICON_HEIGHT = AS2Gui.IMAGE_SIZE_TREENODE;

    private final static ImageIcon ICON_REMOTE
            = new ImageIcon(ListCellRendererPartner.IMAGE_REMOTESTATION.toMinResolution(ICON_HEIGHT));
    private final static ImageIcon ICON_LOCAL
            = new ImageIcon(ListCellRendererPartner.IMAGE_LOCALSTATION.toMinResolution(ICON_HEIGHT));
    private final static ImageIcon ICON_LOCAL_ERROR
            = new ImageIcon(
                    ListCellRendererPartner.IMAGE_LOCALSTATION_CONFIGERROR.toMinResolution(ICON_HEIGHT));
    private final static ImageIcon ICON_REMOTE_ERROR
            = new ImageIcon(
                    ListCellRendererPartner.IMAGE_REMOTESTATION_CONFIGERROR.toMinResolution(ICON_HEIGHT));

    private Color colorForegroundError = Color.RED.darker();
    private Color colorForegroundUnselectedNoError = Color.BLACK;
    private Color colorForegroundSelectedNoError = Color.BLACK;

    /**
     * Stores the selected node
     */
    private DefaultMutableTreeNode selectedNode = null;

    /**
     * Constructor to create Renderer for console tree
     */
    public TreeCellRendererPartner() {
        super();
        if (UIManager.getColor("Objects.RedStatus") != null) {
            this.colorForegroundError = UIManager.getColor("Objects.RedStatus");
        }
        if (UIManager.getColor("Tree.selectionForeground") != null) {
            this.colorForegroundSelectedNoError = UIManager.getColor("Tree.selectionForeground");
        }
        if (UIManager.getColor("Tree.foreground") != null) {
            this.colorForegroundUnselectedNoError = UIManager.getColor("Tree.foreground");
        }        
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree,
            Object selectedObject, boolean isSelected,
            boolean expanded,
            boolean leaf,
            int row, boolean hasFocus) {
        this.selectedNode = (DefaultMutableTreeNode) selectedObject;
        Component component = super.getTreeCellRendererComponent(tree, selectedObject, isSelected, expanded,
                leaf, row, hasFocus);
        Object object = null;
        if (this.selectedNode != null) {
            object = this.selectedNode.getUserObject();
        }
        if (object != null) {
            if (object instanceof Partner) {
                Partner partner = (Partner) object;
                super.setText(partner.toString());
                if (partner.hasConfigError()) {
                    if (isSelected) {
                        Color selectionBackground = super.getBackgroundSelectionColor();
                        Color xorForeground = ColorUtil.getXORColor(selectionBackground);                        
                        super.setForeground(xorForeground);
                    } else {
                        super.setForeground(this.colorForegroundError);
                    }                    
                } else {
                    if (isSelected) {
                        super.setForeground(this.colorForegroundSelectedNoError);
                    } else {
                        super.setForeground(this.colorForegroundUnselectedNoError);
                    }
                }
            }
        }
        return (component);
    }

    /**
     * Returns the defined Icon of the entry to be rendered
     */
    private Icon getDefinedIcon() {
        ImageIcon icon = null;
        Object object = null;
        if (this.selectedNode != null) {
            object = this.selectedNode.getUserObject();
        }
        //is this root node?
        if (object == null || !(object instanceof Partner)) {
            return (super.getOpenIcon());
        }
        Partner partner = (Partner) object;
        if (partner.isLocalStation()) {
            if (partner.hasConfigError()) {
                icon = ICON_LOCAL_ERROR;
            } else {
                icon = ICON_LOCAL;
            }
        } else {
            if (partner.hasConfigError()) {
                icon = ICON_REMOTE_ERROR;
            } else {
                icon = ICON_REMOTE;
            }
        }
        return (icon);
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
