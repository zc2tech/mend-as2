/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.mendelson.comm.as2.tracker;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analyzes EDI payload content to identify format and document type
 *
 * @author Julian Xu
 */
public class PayloadAnalyzer {

    /**
     * Analysis result containing format and document type information
     */
    public static class PayloadAnalysis implements Serializable {
        public static final long serialVersionUID = 1L;

        private String format; // "cXML", "X12", "EDIFACT", "Unknown"
        private String documentType; // e.g., "Purchase Order", "Invoice", "810", "DESADV"
        private String details; // Additional details

        public PayloadAnalysis(String format, String documentType, String details) {
            this.format = format;
            this.documentType = documentType;
            this.details = details;
        }

        public String getFormat() {
            return format;
        }

        public String getDocumentType() {
            return documentType;
        }

        public String getDetails() {
            return details;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Format: ").append(format);
            if (documentType != null && !documentType.isEmpty()) {
                sb.append(", Document Type: ").append(documentType);
            }
            if (details != null && !details.isEmpty()) {
                sb.append(", ").append(details);
            }
            return sb.toString();
        }
    }

    /**
     * Analyze payload content to determine format and document type
     * Only analyzes the first 4KB for performance
     */
    public static PayloadAnalysis analyze(byte[] content) {
        if (content == null || content.length == 0) {
            return new PayloadAnalysis("Unknown", null, "Empty content");
        }

        try {
            // Only analyze first 4KB for performance
            int bytesToAnalyze = Math.min(content.length, 4096);
            byte[] contentToAnalyze = (bytesToAnalyze == content.length) ? content
                    : java.util.Arrays.copyOf(content, bytesToAnalyze);
            String contentStr = new String(contentToAnalyze, StandardCharsets.UTF_8);

            // Try cXML first (XML-based)
            if (contentStr.trim().startsWith("<?xml") || contentStr.contains("<cXML")) {
                return analyzeCXML(contentStr);
            }

            // Try EDIFACT (starts with UNB segment)
            if (contentStr.startsWith("UNB+") || contentStr.contains("UNB+")) {
                return analyzeEDIFACT(contentStr);
            }

            // Try X12 (starts with ISA segment)
            if (contentStr.startsWith("ISA") && contentStr.length() >= 106) {
                return analyzeX12(contentStr);
            }

            return new PayloadAnalysis("Unknown", null, "Format not recognized");

        } catch (Exception e) {
            return new PayloadAnalysis("Unknown", null, "Analysis failed: " + e.getMessage());
        }
    }

