
package com.xpn.xwiki.wiked.internal.xwt.cf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Widget;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.XWTException;

/**
 * Layout data factory for layouts, takes care of layout used on parent
 * if not explicitly set, takes in account the parent's layout manager
 */
public class LayoutDataFactory extends AbstractSWTFactory {

    private Map layoutDataClasses;
    
    public LayoutDataFactory() {
        this.layoutDataClasses = new HashMap();
        this.layoutDataClasses.put(RowLayout.class, RowData.class);
        this.layoutDataClasses.put(GridLayout.class, GridData.class);
        this.layoutDataClasses.put(FormLayout.class, FormData.class);
    }
    
    protected Object createObject(Widget parent, Element element) 
    	throws XWTException {
        ClassLoader cl = getClass().getClassLoader();
        Class layoutDataClass = null;
        String layoutDataClassName = element.getAttribute("class");
        if (layoutDataClassName == null || layoutDataClassName.length() == 0) {
            if (parent instanceof Composite) {
                Layout parentLayout = ((Composite)parent).getLayout();
                if (parentLayout != null) {
                    Class plClass = parentLayout.getClass();
                    layoutDataClass = (Class)this.layoutDataClasses.get(plClass);
                }
            } else {
                throw new XWTException("cannot determine layout data for "+
                    parent+", no explicit class defined");
            }
        }
        if (layoutDataClassName != null && layoutDataClassName.length() > 0) {
            try {
                Class layoutClass = (layoutDataClass != null) ? 
                    layoutDataClass : cl.loadClass(layoutDataClassName);
                return instantiate(layoutClass, element);
            } catch (Exception ex) {
                throw new XWTException(ex);
            }
        }
        return null;
    }

	private Object instantiate(Class layoutClass, Element element) 
        throws TransformerException, SecurityException, NoSuchMethodException, 
        IllegalArgumentException, InstantiationException, IllegalAccessException, 
        InvocationTargetException {
        Node node = XPathAPI.selectSingleNode(element, "text()");
        String value = (node != null) ? ((Text)node).getData() : "";
        if (value == null || "".equals(value)) {
        	return layoutClass.newInstance();
        }
        Constructor constructor = layoutClass.getConstructor(new Class[] {
            Integer.TYPE
        });
        if (constructor != null) {
        	int flags = LayoutPropertyReader.getDefault().parse(value);
            return constructor.newInstance(new Object[] { new Integer(flags) });
        }
        constructor = layoutClass.getConstructor(new Class[] { String.class });
        if (constructor != null) {
            return constructor.newInstance(new Object[] { value });
        }
        
        return null;
	}

}