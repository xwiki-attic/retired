/**
 * 
 */
package adnotatio.common.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import adnotatio.common.data.IPropertiesContainer;
import adnotatio.common.data.PropertiesContainer;

/**
 * This is an utility class containing methods used for XML
 * serialization/deserialization.
 * 
 * @author kotelnikov
 */
public class SerializationUtil {

    private static SerializationUtil fInstance = new SerializationUtil();

    public final static SerializationUtil getInstance() {
        return fInstance;
    }

    /**
     * 
     */
    public SerializationUtil() {
        super();
    }

    /**
     * Reads properties from the specified element and adds them to the given
     * properties object
     * 
     * @param e the XML element containing the serialized properties
     * @param properties to this object the loaded properties will be added
     */
    public void deserializeProperties(
        IXmlElement e,
        IPropertiesContainer properties) {
        int len = e.getChildNumber();
        for (int i = 0; i < len; i++) {
            IXmlNode child = e.getChild(i);
            if (!(child instanceof IXmlElement))
                continue;
            IXmlElement childElement = (IXmlElement) child;
            StringBuffer buf = new StringBuffer();
            serializeNodeContent(childElement, buf);
            String name = childElement.getNodeName();
            if (buf.length() > 0) {
                properties.addValue(name, buf.toString());
            }
        }
    }

    /**
     * Reads properties from the given XML element and adds them to the
     * specified list
     * 
     * @param e the XML element containing serialized properties
     * @param list the list to fill with properties objects
     */
    public void deserializePropertiesList(IXmlElement e, Collection list) {
        int len = e.getChildNumber();
        for (int i = 0; i < len; i++) {
            IXmlNode child = e.getChild(i);
            if (child instanceof IXmlElement) {
                IXmlElement childElement = (IXmlElement) child;
                IPropertiesContainer container = newPropertiesContainer();
                deserializeProperties(childElement, container);
                list.add(container);
            }
        }
    }

    /**
     * Escapes (if required) all special symbols in the given string and appends
     * the result to the given buffer
     * 
     * @param value the value to escape
     * @param buf the buffer where the escaped value should be appended
     */
    private void escape(String value, StringBuffer buf) {
        char[] array = value.toCharArray();
        for (int i = 0; i < array.length; i++) {
            char ch = array[i];
            switch (ch) {
                case '&':
                    buf.append("&amp;");
                    break;
                case '<':
                    buf.append("&lt;");
                    break;
                case '>':
                    buf.append("&gt;");
                    break;
                default:
                    buf.append(ch);
                    break;
            }
        }
    }

    /**
     * Escapes all not valid symbols in the given attribute name
     * 
     * @param name the attribute name to escape
     * @param buf the buffer where the results will be appended
     */
    protected void escapeAttrName(String name, StringBuffer buf) {
        escape(name, buf);
    }

    /**
     * Escapes the attribute value and appends it to the given buffer
     * 
     * @param value the value to escape
     * @param buf the buffer where the resulting value will be appended
     */
    protected void escapeAttrValue(String value, StringBuffer buf) {
        escape(value, buf);
    }

    /**
     * Escapes the tag name and appends it to the given buffer
     * 
     * @param value the name of the tag to escape
     * @param buf the buffer where the resulting value will be appended
     */
    protected void escapeTagName(String value, StringBuffer buf) {
        escape(value, buf);
    }

    /**
     * Creates and returns a new properties container
     * 
     * @return a newly created properties container
     */
    protected IPropertiesContainer newPropertiesContainer() {
        return new PropertiesContainer();
    }

    /**
     * Appends serialized annotations to the given XML document
     * 
     * @param document the document where the serialized annotations will be
     *        appended
     * @param list a list of annotations to serialize
     */
    public void serializeAnnotations(IXmlDocument document, Collection list) {
        serializePropertiesList(document, list, "annotations", "annotation");
    }

    /**
     * Generates a closing tag corresponding to the specified XML node and
     * appends it to the given buffer
     * 
     * @param node the node for which the closing tag should be generated
     * @param buf the buffer where the result is appended
     */
    public void serializeCloseTag(IXmlElement node, StringBuffer buf) {
        IXmlElement e = (IXmlElement) node;
        String name = e.getNodeName();
        buf.append("</");
        escapeTagName(name, buf);
        buf.append(">");
    }

