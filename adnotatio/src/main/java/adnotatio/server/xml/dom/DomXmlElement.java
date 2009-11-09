/**
 * 
 */
package adnotatio.server.xml.dom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import adnotatio.common.xml.IXmlElement;
import adnotatio.common.xml.IXmlNode;

/**
 * @author kotelnikov
 */
public class DomXmlElement extends DomXmlNode implements IXmlElement {

    private List fChildren;

    public DomXmlElement(Element e) {
        super(e);
    }

    /**
     * @see adnotatio.common.xml.IXmlElement#appendChild(adnotatio.common.xml.IXmlNode)
     */
    public void appendChild(IXmlNode child) {
        Node node = ((DomXmlNode) child).fNode;
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
            result = new DomXmlText((CharacterData) node);
        } else if (node instanceof Element) {
            result = new DomXmlElement((Element) node);
        }
        return result;
    }

}
