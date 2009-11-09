/**
 * 
 */
package adnotatio.server.xml.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import adnotatio.common.xml.IXmlDocument;
import adnotatio.common.xml.IXmlNode;

/**
 * @author kotelnikov
 */
public class DomXmlNode implements IXmlNode {

    private IXmlDocument fDocument;

    protected Node fNode;

    public DomXmlNode(Node node) {
        fNode = node;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof DomXmlNode))
            return false;
        DomXmlNode e = (DomXmlNode) obj;
        return fNode.equals(e.fNode);
    }

    public IXmlDocument getDocument() {
        if (fDocument == null) {
            Document doc = fNode.getOwnerDocument();
            fDocument = new DomXmlDocument(doc);
        }
        return fDocument;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return fNode.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return fNode.toString();
    }

}