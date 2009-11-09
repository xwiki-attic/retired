/**
 * 
 */
package adnotatio.client.renderer.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import adnotatio.common.cache.CacheBuilder;
import adnotatio.common.cache.CompositeCacheNode;

/**
 * @author kotelnikov
 */
public class DomCacheBuilder extends CacheBuilder {

    /**
     * 
     */
    public DomCacheBuilder() {
        super();
    }

    @Override
    protected Iterator getChildren(Object node, CompositeCacheNode compositeNode) {
        if (!(node instanceof Node))
            return null;
        Node e = (Node) node;
        final NodeList children = e.getChildNodes();
        return new Iterator() {

            int fPos = 0;

            public boolean hasNext() {
                return fPos < children.getLength();
            }

            public Object next() {
                if (!hasNext())
                    return null;
                return children.item(fPos++);
            }

            public void remove() {
                throw new RuntimeException();
            }

        };
    }

    @Override
    protected CompositeCacheNode getCompositeNode(Object node) {
        if (!(node instanceof Element))
            return null;
        Element e = (Element) node;
        CompositeCacheNode result = null;
        if (isDynamic(e)) {
            result = new CompositeCacheNode();
            result.setData("name", e.getNodeName());
            Map params = getParams(e);
            result.setData("params", params);
        }
        return result;
    }

    private Map getParams(Element e) {
        Map params = new HashMap();
        NamedNodeMap attrs = e.getAttributes();
        int len = attrs.getLength();
        for (int i = 0; i < len; i++) {
            Node item = attrs.item(i);
            String name = item.getNodeName();
            String value = item.getNodeValue();
            params.put(name, value);
        }
        return params;
    }

    @Override
    protected String getTextPrefix(Object node, CompositeCacheNode compositeNode) {
        if (node instanceof Element) {
            Element e = (Element) node;
            if (isDynamic(e))
                return null;
            String name = e.getNodeName();
            String tag = "<" + name;
            NamedNodeMap attributes = e.getAttributes();
            int len = attributes != null ? attributes.getLength() : 0;
            for (int i = 0; i < len; i++) {
                Attr attr = (Attr) attributes.item(i);
                tag += " " + attr.getName() + "='";
                tag += attr.getNodeValue();
                tag += "'";
            }
            tag += ">";
            return tag;
        } else if (node instanceof CharacterData) {
            CharacterData data = (CharacterData) node;
            return data.getData();
        }
        return null;
    }

    @Override
    protected String getTextSuffix(Object node, CompositeCacheNode compositeNode) {
        if (node instanceof Element) {
            Element e = (Element) node;
            if (isDynamic(e))
                return null;
            String name = e.getNodeName();
            String tag = "</" + name + ">";
            return tag;
        }
        return null;
    }

    private boolean isDynamic(Element e) {
        String name = e.getNodeName();
        return name.indexOf(':') > 0;
    }

    @Override
    protected CompositeCacheNode newTopNode(Object object) {
        return new CompositeCacheNode();
    }

}