    /**
     * Serializes the given element as XML and appends it to the given string
     * buffer
     * 
     * @param e the element to serialize
     * @param buf the string buffer used to accumulate the content of the node
     */
    public void serializeNode(IXmlElement e, StringBuffer buf) {
        serializeOpenTag(e, buf);
        serializeNodeContent(e, buf);
        serializeCloseTag(e, buf);
    }

    /**
     * Serializes all children of the given element as XML and appends this XML
     * to the given string buffer
     * 
     * @param e the element to serialize
     * @param buf the string buffer used to accumulate the string content of the
     *        node
     */
    public void serializeNodeContent(IXmlElement e, StringBuffer buf) {
        int len = e.getChildNumber();
        for (int i = 0; i < len; i++) {
            IXmlNode child = e.getChild(i);
            if (child instanceof IXmlElement) {
                IXmlElement c = (IXmlElement) child;
                serializeOpenTag(c, buf);
                serializeNodeContent(c, buf);
                serializeCloseTag(c, buf);
            } else if (child instanceof IXmlText) {
                IXmlText text = (IXmlText) child;
                serializeText(text, buf);
            }
        }
    }

    /**
     * Generates an opening tag corresponding to the specified XML node and
     * appends it to the buffer
     * 
     * @param node the node for which an opening tag should be generated
     * @param buf the buffer where the result is appended
     */
    public void serializeOpenTag(IXmlElement e, StringBuffer buf) {
        String name = e.getNodeName();
        buf.append("<");
        escapeTagName(name, buf);
        Set attributes = e.getAttributeNames();
        for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
            String attrName = (String) iterator.next();
            String attrValue = e.getAttribute(attrName);
            buf.append(" ");
            escapeAttrName(attrName, buf);
            buf.append("='");
            escapeAttrValue(attrValue, buf);
            buf.append("'");
        }
        buf.append(">");
    }

    /**
     * Serializes the given properties in the element object
     * 
     * @param properties the properties to serialize
     * @param element the properties object will be serialized as XML and
     *        appended to this element
     */
    public void serializeProperties(
        IPropertiesContainer properties,
        IXmlElement element) {
        Set names = properties.getNames();
        IXmlDocument doc = element.getDocument();
        for (Iterator iterator = names.iterator(); iterator.hasNext();) {
            String propertyName = (String) iterator.next();
            Object value = properties.getValue(propertyName);
            IXmlElement child = doc.createElement(propertyName);
            element.appendChild(child);
            if (value instanceof IPropertiesContainer) {
                serializeProperties((IPropertiesContainer) value, child);
            } else {
                String strValue = value != null ? value.toString() : "";
                IXmlText text = doc.createTextNode(strValue);
                child.appendChild(text);
            }
        }
    }

    /**
     * Serializes the given collection with properties (@link
     * {@link IPropertiesContainer}) as an XML document
     * 
     * @param doc the document used to serialize
     * @param propertiesList a collection containing
     *        {@link IPropertiesContainer} instances
     * @param rootName the name of the root element to add to the document
     * @param elementName the name of elements containing individual
     *        {@link IPropertiesContainer} instances
     */
    public void serializePropertiesList(
        IXmlDocument doc,
        Collection propertiesList,
        String rootName,
        String elementName) {
        IXmlElement root = doc.createElement(rootName);
        doc.setRootElement(root);
        for (Iterator iterator = propertiesList.iterator(); iterator.hasNext();) {
            IPropertiesContainer properties = (IPropertiesContainer) iterator
                .next();
            IXmlElement e = doc.createElement(elementName);
            root.appendChild(e);
            serializeProperties(properties, e);
        }
    }

    /**
     * Serializes the given text node and appends its content to the given
     * buffer
     * 
     * @param node the text node to serialize
     * @param buf the buffer where the result is appended
     */
    public void serializeText(IXmlText node, StringBuffer buf) {
        escape(node.getText(), buf);
    }

}
