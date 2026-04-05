//$Header: /as2/de/mendelson/comm/as2/partner/HTTPAuthentication.java 9     15/01/25 17:50 Heller $
package de.mendelson.comm.as2.partner;

import java.io.Serializable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Object that stores the information for a HTTP authentication used by a partner
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class HTTPAuthentication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**Auth mode: 0=No Authentication, 1=Basic Auth (credentials in this object), 2=Use User Preference*/
    public static final int AUTH_MODE_NONE = 0;
    public static final int AUTH_MODE_BASIC = 1;
    public static final int AUTH_MODE_USER_PREFERENCE = 2;

    private int authMode = AUTH_MODE_NONE;
    private String user = "";
    private String password = "";
    /**Use it or dont use it? @deprecated Use authMode instead*/
    @Deprecated
    private boolean enabled = false;

    public HTTPAuthentication() {
    }

    /**copy constructor
     *
     * @param authentication
     */
    public HTTPAuthentication( HTTPAuthentication authentication ){
        this.authMode = authentication.authMode;
        this.user = authentication.user;
        this.password = authentication.password;
        this.enabled = authentication.enabled;
    }

    public int getAuthMode() {
        return authMode;
    }

    public void setAuthMode(int authMode) {
        this.authMode = authMode;
        // Keep backward compatibility: update enabled flag
        this.enabled = (authMode == AUTH_MODE_BASIC);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Use it or dont use it?
     * @return the enabled
     * @deprecated Use getAuthMode() instead
     */
    @Deprecated
    public boolean isEnabled() {
        return authMode == AUTH_MODE_BASIC;
    }

    /**
     * Use it or dont use it?
     * @param enabled the enabled to set
     * @deprecated Use setAuthMode() instead
     */
    @Deprecated
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.authMode = enabled ? AUTH_MODE_BASIC : AUTH_MODE_NONE;
    }

    /**Serializes this authentication to XML
     * @param level level in the XML hierarchie for the xml beatifying
     */
    public String toXML(int level, String type) {
        StringBuilder builder = new StringBuilder();
        String offset = "";
        for (int i = 0; i < level; i++) {
            offset += "\t";
        }
        builder.append(offset).append("<httpauthentication type=\"").append(type).append("\">\n");
        builder.append(offset).append("\t<authmode>").append(String.valueOf(this.authMode)).append("</authmode>\n");
        builder.append(offset).append("\t<enabled>").append(String.valueOf(this.isEnabled())).append("</enabled>\n");
        if (this.user != null && !this.user.isEmpty()) {
            builder.append(offset).append("\t<user>").append(this.toCDATA(this.user)).append("</user>\n");
        }
        if (this.password != null && !this.password.isEmpty()) {
            builder.append(offset).append("\t<password>").append(this.toCDATA(this.password)).append("</password>\n");
        }
        builder.append(offset).append("</httpauthentication>\n");
        return (builder.toString());
    }

    /**Adds a cdata indicator to xml data*/
    private String toCDATA(String data) {
        return ("<![CDATA[" + data + "]]>");
    }

    /**Deserializes a httpauthentication from an XML node*/
    public static HTTPAuthentication fromXML(Element element) {
        HTTPAuthentication authentication = new HTTPAuthentication();
        NodeList propertiesNodeList = element.getChildNodes();
        for (int i = 0; i < propertiesNodeList.getLength(); i++) {
            if (propertiesNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element property = (Element) propertiesNodeList.item(i);
                String key = property.getTagName();
                String value = property.getTextContent();
                if (key.equals("user")) {
                    authentication.setUser(value);
                } else if (key.equals("password")) {
                    authentication.setPassword(value);
                } else if (key.equals("authmode")) {
                    try {
                        authentication.setAuthMode(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        authentication.setAuthMode(AUTH_MODE_NONE);
                    }
                } else if (key.equals("enabled")) {
                    // Backward compatibility: if authmode not set, use enabled
                    if (authentication.getAuthMode() == AUTH_MODE_NONE) {
                        authentication.setEnabled(value.equalsIgnoreCase("true"));
                    }
                }
            }
        }
        return (authentication);
    }
}
