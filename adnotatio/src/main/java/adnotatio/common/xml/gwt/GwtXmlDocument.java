/**
 * 
 */
package adnotatio.common.xml.gwt;

import adnotatio.common.xml.IXmlDocument;
import adnotatio.common.xml.IXmlElement;
import adnotatio.common.xml.IXmlText;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;

/**
 * @author kotelnikov
 */
public class GwtXmlDocument extends GwtXmlNode implements IXmlDocument {

    /**
     * 
     */
    public GwtXmlDocument(Document doc) {
        super(doc);
    }

    /**
     * @see adnotatio.common.xml.IXmlDocument#createElement(java.lang.String)
     */
    public IXmlElement createElement(String name) {
        return new GwtXmlElement(getDoc().createElement(name));
    }

    /**
     * @see adnotatio.common.xml.IXmlDocument#createTextNode(java.lang.String)
     */
    public IXmlText createTextNode(String content) {
        return new GwtXmlText(getDoc().createTextNode(content));
    }

    private Document getDoc() {
        return (Document) fNode;
    }

    /**
     * @see adnotatio.common.xml.IXmlDocument#getRootElement()
     */
    public IXmlElement getRootElement() {
        Element e = (Element) getDoc().getFirstChild();
        return new GwtXmlElement(e);
    }

    /**
     * @see adnotatio.common.xml.IXmlDocument#setRootElement(adnotatio.common.xml.IXmlElement)
     */
    public void setRootElement(IXmlElement root) {
        GwtXmlElement e = (GwtXmlElement) root;
        getDoc().appendChild(e.fNode);
    }

}
