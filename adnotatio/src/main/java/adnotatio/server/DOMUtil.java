package adnotatio.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DOMUtil {

    public static Document newDocument() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder();
        return builder.newDocument();
    }

    public static Document readXML(InputStream input)
        throws ParserConfigurationException,
        SAXException,
        IOException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder();
            Document doc = builder.parse(input);
            return doc;
        } finally {
            input.close();
        }
    }

    public static Document readXML(Reader reader)
        throws ParserConfigurationException,
        SAXException,
        IOException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder();
            InputSource source = new InputSource(reader);
            Document doc = builder.parse(source);
            return doc;
        } finally {
            reader.close();
        }
    }
}
