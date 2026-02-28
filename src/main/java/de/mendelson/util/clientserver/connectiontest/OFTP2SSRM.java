//$Header: /as2/de/mendelson/util/clientserver/connectiontest/OFTP2SSRM.java 2     2/11/23 14:03 Heller $
package de.mendelson.util.clientserver.connectiontest;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Command SSRM
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class OFTP2SSRM extends OFTP2AbstractCommand implements OFTP2Command {

    public static final String SSRMCMD = "SSRMCMD";
    public static final String SSRMMSG = "SSRMMSG";
    public static final String SSRMCR = "SSRMCR";
    
    private final OFTP2Field[] fields = new OFTP2Field[]{
        new OFTP2FieldAN(SSRMCMD, 1, "SSRM Command", this.getIndicator()),
        new OFTP2FieldAN(SSRMMSG, 17, "Ready Message", "ODETTE FTP READY"),
        new OFTP2FieldAN(SSRMCR, 1, "Carriage Return", new byte[]{0x0d}),};

    @Override
    public String getName() {
        return (OFTP2Command.COMMANDNAME_SSRM);
    }

    @Override
    public String getIndicator() {
        return ("I");
    }

    @Override
    public OFTP2Field[] getFields() {
        return (fields);
    }

    @Override
    public String getDescription() {
        return ("Start Session Ready Message");
    }



}
