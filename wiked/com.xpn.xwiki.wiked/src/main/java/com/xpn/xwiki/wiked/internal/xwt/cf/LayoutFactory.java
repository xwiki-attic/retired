
package com.xpn.xwiki.wiked.internal.xwt.cf;

import org.eclipse.swt.widgets.Widget;
import org.w3c.dom.Element;

import com.xpn.xwiki.wiked.internal.xwt.AbstractSWTFactory;
import com.xpn.xwiki.wiked.internal.xwt.XWTException;

/**
 * Composite factory for JavaBeans
 */
public class LayoutFactory extends AbstractSWTFactory {

    private SWTPropertyReader reader = SWTPropertyReader.getDefault();
    
    protected Object createObject(Widget parent, Element element) 
    	throws XWTException {
        ClassLoader cl = getClass().getClassLoader();
        String layoutClassName = element.getAttribute("class");
        if (layoutClassName != null && layoutClassName.length() > 0) {
            try {
                Class layoutClass = cl.loadClass(layoutClassName);
                return layoutClass.newInstance();
            } catch (Exception ex) {
                throw new XWTException(ex);
            }
        }
        
        return null;
    }

}