    /**
     * Analyze cXML content
     */
    private static PayloadAnalysis analyzeCXML(String content) {
        String docType = null;
        String details = null;

        // Check for OrderRequest (Purchase Order)
        if (content.contains("<OrderRequest")) {
            docType = "Purchase Order";

            // Extract order ID if available
            Pattern orderIdPattern = Pattern.compile("orderID=\"([^\"]+)\"");
            Matcher matcher = orderIdPattern.matcher(content);
            if (matcher.find()) {
                docType += " " + matcher.group(1);
                details = "Order ID: " + matcher.group(1);
            }
        }
        // PaymentRemittanceRequest
        else if (content.contains("<PaymentRemittanceRequest")) {
            docType = "Payment Remittance";
            // Extract order ID if available
            Pattern pID = Pattern.compile("paymentRemittanceID=\"([^\"]+)\"");
            Matcher matcher = pID.matcher(content);
            if (matcher.find()) {
                docType += " " + matcher.group(1);
                details = "Remittance ID: " + matcher.group(1);
            }
        }
        // TransportRequest
        else if (content.contains("<TransportRequest")) {
            docType = "Transport Request";
            // Extract order ID if available
            Pattern pID = Pattern.compile("requestID=\"([^\"]+)\"");
            Matcher matcher = pID.matcher(content);
            if (matcher.find()) {
                docType += " " + matcher.group(1);
                details = "Remittance ID: " + matcher.group(1);
            }
        }
        // ProductActivityMessage
        else if (content.contains("<ProductActivityMessage")) {
            docType = "ProductActivity";
            Pattern pID = Pattern.compile("messageID=\"([^\"]+)\"");
            // Extract order ID if available
            Pattern pType = Pattern.compile("processType=\"([^\"]+)\"");
            Matcher matcher = pType.matcher(content);
            if (matcher.find()) {
                String sType = matcher.group(1);

                if (sType.equals("Consignment")) {
                    docType = "Consignment";
                } else if (sType.equals("Forecast")) {
                    docType = "Forecast";
                } else if (sType.equals("ManufacturingVisibility")) {
                    docType = "Forecast";
                }

            } else {
                docType = "ProductActivityMessage";
            }
            Matcher mID = pID.matcher(content);
            if (mID.find()) {
                docType += " " + mID.group(1);
                details = "Message ID: " + mID.group(1);
            }

        }
        // ProductReplenishmentMessage
        else if (content.contains("<ProductReplenishmentMessage")) {
            docType = "ProductReplenishment";
            Pattern pID = Pattern.compile("messageID=\"([^\"]+)\"");
            // Extract order ID if available
            Pattern pType = Pattern.compile("processType=\"([^\"]+)\"");
            Matcher matcher = pType.matcher(content);
            if (matcher.find()) {
                String sType = matcher.group(1);
                if (sType.equals("ManufacturingVisibility")) {
                    docType = "ProductReplenishment";
                }

            } else {
                docType = "ProductReplenishmentMessage";
            }
            Matcher mID = pID.matcher(content);
            if (mID.find()) {
                docType += " " + mID.group(1);
                details = "Message ID: " + mID.group(1);
            }

        }
        // ReceiptRequest
        else if (content.contains("<ReceiptRequest")) {
            docType = "Goods Receipt";
            // Extract ID if available
            Pattern pID = Pattern.compile("receiptID=\"([^\"]+)\"");
            Matcher mPID = pID.matcher(content);
            if (mPID.find()) {
                docType += " " + mPID.group(1);
                details = "Receipt ID: " + mPID.group(1);
            }
        }
        // Check for InvoiceDetailRequest (Invoice)
        else if (content.contains("<InvoiceDetailRequest")) {
            docType = "Invoice";

            // Extract invoice number if available
            Pattern invoicePattern = Pattern.compile("invoiceID=\"([^\"]+)\"");
            Matcher matcher = invoicePattern.matcher(content);
            if (matcher.find()) {
                docType += " " + matcher.group(1);
                details = "Invoice ID: " + matcher.group(1);
            }
        }
        // Check for ConfirmationRequest (Order Confirmation)
        else if (content.contains("<ConfirmationRequest")) {
            docType = "Order Confirmation";
            Pattern pID = Pattern.compile("confirmID=\"([^\"]+)\"");
            Matcher matcher = pID.matcher(content);
            if (matcher.find()) {
                docType += " " + matcher.group(1);
                details = "confirmID: " + matcher.group(1);
            }
        }
        // Check for ShipNoticeRequest (ASN/Despatch Advice)
        else if (content.contains("<ShipNoticeRequest")) {
            docType = "Ship Notice";
            Pattern pID = Pattern.compile("shipmentID=\"([^\"]+)\"");
            Matcher matcher = pID.matcher(content);
            if (matcher.find()) {
                docType += " " + matcher.group(1);
                details = "Shipment ID: " + matcher.group(1);
            }
        }
        // Check for StatusUpdateRequest
        else if (content.contains("<StatusUpdateRequest")) {
            docType = "StatusUpdateRequest";
            Pattern pID = Pattern.compile("documentID *= *\"([^\"]+)\"");
            Matcher mPID = pID.matcher(content);
            Pattern pID2 = Pattern.compile("invoiceID *= *\"([^\"]+)\"");
            Matcher mPID2 = pID2.matcher(content);
            Pattern pCode = Pattern.compile("code *= *\"(\\d+)\"");
            Matcher mCode = pCode.matcher(content);

            if (mCode.find()) {
                docType += "(" + mCode.group(1) + ")";
            }
            if (mPID.find()) {
                docType += " " + mPID.group(1);
                details = "Document ID: " + mPID.group(1);
            } else if (mPID2.find()) {
                docType += " " + mPID2.group(1);
                details = "Invoice ID: " + mPID2.group(1);
            }

        }
        // Generic cXML
        else {
            docType = "cXML Document (type unspecified)";
        }

        return new PayloadAnalysis("cXML", docType, details);
    }

    /**
     * Analyze X12 content
     */
    private static PayloadAnalysis analyzeX12(String content) {
        String docType = null;
        String details = null;

        // X12 uses segment terminators (usually ~ or newline)
        // Find the element separator (character at position 3)
        char elementSep = content.length() > 3 ? content.charAt(3) : '*';

        // Find ST segment (Transaction Set Header)
        Pattern stPattern = Pattern.compile("ST" + Pattern.quote(String.valueOf(elementSep)) + "(\\d{3})");
        Matcher stMatcher = stPattern.matcher(content);

        if (stMatcher.find()) {
            String transactionCode = stMatcher.group(1);
            docType = getX12TransactionType(transactionCode, content);
            details = "Transaction Set: " + transactionCode;

            // Try to extract control number
            Pattern controlPattern = Pattern.compile("ST" + Pattern.quote(String.valueOf(elementSep)) +
                    "\\d{3}" + Pattern.quote(String.valueOf(elementSep)) + "([^" +
                    Pattern.quote(String.valueOf(elementSep)) + "~\r\n]+)");
            Matcher controlMatcher = controlPattern.matcher(content);
            if (controlMatcher.find()) {
                details += ", Control#: " + controlMatcher.group(1);
            }
        } else {
            docType = "X12 Document (ST segment not found)";
        }

        return new PayloadAnalysis("X12", docType, details);
    }

