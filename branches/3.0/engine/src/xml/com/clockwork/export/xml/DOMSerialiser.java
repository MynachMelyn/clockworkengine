
  
package com.clockwork.export.xml;

import java.io.*;
import java.nio.charset.Charset;
import org.w3c.dom.*;

/**
 * The DOMSerialiser was based primarily off the DOMSerialiser.java class from the 
 * "Java and XML" 3rd Edition book by Brett McLaughlin, and Justin Edelson. Some 
 * modifications were made to support formatting of elements and attributes.
 * 
 * @version $Revision: 4207 $, $Date: 2009-03-29 11:19:16 -0400 (Sun, 29 Mar 2009) $
 */
public class DOMSerialiser {

    /** The encoding to use for output (default is UTF-8) */
    private Charset encoding = Charset.forName("utf-8");

    /** The amount of indentation to use (default is 4 spaces). */
    private int indent = 4;

    /** The line separator to use (default is the based on the current system settings). */
    private String lineSeparator = System.getProperty("line.separator", "\n");

    private void escape(Writer writer, String s) throws IOException {
        if (s == null) { return; }
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            switch (c) {
            case '<':
                writer.write("&lt;");
                break;
            case '>':
                writer.write("&gt;");
                break;
            case '&':
                writer.write("&amp;");
                break;
            case '\r':
                writer.write("&#xD;");
                break;
            default:
                writer.write(c);
            }
        }
    }

    /**
     * Serialise doc} to out}
     * 
     * @param doc the document to serialise.
     * @param file the file to serialise to.
     * @throws IOException
     */
    public void serialise(Document doc, File file) throws IOException {
        serialise(doc, new FileOutputStream(file));
    }

    /**
     * Serialise doc} to out}
     * 
     * @param doc the document to serialise.
     * @param out the stream to serialise to.
     * @throws IOException
     */
    public void serialise(Document doc, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, encoding);
        write(doc, writer, 0);
        writer.flush();
    }

    /**
     * Set the encoding used by this serialiser.
     * 
     * @param encoding the encoding to use, passing in null} results in the
     *  default encoding (UTF-8) being set.
     * @throws IllegalCharsetNameException if the given charset name is illegal.
     * @throws UnsupportedCharsetException if the given charset is not supported by the
     *  current JVM.
     */
    public void setEncoding(String encoding) {
        this.encoding = Charset.forName(encoding);
    }

    /**
     * Set the number of spaces to use for indentation.
     * 
     * The default is to use 4 spaces.
     * 
     * @param indent the number of spaces to use for indentation, values less than or
     *  equal to zero result in no indentation being used.
     */
    public void setIndent(int indent) {
        this.indent = indent >= 0 ? indent : 0;
    }

    /**
     * Set the line separator that will be used when serializing documents.
     * 
     * If this is not called then the serialiser uses a default based on the
     * line.separator} system property. 
     * 
     * @param lineSeparator the line separator to set.
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    private void write(Node node, Writer writer, int depth) throws IOException {
        switch (node.getNodeType()) {
        case Node.DOCUMENT_NODE:
            writeDocument((Document) node, writer);
            break;
        case Node.ELEMENT_NODE:
            writeElement((Element) node, writer, depth);
            break;
        case Node.TEXT_NODE:
            escape(writer, node.getNodeValue());
            break;
        case Node.CDATA_SECTION_NODE:
            writer.write("<![CDATA[");
            escape(writer, node.getNodeValue());
            writer.write("]]>");
            break;
        case Node.COMMENT_NODE:
            for (int i = 0; i < depth; ++i) { writer.append(' '); }
            writer.append("<!-- ").append(node.getNodeValue()).append(" -->").append(lineSeparator);
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            String n = node.getNodeName();
            String v = node.getNodeValue();
            for (int i = 0; i < depth; ++i) { writer.append(' '); }
            writer.append("<?").append(n).append(' ').append(v).append("?>").append(lineSeparator);
            break;
        case Node.ENTITY_REFERENCE_NODE:
            writer.append('&').append(node.getNodeName()).append(';');
            break;
        case Node.DOCUMENT_TYPE_NODE:
            writeDocumentType((DocumentType) node, writer, depth);
            break;
        }
    }

    private void writeDocument(Document document, Writer writer) throws IOException {
        String v = document.getXmlVersion();

        writer.append("<?xml ");
        writer.append(" version='").append(v == null ? "1.0" : v).append("'");
        writer.append(" encoding='").append(encoding.name()).append("'");
        if (document.getXmlStandalone()) {
            writer.append(" standalone='yes'");
        }
        writer.append("?>").append(lineSeparator);

        NodeList nodes = document.getChildNodes();
        for (int i = 0, imax = nodes.getLength(); i < imax; ++i) {
            write(nodes.item(i), writer, 0);
        }
    }

    private void writeDocumentType(DocumentType docType, Writer writer, int depth) throws IOException {
        String publicId = docType.getPublicId();
        String internalSubset = docType.getInternalSubset();

        for (int i = 0; i < depth; ++i) { writer.append(' '); }
        writer.append("<!DOCTYPE ").append(docType.getName());
        if (publicId != null) {
            writer.append(" PUBLIC '").append(publicId).append("' ");
        } else {
            writer.write(" SYSTEM ");
        }
        writer.append("'").append(docType.getSystemId()).append("'");
        if (internalSubset != null) {
            writer.append(" [").append(internalSubset).append("]");
        }
        writer.append('>').append(lineSeparator);
    }

    private void writeElement(Element element, Writer writer, int depth) throws IOException {
        for (int i = 0; i < depth; ++i) { writer.append(' '); }
        writer.append('<').append(element.getTagName());
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0, imax = attrs.getLength(); i < imax; ++i) {
            Attr attr = (Attr) attrs.item(i);
            writer.append(' ').append(attr.getName()).append("='").append(attr.getValue()).append("'");
        }
        NodeList nodes = element.getChildNodes();
        if (nodes.getLength() == 0) {
            // no children, so just close off the element and return
            writer.append("/>").append(lineSeparator);
            return;
        }
        writer.append('>').append(lineSeparator);
        for (int i = 0, imax = nodes.getLength(); i < imax; ++i) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.ATTRIBUTE_NODE) { continue; }
            write(n, writer, depth + indent);
        }
        for (int i = 0; i < depth; ++i) { writer.append(' '); }
        writer.append("</").append(element.getTagName()).append('>').append(lineSeparator);
    }

}
