package de.mendelson.util.xmleditorkit;

import java.io.IOException;
import java.io.InputStream;
import org.w3c.dom.Element;
import javax.swing.text.Document;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * XML Editor Kit - based on code from Stanislav Lapitsky
 *
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class XMLEditorKitXMLReader {

    static final XMLEditorKitXMLReader instance = new XMLEditorKitXMLReader();

    private XMLEditorKitXMLReader() {

    }

    public static XMLEditorKitXMLReader getInstance() {
        return instance;
    }

    public void read(InputStream inputStream, Document document, int pos) throws IOException, BadLocationException {
        if (!(document instanceof XMLStyledDocument)) {
            return;
        }
        XMLStyledDocument xmlDocument = (XMLStyledDocument) document;
        xmlDocument.setUserChanges(false);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setCoalescing(true);
        builderFactory.setValidating(false);
        builderFactory.setIgnoringComments(false);
        builderFactory.setIgnoringElementContentWhitespace(false);
        builderFactory.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        builderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        builderFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try {
            builderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (Exception e) {
        }
        try {
            builderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        } catch (Exception e) {
        }
        try {
            builderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception e) {
        }
        try {
            // Disable external DTDs
            builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (Exception e) {
        }
        // per Timothy Morgans 2014 paper: "XML Schema, DTD, and Entity Attacks"
        builderFactory.setXIncludeAware(false);
        builderFactory.setExpandEntityReferences(false);

        try {
            //Using factory get an instance of document builder
            javax.xml.parsers.DocumentBuilder documentBuilderXML = builderFactory.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
            documentBuilderXML.setErrorHandler(null);
            org.w3c.dom.Document dom = documentBuilderXML.parse(inputStream);
            List<DefaultStyledDocument.ElementSpec> specsList = new ArrayList<DefaultStyledDocument.ElementSpec>();
            DefaultStyledDocument.ElementSpec elementSpec
                    = new DefaultStyledDocument.ElementSpec(new SimpleAttributeSet(), DefaultStyledDocument.ElementSpec.EndTagType);
            specsList.add(elementSpec);
            if (xmlDocument.getLength() == 0) {
                writeNode(xmlDocument, dom, pos, specsList);
            } else {
                writeNode(xmlDocument, dom.getDocumentElement(), pos, specsList);
            }
            DefaultStyledDocument.ElementSpec[] data = new DefaultStyledDocument.ElementSpec[specsList.size()];
            specsList.toArray(data);
            xmlDocument.insert(pos, data);
            xmlDocument.setUserChanges(true);
        } catch (Throwable pce) {
            throw new IOException(pce.getMessage());
        }
    }

    /**
     * Collects all nodes in a list, they are written to the document afterwards
     * in a later step
     */
    public int writeNode(Document doc, Node node, int pos, List<DefaultStyledDocument.ElementSpec> specsList) throws BadLocationException {
        SimpleAttributeSet tagAttributeSet = new SimpleAttributeSet();
        tagAttributeSet.addAttribute(AbstractDocument.ElementNameAttribute, XMLStyledDocument.TAG_ELEMENT);
        SimpleAttributeSet tagRowStartAttributs = new SimpleAttributeSet();
        tagRowStartAttributs.addAttribute(AbstractDocument.ElementNameAttribute, XMLStyledDocument.TAG_ROW_START_ELEMENT);
        SimpleAttributeSet tagRowEndAttributs = new SimpleAttributeSet();
        tagRowEndAttributs.addAttribute(AbstractDocument.ElementNameAttribute, XMLStyledDocument.TAG_ROW_END_ELEMENT);
        DefaultStyledDocument.ElementSpec elementSpec;
        elementSpec = new DefaultStyledDocument.ElementSpec(tagAttributeSet, DefaultStyledDocument.ElementSpec.StartTagType);
        specsList.add(elementSpec);
        elementSpec = new DefaultStyledDocument.ElementSpec(tagRowStartAttributs, DefaultStyledDocument.ElementSpec.StartTagType);
        specsList.add(elementSpec);
        int offs = pos;
        //"<"
        elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.BRACKET_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, "<".toCharArray(), 0, 1);
        specsList.add(elementSpec);
        //tag name
        if (node instanceof org.w3c.dom.Document && doc.getLength() == 0) {
            //root node
            org.w3c.dom.Document documentNode = (org.w3c.dom.Document) node;
            String nodeStr = "?xml version=\"" + documentNode.getXmlVersion() + "\" encoding=\"" + documentNode.getXmlEncoding() + "\"?";
            elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.TAGNAME_ATTRIBUTES,
                    DefaultStyledDocument.ElementSpec.ContentType,
                    nodeStr.toCharArray(), 0, nodeStr.length());
        } else {
            elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.TAGNAME_ATTRIBUTES,
                    DefaultStyledDocument.ElementSpec.ContentType,
                    node.getNodeName().toCharArray(), 0, node.getNodeName().length());
        }
        specsList.add(elementSpec);

        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null && attributes.getLength() > 0) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                String name = attribute.getNodeName();
                String value = attribute.getNodeValue();
                String attributeDelimiter = " ";
                elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.BRACKET_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, attributeDelimiter.toCharArray(), 0, attributeDelimiter.length());
                specsList.add(elementSpec);
                //attribute name
                elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.ATTRIBUTENAME_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, name.toCharArray(), 0, name.length());
                specsList.add(elementSpec);
                // '="'
                elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.BRACKET_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, "=\"".toCharArray(), 0, 2);
                specsList.add(elementSpec);
                //attribute value
                elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.ATTRIBUTEVALUE_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, value.toCharArray(), 0, value.length());
                specsList.add(elementSpec);
                // '"'
                elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.BRACKET_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, "\"".toCharArray(), 0, 1);
                specsList.add(elementSpec);
            }
        }
        org.w3c.dom.NodeList nodeList = node.getChildNodes();
        if (nodeList != null && nodeList.getLength() > 0) {
            //">"
            elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.BRACKET_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, ">\n".toCharArray(), 0, 2);
            specsList.add(elementSpec);
            elementSpec = new DefaultStyledDocument.ElementSpec(tagRowStartAttributs, DefaultStyledDocument.ElementSpec.EndTagType);
            specsList.add(elementSpec);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node childNode = nodeList.item(i);
                if (childNode instanceof Element) {
                    //Element node
                    Element child = (Element) childNode;
                    offs += writeNode(doc, child, offs, specsList);
                } else if (childNode.getNodeType() == Node.COMMENT_NODE) {
                    //Comment node
                    String childNodeValue = childNode.getNodeValue();
                    int linefeedIndex = childNodeValue.indexOf("\n");
                    while (linefeedIndex > 0) {
                        elementSpec = new DefaultStyledDocument.ElementSpec(tagRowStartAttributs, DefaultStyledDocument.ElementSpec.StartTagType);
                        specsList.add(elementSpec);
                        String value = childNodeValue.substring(0, linefeedIndex);
                        elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.COMMENT_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, value.toCharArray(), 0, value.length());
                        specsList.add(elementSpec);
                        elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.COMMENT_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, "\n".toCharArray(), 0, 1);
                        specsList.add(elementSpec);
                        elementSpec = new DefaultStyledDocument.ElementSpec(tagRowStartAttributs, DefaultStyledDocument.ElementSpec.EndTagType);
                        specsList.add(elementSpec);
                        childNodeValue = childNodeValue.substring(linefeedIndex + 1);
                        linefeedIndex = childNodeValue.indexOf("\n");
                    }
                    elementSpec = new DefaultStyledDocument.ElementSpec(tagRowStartAttributs, DefaultStyledDocument.ElementSpec.StartTagType);
                    specsList.add(elementSpec);

                    elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.COMMENT_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, childNodeValue.toCharArray(), 0, childNodeValue.length());
                    specsList.add(elementSpec);
                    elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.COMMENT_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, "\n".toCharArray(), 0, 1);
                    specsList.add(elementSpec);

                    elementSpec = new DefaultStyledDocument.ElementSpec(tagRowStartAttributs, DefaultStyledDocument.ElementSpec.EndTagType);
                    specsList.add(elementSpec);
                } else {
                    //plain text node
                    elementSpec = new DefaultStyledDocument.ElementSpec(tagRowStartAttributs, DefaultStyledDocument.ElementSpec.StartTagType);
                    specsList.add(elementSpec);
                    String plainTextNodeValue = childNode.getNodeValue();
                    if (plainTextNodeValue != null && !plainTextNodeValue.trim().isEmpty()) {
                        //do not add an additional line if the content of the node is just whitespace..else it looks weird if there are just CR LFs in the content
                        elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.PLAIN_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, plainTextNodeValue.toCharArray(), 0, plainTextNodeValue.length());
                        specsList.add(elementSpec);
                        elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.PLAIN_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, "\n".toCharArray(), 0, 1);
                        specsList.add(elementSpec);
                    }
                    elementSpec = new DefaultStyledDocument.ElementSpec(tagRowStartAttributs, DefaultStyledDocument.ElementSpec.EndTagType);
                    specsList.add(elementSpec);
                }
            }
            elementSpec = new DefaultStyledDocument.ElementSpec(tagRowEndAttributs, DefaultStyledDocument.ElementSpec.StartTagType);
            specsList.add(elementSpec);
            if (node instanceof org.w3c.dom.Document) {
                //document node
                elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.TAGNAME_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, " ".toCharArray(), 0, 1);
                specsList.add(elementSpec);
            } else {
                //"</"
                elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.BRACKET_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, "</".toCharArray(), 0, 2);
                specsList.add(elementSpec);
                //tag name
                elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.TAGNAME_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, node.getNodeName().toCharArray(), 0, node.getNodeName().length());
                specsList.add(elementSpec);
                //"/>"
                elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.BRACKET_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, ">\n".toCharArray(), 0, 2);
                specsList.add(elementSpec);
            }
            elementSpec = new DefaultStyledDocument.ElementSpec(tagRowEndAttributs, DefaultStyledDocument.ElementSpec.EndTagType);
            specsList.add(elementSpec);
        } else {
            //"/>"
            elementSpec = new DefaultStyledDocument.ElementSpec(XMLStyledDocument.BRACKET_ATTRIBUTES, DefaultStyledDocument.ElementSpec.ContentType, "/>\n".toCharArray(), 0, 3);
            specsList.add(elementSpec);
            elementSpec = new DefaultStyledDocument.ElementSpec(new SimpleAttributeSet(), DefaultStyledDocument.ElementSpec.EndTagType);
            specsList.add(elementSpec);
        }
        elementSpec = new DefaultStyledDocument.ElementSpec(tagAttributeSet, DefaultStyledDocument.ElementSpec.EndTagType);
        specsList.add(elementSpec);

        return offs - pos;
    }

    public int writeNodeNotUsed(Document doc, Element node, int pos) throws BadLocationException {
        int offs = pos;
        doc.insertString(offs, "<", XMLStyledDocument.BRACKET_ATTRIBUTES);
        offs++;
        doc.insertString(offs, node.getNodeName(), XMLStyledDocument.TAGNAME_ATTRIBUTES);
        offs += node.getNodeName().length();

        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null && attrs.getLength() > 0) {
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                String name = attr.getNodeName();
                String value = attr.getNodeValue();
                doc.insertString(offs, " ", XMLStyledDocument.BRACKET_ATTRIBUTES);
                offs++;
                doc.insertString(offs, name, XMLStyledDocument.ATTRIBUTENAME_ATTRIBUTES);
                offs += name.length();
                doc.insertString(offs, "=\"", XMLStyledDocument.BRACKET_ATTRIBUTES);
                offs += 2;
                doc.insertString(offs, value, XMLStyledDocument.ATTRIBUTEVALUE_ATTRIBUTES);
                offs += value.length();
                doc.insertString(offs, "\"", XMLStyledDocument.BRACKET_ATTRIBUTES);
                offs += 1;
            }
        }
        org.w3c.dom.NodeList list = node.getChildNodes();
        if (list != null && list.getLength() > 0) {
            doc.insertString(offs, ">\n", XMLStyledDocument.BRACKET_ATTRIBUTES);
            offs += 2;
            for (int i = 0; i < list.getLength(); i++) {
                Node n = list.item(i);
                if (n instanceof Element) {
                    Element child = (Element) n;
                    offs += writeNodeNotUsed(doc, child, offs);
                } else {
                    //plain text
                    doc.insertString(offs, n.getNodeValue() + "\n", XMLStyledDocument.PLAIN_ATTRIBUTES);
                    offs += n.getNodeValue().length() + 1;
                }
            }
            doc.insertString(offs, "<", XMLStyledDocument.BRACKET_ATTRIBUTES);
            offs++;
            doc.insertString(offs, node.getNodeName(), XMLStyledDocument.TAGNAME_ATTRIBUTES);
            offs += node.getNodeName().length();
            doc.insertString(offs, "/>\n", XMLStyledDocument.BRACKET_ATTRIBUTES);
            offs += 3;
        } else {
            doc.insertString(offs, "/>\n", XMLStyledDocument.BRACKET_ATTRIBUTES);
            offs += 3;
        }

        return offs - pos;
    }

}