    /**
     * Return Regular Expression for extracting ID , assuming it's in position 2 or
     * 3
     * 
     * @param segHeader
     * @param elementSep
     * @return
     */
    private static Pattern getPattern_X12(String segHeader, char elementSep, int iPos) {
        String sSep = String.valueOf((elementSep));
        if (iPos == 1) {
            return Pattern.compile(segHeader + Pattern.quote(sSep) + "([^" +
                    Pattern.quote(String.valueOf(elementSep)) + "~\r\n]+)");
        }

        else if (iPos == 2) {
            return Pattern.compile(segHeader + Pattern.quote(sSep) +
                    "\\d{1,8}" + Pattern.quote(sSep) + "([^" +
                    Pattern.quote(String.valueOf(elementSep)) + "~\r\n]+)");
        } else if (iPos == 3) {
            return Pattern.compile(segHeader + Pattern.quote(sSep) +
                    "\\d{1,8}" + Pattern.quote(sSep) + "\\w{1,8}" + Pattern.quote(sSep) + "([^" +
                    Pattern.quote(sSep) + "~\r\n]+)");
        } else {
            return null;
        }

    }

    /**
     * Return Regular Expression for extracting ID , assuming it's in position 2 or
     * 3
     * 
     * @param segHeader
     * @param elementSep
     * @return
     */
    private static Pattern getPattern_EDIFACT(String segHeader, int iPos) {
        String sSep = "+";
        if (iPos == 1) {
            return Pattern.compile(segHeader + Pattern.quote(sSep) + "([^" +
                    Pattern.quote(sSep) + "'\r\n]+)");
        }

        else if (iPos == 2) {
            return Pattern.compile(segHeader + Pattern.quote(sSep) +
                    "[^\\+]*" + Pattern.quote(sSep) + "([^" +
                    Pattern.quote(sSep) + "'\r\n]+)");
        } else if (iPos == 3) {
            return Pattern.compile(segHeader + Pattern.quote(sSep) +
                    "[^\\+]+" + Pattern.quote(sSep) + "[^\\+]+" + Pattern.quote(sSep) + "([^" +
                    Pattern.quote(sSep) + "'\r\n]+)");
        } else {
            return null;
        }

    }

    /**
     * Return value of ID, assumming it's in position 2 or 3
     * 
     * @param segHeader
     * @param content
     * @return
     */
    private static String getID_X12(String segHeader, String content, int iPos) {
        char elementSep = content.length() > 3 ? content.charAt(3) : '*';
        Pattern pSegID = getPattern_X12(segHeader, elementSep, iPos);
        if (pSegID == null) {
            return "";
        }
        Matcher mSegID = pSegID.matcher(content);
        if (mSegID.find()) {
            return mSegID.group(1);
        } else {
            return "";
        }

    }

    /**
     * Return value of ID, assumming it's in position 2 or 3
     * 
     * @param segHeader
     * @param content
     * @return
     */
    private static String getID_EDIFACT(String segHeader, String content, int iPos) {
        Pattern pSegID = getPattern_EDIFACT(segHeader, iPos);
        if (pSegID == null) {
            return "";
        }
        Matcher mSegID = pSegID.matcher(content);
        if (mSegID.find()) {
            return mSegID.group(1);
        } else {
            return "";
        }

    }

