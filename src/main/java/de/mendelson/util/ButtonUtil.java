package de.mendelson.util;

import javax.swing.JButton;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Class that contains routines for buttons
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ButtonUtil{

    private ButtonUtil(){
    }

    /**
     * Reformats a button text if it is much to long in the translation. Creates a centered
     * text that is split into multiple lines - the button has to be prepared for this in the
     * layout
     */
    public static void reformatButtonText(JButton button) {
        String buttonText = button.getText();
        int maxLength = 25;
        if (buttonText.length() > maxLength) {
            StringBuilder builder = new StringBuilder();
            String[] parts = buttonText.split(" ");
            int lineLength = 0;
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (i == 0) {
                    builder.append(part);
                    lineLength = part.length();
                    continue;
                }
                if ((lineLength + part.length() + 1) < maxLength) {
                    builder.append(" ");
                    builder.append(part);
                    lineLength += 1 + part.length();
                } else {
                    builder.append("<br>");
                    builder.append(part);
                    lineLength = part.length();
                }
            }
            builder.insert(0, "<HTML><p style=\"text-align:center;\">");
            builder.append("</p></HTML>");
            button.setText(builder.toString());
        }
    }
    
}
