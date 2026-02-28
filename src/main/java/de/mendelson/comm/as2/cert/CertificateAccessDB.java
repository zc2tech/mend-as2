//$Header: /mec_as2/de/mendelson/comm/as2/cert/CertificateAccessDB.java 35    21/03/25 8:14 Heller $
package de.mendelson.comm.as2.cert;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerCertificateInformation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Access the certificate lists in the database
 *
 * @author S.Heller
 * @version $Revision: 35 $
 */
public class CertificateAccessDB {

    public CertificateAccessDB() {
    }

    /**
     * Returns the list of certificates used by the passed partner READ lock on
     * certificates
     */
    public void loadPartnerCertificateInformation(Partner partner, Connection configConnection) throws Exception {
        try (PreparedStatement statement = configConnection.prepareStatement("SELECT * FROM certificates WHERE partnerid=?")) {
            statement.setInt(1, partner.getDBId());
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    String fingerprint = result.getString("fingerprintsha1");
                    PartnerCertificateInformation information = new PartnerCertificateInformation(
                            fingerprint, result.getInt("category"));
                    partner.setCertificateInformation(information);
                }
            }
        }
    }

    /**
     * Stores the actual partner certificate list of a partner Needs DELETE lock
     * on certificates
     */
    public void storePartnerCertificateInformationList(Partner partner, Connection configConnection) throws Exception {
        this.deletePartnerCertificateInformationList(partner, configConnection);
        Collection<PartnerCertificateInformation> list = partner.getPartnerCertificateInformationList().asList();
        if (!list.isEmpty()) {
            try (PreparedStatement statement = configConnection.prepareStatement(
                    "INSERT INTO certificates(partnerid,fingerprintsha1,category)VALUES(?,?,?)")) {
                for (PartnerCertificateInformation certInfo : list) {
                    statement.setInt(1, partner.getDBId());
                    statement.setString(2, certInfo.getFingerprintSHA1());
                    statement.setInt(3, certInfo.getCategory());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }
    }

    /**
     * Deletes the actual partner certificate list of a partner. A config
     * connection is passed to allow the storing process in a transactional way.
     * DELETE lock on table certificates
     */
    public void deletePartnerCertificateInformationList(Partner partner, Connection configConnection) throws Exception {
        try (PreparedStatement statement = configConnection.prepareStatement(
                "DELETE FROM certificates WHERE partnerid=?")) {
            statement.setInt(1, partner.getDBId());
            statement.executeUpdate();
        }
    }
}
