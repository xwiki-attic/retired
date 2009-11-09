/**
 * 
 */
package adnotatio.server.xml.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import adnotatio.common.xml.IXmlDocument;
import adnotatio.common.xml.IXmlElement;
import adnotatio.common.xml.IXmlText;


/**
 * @author kotelnikov
 */
public class DomXmlDocument extends DomXmlNode implements IXmlDocument {

    /**
     * 
     */
    public DomXmlDocument(Document doc) {
        super(doc);
    }

    /**
     * @see adnotatio.common.xml.IXmlDocument#createElement(java.lang.String)
     */
    public IXmlElement createElement(String name) {
        return new DomXmlElement(getDoc().createElement(name));
    }

    /**
     * @see adnotatio.common.xml.IXmlDocument#createTextNode(java.lang.String)
     */
    public IXmlText createTextNode(String content) {
        return new DomXmlText(getDoc().createTextNode(content));
    }

    private Document getDoc() {
        return (Document) fNode;
    }

    /**
     * @see adnotatio.common.xml.IXmlDocument#getRootElement()
     */
    public IXmlElement getRootElement() {
        Element e = (Element) getDoc().getFirstChild();
        return new DomXmlElement(e);
    }

    /**
     * @see adnotatio.common.xml.IXmlDocument#setRootElement(adnotatio.common.xml.IXmlElement)
     */
    public void setRootElement(IXmlElement root) {
        DomXmlElement e = (DomXmlElement) root;
        getDoc().appendChild(e.fNode);
    }

}
