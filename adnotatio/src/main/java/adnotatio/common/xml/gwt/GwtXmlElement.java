/**
 * 
 */
package adnotatio.common.xml.gwt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adnotatio.common.xml.IXmlElement;
import adnotatio.common.xml.IXmlNode;

import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.CharacterData;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * @author kotelnikov
 */
public class GwtXmlElement extends GwtXmlNode implements IXmlElement {

    private List fChildren;

    public GwtXmlElement(Element e) {
        super(e);
    }

    /**
     * @see adnotatio.common.xml.IXmlElement#appendChild(adnotatio.common.xml.IXmlNode)
     */
    public void appendChild(IXmlNode child) {
        Node node = ((GwtXmlNode) child).fNode;
        getElement().appendChild(node);
        fChildren = null;
    }

    /**
     * @see adnotatio.common.xml.IXmlElement#getAttribute(java.lang.String)
     */
    public String getAttribute(String name) {
        return getElement().getAttribute(name);
    }

    /**
     * @see adnotatio.common.xml.IXmlElement#getAttributeNames()
     */
    public Set getAttributeNames() {
        Set set = new HashSet();
        NamedNodeMap attrs = getElement().getAttributes();
        int len = attrs.getLength();
        for (int i = 0; i < len; i++) {
            Attr attr = (Attr) attrs.item(i);
            String name = attr.getName();
            set.add(name);
        }
        return set;
    }

    /**
     * @see adnotatio.common.xml.IXmlElement#getChild(int)
     */
    public IXmlNode getChild(int pos) {
        List list = getChildren();
        if (pos < 0 || pos >= list.size())
            throw new IndexOutOfBoundsException();
        IXmlNode node = (IXmlNode) list.get(pos);
        return node;
    }

    /**
     * @see adnotatio.common.xml.IXmlElement#getChildNumber()
     */
    public int getChildNumber() {
        List children = getChildren();
        return children.size();
    }

    private List getChildren() {
        if (fChildren == null) {
            fChildren = new ArrayList();
            NodeList list = getElement().getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                IXmlNode xmlNode = newNode(node);
                if (xmlNode != null) {
                    fChildren.add(xmlNode);
                }
            }
        }
        return fChildren;
    }

    /**
     * @return the element
     */
    private Element getElement() {
        return (Element) fNode;
    }

    /**
     * @see adnotatio.common.xml.IXmlElement#getNodeName()
     */
    public String getNodeName() {
        return getElement().getNodeName();
    }

    /**
     * @param node
     * @return
     */
    protected IXmlNode newNode(Node node) {
        IXmlNode result = null;
        if (node instanceof CharacterData) {
            result = new GwtXmlText((CharacterData) node);
        } else if (node instanceof Element) {
            result = new GwtXmlElement((Element) node);
        }
        return result;
    }

}
