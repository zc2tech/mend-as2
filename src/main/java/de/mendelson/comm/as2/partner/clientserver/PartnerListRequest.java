package de.mendelson.comm.as2.partner.clientserver;

import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Msg for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class PartnerListRequest extends ClientServerMessage{

    private static final long serialVersionUID = 1L;
    public static final int LIST_ALL = 1;
    public static final int LIST_LOCALSTATION = 2;
    public static final int LIST_NON_LOCALSTATIONS = 3;
    public static final int LIST_BY_AS2_ID = 4;
    public static final int LIST_BY_DB_ID = 5;
    public static final int LIST_NON_LOCALSTATIONS_SUPPORTING_CEM = 6;
    public static final int LIST_BY_NAME = 7;
    private int listOption = LIST_ALL;

    private String additionalListOptionStr = null;
    private int additionalListOptionInt = -1;

    /**Default: Request full partner data*/
    public static final int DATA_COMPLETENESS_FULL = PartnerAccessDB.DATA_COMPLETENESS_FULL;
    /**Just return the partner names and the AS2 id and the type - for fast UI requests*/
    public static final int DATA_COMPLETENESS_NAME_AS2ID_TYPE = PartnerAccessDB.DATA_COMPLETENESS_NAMES_AS2ID_TYPE;
    private int requestedDataCompleteness = DATA_COMPLETENESS_FULL;

    /**User ID to filter partners (0 = admin sees all, >0 = specific user's partners)*/
    private int userId = 0;


    public PartnerListRequest() {
    }

    public PartnerListRequest(int listOption) {
        this.listOption = listOption;
        this.requestedDataCompleteness = DATA_COMPLETENESS_FULL;
    }

    public PartnerListRequest(int listOption, int dataCompleteness) {
        this.listOption = listOption;
        this.requestedDataCompleteness = dataCompleteness;
    }

    @Override
    public String toString() {
        return ("List partner");
    }

    /**
     * @return the listOption
     */
    public int getListOption() {
        return listOption;
    }

    /**
     * @param listOption the listOption to set
     */
    public void setListOption(int listOption) {
        this.listOption = listOption;
    }

    /**
     * @return the additionalListOption
     */
    public String getAdditionalListOptionStr() {
        return additionalListOptionStr;
    }

    /**
     */
    public void setAdditionalListOptionStr(String additionalListOptionStr) {
        this.additionalListOptionStr = additionalListOptionStr;
    }

    /**
     * @return the additionalListOptionInt
     */
    public int getAdditionalListOptionInt() {
        return additionalListOptionInt;
    }

    /**
     * @param additionalListOptionInt the additionalListOptionInt to set
     */
    public void setAdditionalListOptionInt(int additionalListOptionInt) {
        this.additionalListOptionInt = additionalListOptionInt;
    }

    public int getRequestedDataCompleteness(){
        return( this.requestedDataCompleteness );
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set (0 = admin, >0 = specific user)
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**Prevent an overwrite of the readObject method for de-serialization*/
    private void readObject(ObjectInputStream inStream) throws ClassNotFoundException, IOException{
        inStream.defaultReadObject();
    }
}
