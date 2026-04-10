package de.mendelson.comm.as2.server;

import de.mendelson.util.AS2Tools;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Generates a test file that could be sent to a partner for testing purpose
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class TestdataGenerator {

    private TestdataGenerator(){}
    
    public static Path generateTestdata() throws Exception{
        Path testFile = AS2Tools.createTempFile("test-invoice", ".x12");
        String content = 
                "ISA*00*          *00*          *ZZ*SenderID       *ZZ*ReceiverID     *160104*0930*U*00401*000000001*0*T*>~\r\n" + //
                                        "GS*IN*SenderID*ReceiverID*20160104*0930*1*X*004010~\r\n" + //
                                        "ST*810*0001~\r\n" + //
                                        "BIG*20160103*Invoice ID*20151230*PurchaseOrder ID***DI*00~\r\n" + //
                                        "NTE*REG*LegalStatus~\r\n" + //
                                        "NTE*CBH*100000 EUR~\r\n" + //
                                        "CUR*SE*USD*1.10*SE*EUR~\r\n" + //
                                        "REF*AH*Agreement ID*1~\r\n" + //
                                        "REF*IV*Invoice ID~\r\n" + //
                                        "REF*MA*Ship Notice ID~\r\n" + //
                                        "REF*VN*Supplier Order ID~\r\n" + //
                                        "REF*06*System ID~\r\n" + //
                                        "REF*PK*PackList ID~\r\n" + //
                                        "REF*BM*Freight Bill ID~\r\n" + //
                                        "REF*EU*Ultimate Customer ID~\r\n" + //
                                        "REF*KK*Delivery Note ID~\r\n" + //
                                        "REF*4N*Payment Proposal ID~\r\n" + //
                                        "REF*ZZ*MutuallyDefinedIDName*Mutually defined identification~\r\n" + //
                                        "N1*FR*Seller Name*92*Seller ID~\r\n" + //
                                        "REF*14**IBAN ID~\r\n" + //
                                        "REF*02**SWIFT ID~\r\n" + //
                                        "REF*3L**Bank Branch ID~\r\n" + //
                                        "REF*11**Account ID~\r\n" + //
                                        "REF*9X**Account Type~\r\n" + //
                                        "REF*ACT**Account Name~\r\n" + //
                                        "REF*AEC**Government ID ID~\r\n" + //
                                        "REF*VX**VAT ID~\r\n" + //
                                        "REF*GT**GST ID~\r\n" + //
                                        "REF*4O**QST ID~\r\n" + //
                                        "REF*3S**PST ID~\r\n" + //
                                        "REF*ZZ*MutuallyDefinedIDName*Mutually defined identification~\r\n" + //
                                        "PER*CN*Seller Contact Name*EM*E-Mail Addr*TE*Phone ID*UR*URL~\r\n" + //
                                        "PER*RG*Supplier Commercial Credentials~\r\n" + //
                                        "N1*II*IssuerOfInvoice Name*92*IssuerOfInvoice ID~\r\n" + //
                                        "N1*PR*Payer Name*92*Payer ID~\r\n" + //
                                        "N2*Payer Addr Name~\r\n" + //
                                        "N3*Payer Street~\r\n" + //
                                        "N4*Payer City*BE*Payer ZIP*DE~\r\n" + //
                                        "N1*PE*Payee Name*92*Payee ID~\r\n" + //
                                        "N2*Payee Addr Name~\r\n" + //
                                        "N3*Payee Street~\r\n" + //
                                        "N4*Payee City*XX*Payee ZIP*DE*SP*Payee State~\r\n" + //
                                        "N1*RI*Remit-To Name*92*Remit-To ID~\r\n" + //
                                        "N2*Remit-To Addr Name~\r\n" + //
                                        "N3*Remit-To Street~\r\n" + //
                                        "N4*Remit-To City*MH*Remit-To ZIP*IN~\r\n" + //
                                        "N1*SO*Sold-To Name*92*Sold-To ID~\r\n" + //
                                        "N2*Sold-To Addr Name~\r\n" + //
                                        "N3*Sold-To Street~\r\n" + //
                                        "N4*Sold-To City*MH*Sold-To ZIP*IN*SP*XXX~\r\n" + //
                                        "N1*BT*Bill-To Name*92*Bill-To ID~\r\n" + //
                                        "N2*Bill-To Addr Name 1*Bill-To Addr Name 2~\r\n" + //
                                        "N3*Bill-To Street 1*Bill-To Street 2~\r\n" + //
                                        "N4*Bill-To City*CA*Bill-To ZIP*US~\r\n" + //
                                        "REF*VX**VAT ID~\r\n" + //
                                        "N1*BF*Bill-From Name*92*Bill-From ID~\r\n" + //
                                        "N2*Bill-From Addr Name~\r\n" + //
                                        "N3*Bill-From Street~\r\n" + //
                                        "N4*Bill-From City*CA*Bill-From ZIP*US*SP*XXX~\r\n" + //
                                        "REF*TW**Tax Registration Number~\r\n" + //
                                        "N1*ST*Ship-To Name*92*Ship-To ID~\r\n" + //
                                        "N2*Ship-To Addr Name~\r\n" + //
                                        "N3*Ship-To Street~\r\n" + //
                                        "N4*Ship-To City*CA*Ship-To ZIP*US*SP*XXX~\r\n" + //
                                        "N1*SF*Ship-From Name*92*Ship-From ID~\r\n" + //
                                        "N2*Ship-From Addr Name~\r\n" + //
                                        "N3*Ship-From Street~\r\n" + //
                                        "N4*Ship-From City*MH*Ship-From ZIP*IN*SP*XXX~\r\n" + //
                                        "N1*CA*Carrier Name*92*Carrier ID~\r\n" + //
                                        "N2*Carrier Addr Name~\r\n" + //
                                        "N3*Carrier Street~\r\n" + //
                                        "N4*Carrier City*XX*Carrier ZIP*DE*SP*Carrier State~\r\n" + //
                                        "REF*CN*SCAC*Carrier ID~\r\n" + //
                                        "ITD*01*3*2**10**365*100**2000*****40~\r\n" + //
                                        "ITD*05*3*****30***150*****15~\r\n" + //
                                        "ITD*52*3*2**10***100~\r\n" + //
                                        "ITD****20160130*10**365*100~\r\n" + //
                                        "DTM*003*20160103*105920*PD~\r\n" + //
                                        "DTM*004*20151230*105920*PD~\r\n" + //
                                        "DTM*008*20151230*105920*PD~\r\n" + //
                                        "DTM*011*20160102*105920*PD~\r\n" + //
                                        "DTM*111*20160102*105920*PD~\r\n" + //
                                        "DTM*186*20160101*105920*PD~\r\n" + //
                                        "DTM*187*20160103*105920*PD~\r\n" + //
                                        "DTM*LEA*20160103*105920*PD~\r\n" + //
                                        "N9*L1*EN*Comments~\r\n" + //
                                        "MSG*Free-Form-Message-Text~\r\n" + //
                                        "MSG*Free-Form-Message-Text*LC~\r\n" + //
                                        "IT1*10*20*EA*10.00**SN*Serial ID*VP*Supplier Part ID*VS*Supplier Supplemental Part ID*BP*Buyer Part ID*MG*Manufacturer Part ID*MF*Manufacturer Name*CH*Country Code ISO*EN*EAN ID*UP*UPC ID UP*C3*Classification ID~\r\n" + //
                                        "CUR*SE*USD**SE*EUR~\r\n" + //
                                        "CTP*WS***100*EA*CSD*0.7~\r\n" + //
                                        "PAM****1*100.00~\r\n" + //
                                        "PAM****KK*100.00~\r\n" + //
                                        "PAM****N*100.00~\r\n" + //
                                        "PAM****GW*100.00~\r\n" + //
                                        "PAM****EC*100.00~\r\n" + //
                                        "PAM****ZZ*100.00~\r\n" + //
                                        "PID*F****Free-Form-Description-Text****EN~\r\n" + //
                                        "PID*F*GEN***Free-Form-Short-Name****EN~\r\n" + //
                                        "REF*FJ**1~\r\n" + //
                                        "REF*FL*item~\r\n" + //
                                        "REF*KK*Delivery Note ID~\r\n" + //
                                        "REF*MA*Ship Notice ID**LI>1~\r\n" + //
                                        "REF*PK*Packing List ID~\r\n" + //
                                        "REF*ACE*Service Entry Sheet ID**LI>1~\r\n" + //
                                        "REF*8L*HSNSAC*HSNSAC ID~\r\n" + //
                                        "REF*8L*otherDomain*otherDomain ID~\r\n" + //
                                        "REF*L1*en*Item Comments~\r\n" + //
                                        "REF*ZZ*MutuallyDefinedIDName*Mutually defined identification~\r\n" + //
                                        "YNQ*Q3*Y~\r\n" + //
                                        "YNQ**Y********ad-hoc item~\r\n" + //
                                        "DTM*011*20160102*105920*PD~\r\n" + //
                                        "DTM*111*20160102*105920*PD~\r\n" + //
                                        "DTM*192*20160102*105920*PD~\r\n" + //
                                        "DTM*517*20160101*105920*PD~\r\n" + //
                                        "DTM*214*20160101*105920*PD~\r\n" + //
                                        "DTM*472*20160101*105920*PD~\r\n" + //
                                        "SAC*A*H970***1200*3*20.00*****13*10.00*ContractPrice*Free-form description*EN~\r\n" + //
                                        "TXI*TX*0.38********Tax description~\r\n" + //
                                        "TXI*VA*0.38*19.00*VD*Location*0**2.00*Tax Category XY~\r\n" + //
                                        "SAC*C*A520***10000*3*5.00*****02***Free-form description*EN~\r\n" + //
                                        "TXI*TX*19.00********Tax description~\r\n" + //
                                        "TXI*VA*19.00*19.00*VD*Location*0**100.00~\r\n" + //
                                        "SAC*C*C310***1000*3*10.00~\r\n" + //
                                        "SAC*C*G830***1500~\r\n" + //
                                        "TXI*ST*140.00*14.00*VD*Location*0**1000.00**20170106060000ED~\r\n" + //
                                        "TXI*ST*119.70~\r\n" + //
                                        "SAC*C*H090***1200**********Special-Handling description*EN~\r\n" + //
                                        "TXI*ST*140.00*14.00*VD*Location*0**1000.00**20170106060000ED~\r\n" + //
                                        "TXI*ST*119.70~\r\n" + //
                                        "SAC*N*B840*AB*Accounting*10000*Z*100******Accounting segment ID*Accounting seg name*Accounting segment description*EN~\r\n" + //
                                        "SAC*C*H850***14000********130.69**Tax description*EN~\r\n" + //
                                        "TXI*VA*140.00*14.00*VD*Location*0**1000.00**20170106060000ED~\r\n" + //
                                        "TXI*VA*119.70********123456789.12345~\r\n" + //
                                        "N1*SF*Ship-From Name*92*Ship-From ID~\r\n" + //
                                        "N2*Ship-From Addr Name 1*Ship-From Addr Name 2~\r\n" + //
                                        "N3*Ship-From Street 1*Ship-From Street 2~\r\n" + //
                                        "N4*Ship-From City*MH*Ship-From ZIP*IN*SP*XXX~\r\n" + //
                                        "REF*SI*Tracking ID*20170106060000ED~\r\n" + //
                                        "N1*ST*Ship-To Name*92*Ship-To ID~\r\n" + //
                                        "N2*Ship-To Addr Name 1*Ship-To Addr Name 2~\r\n" + //
                                        "N3*Ship-To Street 1*Ship-To Street 2~\r\n" + //
                                        "N4*Ship-To City*CA*Ship-To ZIP*US*SP*XXX~\r\n" + //
                                        "REF*SI*Tracking ID*20170106060000ED~\r\n" + //
                                        "N1*CA*Carrier Name*92*Carrier ID~\r\n" + //
                                        "N2*Carrier Addr Name~\r\n" + //
                                        "N3*Carrier Street~\r\n" + //
                                        "N4*Carrier City*XX*Carrier ZIP*DE*SP*Carrier State~\r\n" + //
                                        "REF*CN*SCAC*Carrier ID~\r\n" + //
                                        "PER*CN*Carrier Contact Name*EM*E-Mail Addr*TE*Phone ID*UR*URL~\r\n" + //
                                        "TDS*100000*100000*100000*100000~\r\n" + //
                                        "AMT*3*100.00~\r\n" + //
                                        "AMT*GW*100.00~\r\n" + //
                                        "AMT*EC*100.00~\r\n" + //
                                        "AMT*ZZ*100.00~\r\n" + //
                                        "AMT*ZZ*85.50~\r\n" + //
                                        "AMT*1*85.50~\r\n" + //
                                        "AMT*BAP*85.50~\r\n" + //
                                        "SAC*A*H970***1200*3*20.00*****13*10.00*ContractPrice*Free-form description*EN~\r\n" + //
                                        "TXI*TX*0.38********Tax description~\r\n" + //
                                        "TXI*VA*0.38*19.00*VD*Location*0**2.00*Tax Category XY~\r\n" + //
                                        "SAC*C*A520***10000*3*5.00********Free-form description*EN~\r\n" + //
                                        "TXI*TX*19.00********Tax description~\r\n" + //
                                        "TXI*VA*19.00*19.00*VD*Location*0**100.00~\r\n" + //
                                        "SAC*C*C310***1000*3*10.00~\r\n" + //
                                        "SAC*C*G830***1500~\r\n" + //
                                        "TXI*ST*140.00*14.00*VD*Location*0**1000.00**20170106060000ED~\r\n" + //
                                        "TXI*ST*154.00~\r\n" + //
                                        "SAC*C*H090***1200**********Special-Handling description*EN~\r\n" + //
                                        "TXI*ST*140.00*14.00*VD*Location*0**1000.00**20170106060000ED~\r\n" + //
                                        "TXI*ST*154.00~\r\n" + //
                                        "SAC*C*H850***38000********130.69**Tax description*EN~\r\n" + //
                                        "TXI*VA*140.00*14.00*VD*Location*0**1000.00**20170106060000ED~\r\n" + //
                                        "TXI*VA*154.00~\r\n" + //
                                        "TXI*VA*240.00*24.00*VD*Location*0**1000.00**20200106060000ED~\r\n" + //
                                        "TXI*VA*264.00********123456789.12345~\r\n" + //
                                        "CTT*1~\r\n" + //
                                        "SE*176*0001~\r\n" + //
                                        "GE*1*1~\r\n" + //
                                        "IEA*1*000000001~\r\n" + //
                                        "\r\n";
        Files.write(testFile, content.getBytes(StandardCharsets.UTF_8));
        return( testFile );
    }
}
