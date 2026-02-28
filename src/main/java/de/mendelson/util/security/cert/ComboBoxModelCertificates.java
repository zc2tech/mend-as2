//$Header: /mec_oftp2/de/mendelson/util/security/cert/ComboBoxModelCertificates.java 1     9.01.15 10:35 Heller $
package de.mendelson.util.security.cert;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Renderer to render the workflows that could be selected
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class ComboBoxModelCertificates extends DefaultComboBoxModel{

    public static final int TYPE_CERTIFICATES = 1;
    public static final int TYPE_KEYS = 2;
    public static final int TYPE_KEYS_AND_CERTIFICATES = 3;

    private int type = TYPE_KEYS_AND_CERTIFICATES;

    public void setType(final int TYPE) {
        if (TYPE != TYPE_CERTIFICATES && TYPE != TYPE_KEYS && TYPE != TYPE_KEYS_AND_CERTIFICATES) {
            throw new IllegalArgumentException("ListModelCertificates.setType(): Unknown type " + TYPE);
        }
        this.type = TYPE;
        super.fireContentsChanged(this, 0, super.getSize());
    }

    private List<Object> filterContents() {
        List<Object> objects = new ArrayList<Object>();
        for (int i = 0; i < super.getSize(); i++) {
            Object element = super.getElementAt(i);
            if (element != null) {
                if (element instanceof KeystoreCertificate) {
                    KeystoreCertificate foundElement = (KeystoreCertificate) element;
                    //display all values
                    if (this.type == TYPE_KEYS_AND_CERTIFICATES) {
                        objects.add(element);
                    } else {
                        int foundType;
                        if (foundElement.getIsKeyPair()) {
                            foundType = TYPE_KEYS;
                        } else {
                            foundType = TYPE_CERTIFICATES;
                        }
                        if (foundType == this.type) {
                            objects.add(element);
                        }
                    }
                } else {
                    objects.add(element);
                }
            }
        }
        return (objects);
    }

    @Override
    public int getSize() {
        List<Object> filtered = this.filterContents();
        return (filtered.size());
    }

    @Override
    public Object getElementAt(int index) {
        List<Object> filtered = this.filterContents();
        return (filtered.get(index));
    }

   
}
