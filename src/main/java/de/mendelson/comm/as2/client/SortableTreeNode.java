package de.mendelson.comm.as2.client;

import javax.swing.tree.*;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * TreeNode which subnodes are automatically sorted while added, the attached user objects
 * have to implement the comparable interface
 * @author  S.Heller
 * @version $Revision: 1 $
 */
public class SortableTreeNode extends DefaultMutableTreeNode {

    public SortableTreeNode() {
        this(null);
    }

    /**Create a new sortable tree node, the user object must implement
     *the comparable interface!
     */
    public SortableTreeNode(Object userObject) {
        super(userObject);
        if (userObject != null && !(userObject instanceof Comparable)) {
            throw new IllegalArgumentException("SortableTreeNode: Passed user object has to impl the Comparable interface.");
        }
    }
    
    /**Add a new child to the node
     *@param newChild Child to add to this node
     */
    public void add(SortableTreeNode newChild) {
        int count = this.getChildCount();
        Object newObjectRaw = newChild.getUserObject();
        for (int i = 0; i < count; i++) {
            SortableTreeNode child = (SortableTreeNode) getChildAt(i);
            Object childObjectRaw = child.getUserObject();
            if (compareObjects(newObjectRaw, childObjectRaw) < 0) {
                super.insert(newChild, i);
                return;
            }
        }
        super.add(newChild);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private int compareObjects(Object o1, Object o2) {
        return ((Comparable) o1).compareTo(o2);
    }

    /**Insert a node below this one, the index is ignored because this is
     *automatically sorted!
     *@param newChild child to add
     *@param childIndex this is IGNORED!
     */
    public void insert(SortableTreeNode newChild, int childIndex) {
        this.add(newChild);
    }
}