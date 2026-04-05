package de.mendelson.util.security.cert.gui;

import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.tree.SortableTreeNode;
import java.util.List;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Tree to display the trust chain of a certificate
 *
 * @author S.Heller
 * @version $Revision: 14 $
 */
public class JTreeTrustChain extends JTree {

    /**
     * This is the root node
     */
    private final SortableTreeNode root;

    /**
     * Tree constructor
     */
    public JTreeTrustChain() {
        super(new SortableTreeNode());
        this.setRootVisible(true);       
        this.root = (SortableTreeNode) this.getModel().getRoot();
        this.setCellRenderer(new TreeCellRendererTrustChain());
        this.setRowHeight(TreeCellRendererTrustChain.ROW_HEIGHT);
        this.setBorder(new EmptyBorder(2, 2, 2, 2));
        //prevent a collapse of this tree
        this.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {

            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                throw new ExpandVetoException(event, "Collapsing trust chain tree is not allowed");
            }
        });
    }

    /**
     * Builds up the tree
     */
    public void buildTree(List<KeystoreCertificate> trustChain) {
        this.root.removeAllChildren();
        ((DefaultTreeModel) this.getModel()).nodeStructureChanged(this.root);
        //check if first cert is untrusted
        SortableTreeNode parent;
        KeystoreCertificate firstCert = trustChain.get(0);
        if (!firstCert.getIssuerX500Principal().equals(firstCert.getSubjectX500Principal())) {
            //there is a missing certificate above - means the first certificate of the chain is not 
            //the root of the trust chain
            StringBuilder text = new StringBuilder();
            if( firstCert.getIssuerCN() != null ){
                text.append( firstCert.getIssuerCN() );
            }
            String organization = firstCert.getIssuerOrganization();
            if( organization != null ){
                text.append(" [")
                    .append(organization)
                    .append("]");
            }
            if( text.length() == 0){
                text.append( "--");
            }
            this.root.setUserObject(text.toString());
            SortableTreeNode child = new SortableTreeNode(firstCert);
            this.root.add(child);
            ((DefaultTreeModel) this.getModel()).nodeStructureChanged(this.root);
            parent = child;
        } else {
            this.root.setUserObject(trustChain.get(0));
            parent = this.root;
        }
        for (int i = 1; i < trustChain.size(); i++) {
            SortableTreeNode child = new SortableTreeNode(trustChain.get(i));
            parent.add(child);
            ((DefaultTreeModel) this.getModel()).nodeStructureChanged(parent);
            parent = child;
        }
        ((DefaultTreeModel) this.getModel()).nodeStructureChanged(parent);
        this.expandPath(new TreePath(parent.getPath()));
        this.setSelectionPath(new TreePath(parent.getPath()));
    }

    /**
     * Returns the selected node of the Tree
     */
    public SortableTreeNode getSelectedNode() {
        synchronized (this.getModel()) {
            TreePath path = this.getSelectionPath();
            if (path != null) {
                return ((SortableTreeNode) path.getLastPathComponent());
            }
            return (null);
        }
    }

    public void partnerChanged(KeystoreCertificate certificate) {
        synchronized (this.getModel()) {
            for (int i = 0; i < this.root.getChildCount(); i++) {
                SortableTreeNode child = (SortableTreeNode) root.getChildAt(i);
                KeystoreCertificate foundCertificate = (KeystoreCertificate) child.getUserObject();
                if (foundCertificate.equals(certificate)) {
                    ((DefaultTreeModel) this.getModel()).nodeChanged(child);
                }
            }
        }
    }

}
