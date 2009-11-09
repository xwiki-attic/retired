/**
 * 
 */
package adnotatio.common.xml.gwt;

import adnotatio.common.xml.IXmlDocument;
import adnotatio.common.xml.IXmlNode;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;

/**
 * @author kotelnikov
 */
public class GwtXmlNode implements IXmlNode {

    private IXmlDocument fDocument;

    protected Node fNode;

    public GwtXmlNode(Node node) {
        fNode = node;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof GwtXmlNode))
            return false;
        GwtXmlNode e = (GwtXmlNode) obj;
        return fNode.equals(e.fNode);
    }

    public IXmlDocument getDocument() {
        if (fDocument == null) {
            Document doc = fNode.getOwnerDocument();
            fDocument = new GwtXmlDocument(doc);
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