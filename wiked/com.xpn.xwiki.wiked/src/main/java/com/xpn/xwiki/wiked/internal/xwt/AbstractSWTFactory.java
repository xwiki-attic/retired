
package com.xpn.xwiki.wiked.internal.xwt;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.widgets.Widget;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.xpn.xwiki.wiked.internal.xwt.cf.Reflector;
import com.xpn.xwiki.wiked.internal.xwt.cf.SWTPropertyReader;

/**
 * Abstract SWT objects factory.
 * See subclasses for actual implementations.
 */
public abstract class AbstractSWTFactory {

    private ClassLoader classLoader;
    private XWTFactoryRegistry factoryRegistry;
    private ObjectRegistry objectRegistry;
    
    private SWTPropertyReader reader = SWTPropertyReader.getDefault();

    public void setBuilderContext(XWTBuilderContext context) {
        this.classLoader = context.getClassLoader();
        this.factoryRegistry = context.getFactoryRegistry();
        this.objectRegistry = context.getObjectRegistry();
    }
    
    /**
     * Creates objects in given parent (SWT).
     * @param parent the SWT parent
     * @param element the W3C Node containing XWT fragment
     * @return newly created object
     * @throws XWTException see nested exception
     */
    public Object create(Widget parent, Element element)
        throws XWTException {
        Object object = createObject(parent, element);
        if (object != null) {
            String eid = element.getAttribute("id");
            if (eid != null && eid.length() > 0) {
                ((SimpleObjectRegistry)objectRegistry).setObject(eid, object);
            }
        } else {
            return null;
        }
        try {
            Reflector reflector = new Reflector(object.getClass());
            Map attrs = readProperties(element.getAttributes());
            Iterator keys = attrs.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String)keys.next();
                reflector.setProperty(key, attrs.get(key));
            }
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode instanceof Element && object instanceof Widget) {
                    AbstractSWTFactory childFactory = this.factoryRegistry.getFactory((Element)childNode);
                    if (childFactory != null) {
                        try {
                            Object child = childFactory.create((Widget)object, (Element)childNode);
                            reflector.setProperty(childNode.getNodeName(), child);
                        } catch (Exception ex) {
                            throw new XWTException(ex);
                        }
                    }
                }
            }
             
            try {
                reflector.modifyObject(object);
            } catch (Exception ex) {
                throw new XWTException(ex);
            }

            return object;

        } catch (IntrospectionException ex) {
            throw new XWTException(ex);
        }
    }

    protected ClassLoader getClassLoader() {
    	return this.classLoader;
    }
    
    protected ObjectRegistry getObjectRegistry() {
        return this.objectRegistry;
    }
    
    protected abstract Object createObject(Widget parent, Element element) 
        throws XWTException;

    private Map readProperties(NamedNodeMap attributes) throws XWTException {
        Map properties = new HashMap();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            Object value = reader.parseKey(attr.getNodeValue());
            if (value != null) {
                properties.put(attr.getNodeName(), value);
            }
        }
        return properties;
    }

}