    /**
     * Return PO ID of 850/860
     * 
     * @param segHeader
     * @param content
     * @return
     */
    private static String getOC_ID_X12(String content) {

        char elementSep = content.length() > 3 ? content.charAt(3) : '*';
        Pattern pSegLine = Pattern.compile("(BAK[^~]+)");
        Matcher mSegLine = pSegLine.matcher(content);
        if (mSegLine.find()) {
            String sLine = mSegLine.group(1);
            String[] arrLine = sLine.split(Pattern.quote(String.valueOf(elementSep)));
            if (arrLine.length >= 9) {
                return arrLine[8];
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    /**
     * Map X12 transaction codes to document types
     */
    private static String getX12TransactionType(String code, String content) {
        char elementSep = content.length() > 3 ? content.charAt(3) : '*';
        switch (code) {
            case "810":
                return "Invoice (810) " + getID_X12("BIG", content, 2);
            case "846":
                return "Consign Movement (846) " + getID_X12("BIA", content, 3);
            case "850":
                return "Purchase Order (850) "
                        + getID_X12("REF" + Pattern.quote(String.valueOf(elementSep)) + "PO", content, 1);
            case "855":
                return "Order Confirmation (855) " + getOC_ID_X12(content);
            case "856":
                return "Ship Notice/ASN (856)" + " " + getID_X12("BSN", content, 2);
            case "820":
                return "Ship Notice/ASN (856)" + " " + getID_X12("TRN", content, 2);
            case "857":
                return "Shipment and Billing Notice (857)";
            case "860":
                return "Purchase Order Change (860) "
                        + getID_X12("REF" + Pattern.quote(String.valueOf(elementSep)) + "PO", content, 1);
            case "861":
                return "Receiving Advice (861) " + getID_X12("BRA", content, 1);
            case "997":
                return "Functional Acknowledgment (997)";
            case "940":
                return "Warehouse Shipping Order (940)";
            case "943":
                return "Warehouse Stock Transfer Shipment Advice (943)";
            case "944":
                return "Warehouse Stock Transfer Receipt Advice (944)";
            case "945":
                return "Warehouse Shipping Advice (945)";
            case "214":
                return "Transportation Carrier Shipment Status (214)";
            case "204":
                return "Motor Carrier Load Tender (204)";
            case "990":
                return "Response to Load Tender (990)";
            default:
                return code; // Just return the code without "X12 Transaction" prefix
        }
    }

    /**
     * Analyze EDIFACT content
     */
    private static PayloadAnalysis analyzeEDIFACT(String content) {
        String docType = null;
        String details = null;

        // EDIFACT uses + as element separator and ' as segment terminator (usually)
        // Find UNH segment (Message Header)
        Pattern unhPattern = Pattern.compile("UNH\\+(\\w+)\\+([^:]+)");
        Matcher unhMatcher = unhPattern.matcher(content);

        if (unhMatcher.find()) {
            String messageRef = unhMatcher.group(1);
            String messageType = unhMatcher.group(2);

            docType = getEDIFACTMessageType(messageType, content);
            details = "Message Type: " + messageType + ", Ref: " + messageRef;
        } else {
            docType = "EDIFACT Document (UNH segment not found)";
        }

        return new PayloadAnalysis("EDIFACT", docType, details);
    }

    /**
     * Map EDIFACT message types to document types
     */
    private static String getEDIFACTMessageType(String messageType, String content) {
        // Message type code is usually the first 6 characters
        String code = messageType.length() >= 6 ? messageType.substring(0, 6) : messageType;

        switch (code) {
            case "ORDERS":
                return "Purchase Order (ORDERS) " + getID_EDIFACT("BGM", content, 2);
            case "ORDCHG":
                return "PO Change (ORDCHG) " + getID_EDIFACT("BGM", content, 2);
            case "ORDRSP":
                return "Order Confirm (ORDRSP) " + getID_EDIFACT("BGM", content, 2);
            case "INVOIC":
                return "Invoice (INVOIC) " + getID_EDIFACT("BGM", content, 2);
            case "DESADV":
                return "ASN (DESADV) " + getID_EDIFACT("BGM", content, 2);
            case "RECADV":
                return "Goods Receipt (RECADV) " + getID_EDIFACT("BGM", content, 2);
            case "CONTRL":
                return "Syntax and Service Report (CONTRL)" + getID_EDIFACT("UNH", content, 1);
            case "APERAK":
                String sERC= getID_EDIFACT("ERC", content, 2);

                String sACW = getID_EDIFACT("RFF", content, 2);
                String[] arrACW = sACW.split(":");
                if (arrACW.length >= 2) {
                    return "SUR (APERAK) " + sERC + " " + arrACW[1];
                } else {
                    return "SUR (APERAK) " + sERC;
                }

            case "PRICAT":
                return "Price/Sales Catalogue (PRICAT)";
            case "INVRPT":
                return "INVRPT " + getID_EDIFACT("BGM", content, 2);
            case "SLSRPT":
                return "Sales Report (SLSRPT)";
            case "DELFOR":
                return "DELFOR " + getID_EDIFACT("BGM", content, 2);
            case "DELJIT":
                return "Delivery Just In Time (DELJIT)";
            case "REMADV":
                return "Remittance Advice (REMADV) " + getID_EDIFACT("BGM", content, 2);
            case "IFTMIN":
                return "Transport (IFTMIN) " + getID_EDIFACT("BGM", content, 2);
            case "IFTMAN":
                return "Arrival Notice (IFTMAN)";
            case "IFTSTA":
                return "Status Report (IFTSTA) " + getID_EDIFACT("BGM", content, 2);
            default:
                return "EDIFACT Message " + code;
        }
    }
}
