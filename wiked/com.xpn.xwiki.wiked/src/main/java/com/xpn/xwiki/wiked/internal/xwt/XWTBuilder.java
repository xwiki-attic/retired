
package com.xpn.xwiki.wiked.internal.xwt;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;


/** 
 * Instantiates SWT components
 */
public class XWTBuilder {

	private static final String CFM = "XWTBuilder.map";
    
    private static final DocumentBuilderFactory DBF = 
        DocumentBuilderFactory.newInstance();
	
    private ClassLoader classLoader;
	private DefaultSWTFactoryRegistry factoryRegistry;
	private AbstractSWTFactory defaultFactory;
    private SimpleObjectRegistry objectRegistry;
	
    /** Creates a builder with default set of composite factories */
    public XWTBuilder() throws XWTException {
        this(XWTBuilder.class.getClassLoader(), 
            XWTBuilder.class.getResourceAsStream(CFM));
    }

    /** Creates a builder with default set of composite factories */
	public XWTBuilder(ClassLoader cl) throws XWTException {
		this(cl, XWTBuilder.class.getResourceAsStream(CFM));
	}

	/** 
	 * Creates a builder with provided set of composite factories.
	 * @param cfmStream A standard property file containing mapping between
	 * element name and CompositeFactory class (with public default constructor)
	 */
	public XWTBuilder(ClassLoader cl, InputStream cfmStream) throws XWTException {
        this.classLoader = cl;
        this.objectRegistry = new SimpleObjectRegistry();
		this.factoryRegistry = new DefaultSWTFactoryRegistry();
        XWTBuilderContext ctx = new Context();
		Properties props = new Properties();
        try {
    		props.load(cfmStream);
    		Iterator iter = props.keySet().iterator();
    		while (iter.hasNext()) {
    			String elementName = (String)iter.next();
    			String factoryClassName = (String)props.get(elementName);
    			AbstractSWTFactory cf = (AbstractSWTFactory)
    				cl.loadClass(factoryClassName).newInstance();
                cf.setBuilderContext(ctx);
    			this.factoryRegistry.setFactory(elementName, cf);
    		}
        } catch (Exception ex) {
            throw new XWTException(ex);
        }
	}

    public ObjectRegistry getObjectRegistry() {
    	return this.objectRegistry;
    }
    
	/**
	 * @param defaultFactory The defaultFactory to set.
	 */
	public void registerDefaultFactory(AbstractSWTFactory defaultFactory) {
		this.defaultFactory = defaultFactory;
	}
	
	public void registerFactory(String elementName, AbstractSWTFactory cf) {
		if (elementName == null) {
			throw new NullPointerException("no element name");
		}
		if (cf == null) {
			throw new NullPointerException("no component factory");
		}
		this.factoryRegistry.setFactory(elementName, cf);
	}

    /** Creates a composite with given parent and XML element */
    public Composite create(Composite parent, InputStream stream) 
        throws XWTException {
        return create(parent, stream, (Map)null);
    }

    /** Creates a composite with given parent and XML element */
    public Composite create(Composite parent, InputStream stream, 
        Object userOsbject) throws XWTException {
    	Map objects = new HashMap();
        objects.put("userObject", userOsbject);
        return create(parent, stream, objects);
    }
    
    /** Creates a composite with given parent and XML element */
    public Composite create(Composite parent, InputStream stream, Map objects) 
        throws XWTException {
        if (stream == null) {
            throw new NullPointerException("no stream");
        }
        try {
			DocumentBuilder db = DBF.newDocumentBuilder();
			Element element = db.parse(stream).getDocumentElement();
			AbstractSWTFactory cf = (AbstractSWTFactory)this.factoryRegistry.getFactory(element);
			cf = (cf == null) ? this.defaultFactory : cf;
			if (cf == null) {
			    throw new XWTException("no factory registered for "+
			        element.getNodeName());
			}
            if (objects != null) {
            	this.objectRegistry.setObjects(objects);
            }
			return (Composite)cf.create(parent, element);
		} catch (Exception ex) {
            throw new XWTException(ex);
		}
    }    
    
	/** Creates a composite with given parent and XML element */
	public Composite create(Composite parent, Element element) 
		throws XWTException {
		if (element == null) {
			throw new NullPointerException("no element");
		}
		AbstractSWTFactory cf = (AbstractSWTFactory)this.factoryRegistry.getFactory(element);
		cf = (cf == null) ? this.defaultFactory : cf;
		if (cf == null) {
			throw new XWTException("no factory registered for "+
				element.getNodeName());
		}
		return (Composite)cf.create(parent, element);
	}
	
    private class Context implements XWTBuilderContext {

        public ClassLoader getClassLoader() {
            return XWTBuilder.this.classLoader;
        }

        public XWTFactoryRegistry getFactoryRegistry() {
    		return XWTBuilder.this.factoryRegistry;
    	}

    	public ObjectRegistry getObjectRegistry() {
            return XWTBuilder.this.objectRegistry;
    	}

    }

    private static class DefaultSWTFactoryRegistry implements XWTFactoryRegistry {

	    private Map factories = new HashMap();
	    
        public AbstractSWTFactory getFactory(Element element) {
            return (AbstractSWTFactory)this.factories.get(element.getNodeName());
        }

        public void setFactory(String elementName, AbstractSWTFactory cf) {
            this.factories.put(elementName, cf);
        }
	}
	
}